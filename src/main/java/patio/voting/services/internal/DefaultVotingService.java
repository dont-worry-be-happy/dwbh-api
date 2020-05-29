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
package patio.voting.services.internal;

import static patio.infrastructure.utils.OptionalUtils.combine;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.common.domain.utils.NotPresent;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.domain.UserGroupKey;
import patio.group.repositories.GroupRepository;
import patio.group.repositories.UserGroupRepository;
import patio.group.services.internal.UserIsInGroup;
import patio.infrastructure.utils.ErrorConstants;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.voting.domain.Vote;
import patio.voting.domain.Voting;
import patio.voting.graphql.CreateVoteInput;
import patio.voting.graphql.CreateVotingInput;
import patio.voting.graphql.GetVotingInput;
import patio.voting.graphql.ListVotingsGroupInput;
import patio.voting.graphql.UserVotesInGroupInput;
import patio.voting.repositories.VoteRepository;
import patio.voting.repositories.VotingRepository;
import patio.voting.services.VotingService;

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
  public Result<Voting> createVoting(CreateVotingInput input) {
    var userGroupKey = new UserGroupKey(input.getUserId(), input.getGroupId());
    var userGroupOptional = userGroupRepository.findById(userGroupKey);

    NotPresent notPresent = new NotPresent();

    return Result.<Voting>create()
        .thenCheck(() -> notPresent.check(userGroupOptional, ErrorConstants.USER_NOT_IN_GROUP))
        .then(() -> createVotingIfSuccess(userGroupOptional));
  }

  private Voting createVotingIfSuccess(Optional<UserGroup> userGroup) {
    Optional<Voting> voting =
        userGroup.map(
            (UserGroup ug) -> {
              return Voting.newBuilder()
                  .with(v -> v.setGroup(ug.getGroup()))
                  .with(v -> v.setCreatedBy(ug.getUser()))
                  .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now()))
                  .build();
            });

    return voting.map(votingRepository::save).orElse(null);
  }

  @Override
  public Result<Vote> createVote(CreateVoteInput input) {
    Optional<User> user = userRepository.findById(input.getUserId());
    Optional<Voting> voting = votingRepository.findById(input.getVotingId());
    Optional<Group> group = voting.map(Voting::getGroup);
    Boolean isGroupAnonymous = group.map(Group::isAnonymousVote).orElse(false);

    var voteScoreBoundaries = new VoteScoreBoundaries();
    var userOnlyVotedOnce = new UserOnlyVotedOnce(voteRepository);
    var votingHasExpired = new VotingHasExpired();
    var notPresent = new NotPresent();
    var userIsInGroup = new UserIsInGroup();
    var anonymousAllowed = new VoteAnonymousAllowedInGroup();

    return Result.<Vote>create()
        .thenCheck(() -> voteScoreBoundaries.check(input.getScore()))
        .thenCheck(() -> userOnlyVotedOnce.check(user, voting))
        .thenCheck(() -> votingHasExpired.check(voting))
        .thenCheck(() -> notPresent.check(group))
        .thenCheck(() -> userIsInGroup.check(user, group))
        .thenCheck(() -> anonymousAllowed.check(input.isAnonymous(), isGroupAnonymous))
        .then(createVote(voting, user, input))
        .sideEffect(v -> updateVotingAverage(v.getVoting()));
  }

  private Supplier<Vote> createVote(
      Optional<Voting> voting, Optional<User> user, CreateVoteInput input) {
    return () ->
        voting
            .map(
                (Voting slot) ->
                    Vote.newBuilder()
                        .with(v -> v.setVoting(slot))
                        .with(v -> v.setCreatedBy(user.orElse(null)))
                        .with(v -> v.setComment(input.getComment()))
                        .with(v -> v.setScore(input.getScore()))
                        .build())
            .map(voteRepository::save)
            .orElse(null);
  }

  private void updateVotingAverage(Voting voting) {
    voting.setAverage(voteRepository.findAvgScoreByVoting(voting));
    votingRepository.update(voting);
  }

  @Override
  public List<Voting> listVotingsGroup(ListVotingsGroupInput input) {
    Optional<Group> group = groupRepository.findById(input.getGroupId());
    OffsetDateTime fromDate = input.getStartDate();
    OffsetDateTime toDate = input.getEndDate();

    return group.stream()
        .flatMap(
            g -> votingRepository.findAllByGroupAndCreatedAtDateTimeBetween(g, fromDate, toDate))
        .collect(Collectors.toList());
  }

  @Override
  public List<Vote> listVotesVoting(UUID votingId) {
    return voteRepository.findAllByVotingOrderByUser(votingId).collect(Collectors.toList());
  }

  @Override
  public Result<Voting> getVoting(GetVotingInput input) {
    Optional<User> user = userRepository.findById(input.getCurrentUserId());
    Optional<UUID> votingId = votingRepository.findById(input.getVotingId()).map(Voting::getId);
    Optional<Voting> votingFound =
        combine(votingId, user).flatmapInto(votingRepository::findByIdAndVotingUser);

    NotPresent notPresent = new NotPresent();

    return Result.<Voting>create()
        .thenCheck(() -> notPresent.check(votingFound))
        .then(votingFound::get);
  }

  @Override
  public Result<List<Vote>> listUserVotesInGroup(UserVotesInGroupInput input) {
    Optional<User> currentUser = userRepository.findById(input.getCurrentUserId());
    Optional<User> user = userRepository.findById(input.getUserId());
    Optional<Group> group = groupRepository.findById(input.getGroupId());

    UserIsInGroup userIsInGroup = new UserIsInGroup();

    return Result.<List<Vote>>create()
        .thenCheck(() -> userIsInGroup.check(currentUser, group))
        .thenCheck(() -> userIsInGroup.check(user, group))
        .then(() -> listUserVotesInGroupIfSuccess(input));
  }

  private List<Vote> listUserVotesInGroupIfSuccess(UserVotesInGroupInput input) {
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