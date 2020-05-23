/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package dwbh.api.services.internal;

import static dwbh.api.util.OptionalUtils.combine;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.UserGroupKey;
import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.domain.input.GetVotingInput;
import dwbh.api.domain.input.ListVotingsGroupInput;
import dwbh.api.domain.input.UserVotesInGroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.services.VotingService;
import dwbh.api.services.internal.checkers.NotPresent;
import dwbh.api.services.internal.checkers.UserIsInGroup;
import dwbh.api.services.internal.checkers.UserOnlyVotedOnce;
import dwbh.api.services.internal.checkers.VoteAnonymousAllowedInGroup;
import dwbh.api.services.internal.checkers.VoteScoreBoundaries;
import dwbh.api.services.internal.checkers.VotingHasExpired;
import dwbh.api.util.ErrorConstants;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.common.Result;
import patio.voting.adapters.persistence.entities.VoteEntity;
import patio.voting.adapters.persistence.entities.VotingEntity;
import patio.voting.adapters.persistence.repositories.VoteRepository;
import patio.voting.adapters.persistence.repositories.VotingRepository;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultVotingService implements VotingService {

  private final transient VotingRepository votingRepository;
  private final transient VoteRepository voteRepository;
  private final transient UserGroupRepository userGroupRepository;
  private final transient UserRepository userRepository;
  private final transient GroupRepository groupRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param votingRepository an instance of {@link VotingRepository}
   * @param voteRepository an instance of {@link VoteRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param userRepository an instance of {@link UserRepository}
   * @param groupRepository an instance of {@link GroupRepository}
   * @since 0.1.0
   */
  public DefaultVotingService(
      VotingRepository votingRepository,
      VoteRepository voteRepository,
      UserGroupRepository userGroupRepository,
      UserRepository userRepository,
      GroupRepository groupRepository) {
    this.votingRepository = votingRepository;
    this.voteRepository = voteRepository;
    this.userGroupRepository = userGroupRepository;
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public Result<VotingEntity> createVoting(CreateVotingInput input) {
    var userGroupKey = new UserGroupKey(input.getUserId(), input.getGroupId());
    var userGroupOptional = userGroupRepository.findById(userGroupKey);

    NotPresent notPresent = new NotPresent();

    return Result.<VotingEntity>create()
        .thenCheck(() -> notPresent.check(userGroupOptional, ErrorConstants.USER_NOT_IN_GROUP))
        .then(() -> createVotingIfSuccess(userGroupOptional));
  }

  private VotingEntity createVotingIfSuccess(Optional<UserGroup> userGroup) {
    Optional<VotingEntity> voting =
        userGroup.map(
            (UserGroup ug) -> {
              return VotingEntity.newBuilder()
                  .with(v -> v.setGroup(ug.getGroup()))
                  .with(v -> v.setCreatedBy(ug.getUser()))
                  .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now()))
                  .build();
            });

    return voting.map(votingRepository::save).orElse(null);
  }

  @Override
  public Result<VoteEntity> createVote(CreateVoteInput input) {
    Optional<User> user = userRepository.findById(input.getUserId());
    Optional<VotingEntity> voting = votingRepository.findById(input.getVotingId());
    Optional<Group> group = voting.map(VotingEntity::getGroup);
    Boolean isGroupAnonymous = group.map(Group::isAnonymousVote).orElse(false);

    var voteScoreBoundaries = new VoteScoreBoundaries();
    var userOnlyVotedOnce = new UserOnlyVotedOnce(voteRepository);
    var votingHasExpired = new VotingHasExpired();
    var notPresent = new NotPresent();
    var userIsInGroup = new UserIsInGroup();
    var anonymousAllowed = new VoteAnonymousAllowedInGroup();

    return Result.<VoteEntity>create()
        .thenCheck(() -> voteScoreBoundaries.check(input.getScore()))
        .thenCheck(() -> userOnlyVotedOnce.check(user, voting))
        .thenCheck(() -> votingHasExpired.check(voting))
        .thenCheck(() -> notPresent.check(group))
        .thenCheck(() -> userIsInGroup.check(user, group))
        .thenCheck(() -> anonymousAllowed.check(input.isAnonymous(), isGroupAnonymous))
        .then(createVote(voting, user, input))
        .sideEffect(v -> updateVotingAverage(v.getVoting()));
  }

  private Supplier<VoteEntity> createVote(
      Optional<VotingEntity> voting, Optional<User> user, CreateVoteInput input) {
    return () ->
        voting
            .map(
                (VotingEntity slot) ->
                    VoteEntity.newBuilder()
                        .with(v -> v.setVoting(slot))
                        .with(v -> v.setCreatedBy(user.orElse(null)))
                        .with(v -> v.setComment(input.getComment()))
                        .with(v -> v.setScore(input.getScore()))
                        .build())
            .map(voteRepository::save)
            .orElse(null);
  }

  private void updateVotingAverage(VotingEntity voting) {
    voting.setAverage(voteRepository.findAvgScoreByVoting(voting));
    votingRepository.update(voting);
  }

  @Override
  public List<VotingEntity> listVotingsGroup(ListVotingsGroupInput input) {
    Optional<Group> group = groupRepository.findById(input.getGroupId());
    OffsetDateTime fromDate = input.getStartDate();
    OffsetDateTime toDate = input.getEndDate();

    return group.stream()
        .flatMap(
            g -> votingRepository.findAllByGroupAndCreatedAtDateTimeBetween(g, fromDate, toDate))
        .collect(Collectors.toList());
  }

  @Override
  public List<VoteEntity> listVotesVoting(UUID votingId) {
    return voteRepository.findAllByVotingOrderByUser(votingId).collect(Collectors.toList());
  }

  @Override
  public Result<VotingEntity> getVoting(GetVotingInput input) {
    Optional<User> user = userRepository.findById(input.getCurrentUserId());
    Optional<UUID> votingId =
        votingRepository.findById(input.getVotingId()).map(VotingEntity::getId);
    Optional<VotingEntity> votingFound =
        combine(votingId, user).flatmapInto(votingRepository::findByIdAndVotingUser);

    NotPresent notPresent = new NotPresent();

    return Result.<VotingEntity>create()
        .thenCheck(() -> notPresent.check(votingFound))
        .then(votingFound::get);
  }

  @Override
  public Result<List<VoteEntity>> listUserVotesInGroup(UserVotesInGroupInput input) {
    Optional<User> currentUser = userRepository.findById(input.getCurrentUserId());
    Optional<User> user = userRepository.findById(input.getUserId());
    Optional<Group> group = groupRepository.findById(input.getGroupId());

    UserIsInGroup userIsInGroup = new UserIsInGroup();

    return Result.<List<VoteEntity>>create()
        .thenCheck(() -> userIsInGroup.check(currentUser, group))
        .thenCheck(() -> userIsInGroup.check(user, group))
        .then(() -> listUserVotesInGroupIfSuccess(input));
  }

  private List<VoteEntity> listUserVotesInGroupIfSuccess(UserVotesInGroupInput input) {
    Optional<User> user = userRepository.findById(input.getUserId());
    Optional<Group> group = groupRepository.findById(input.getGroupId());
    OffsetDateTime fromDate = input.getStartDateTime();
    OffsetDateTime toDate = input.getEndDateTime();

    return combine(user, group)
        .into(
            (u, g) -> {
              return voteRepository.findAllByUserAndGroupAndCreatedAtBetween(
                  u, g, fromDate, toDate);
            })
        .stream()
        .flatMap(voteStream -> voteStream)
        .collect(Collectors.toList());
  }
}