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

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.UserGroupKey;
import dwbh.api.domain.input.AddUserToGroupInput;
import dwbh.api.domain.input.LeaveGroupInput;
import dwbh.api.domain.input.ListUsersGroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.services.UserGroupService;
import dwbh.api.services.internal.checkers.NotPresent;
import dwbh.api.services.internal.checkers.UserCanSeeGroupMembers;
import dwbh.api.services.internal.checkers.UserIsGroupAdmin;
import dwbh.api.services.internal.checkers.UserIsInGroup;
import dwbh.api.services.internal.checkers.UserIsNotInGroup;
import dwbh.api.services.internal.checkers.UserIsNotUniqueGroupAdmin;
import dwbh.api.util.OptionalUtils;
import dwbh.api.util.Result;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;
import javax.transaction.Transactional;

/**
 * Business logic regarding {@link UserGroup} domain
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultUserGroupService implements UserGroupService {

  private final transient GroupRepository groupRepository;
  private final transient UserRepository userRepository;
  private final transient UserGroupRepository userGroupRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @since 0.1.0
   */
  public DefaultUserGroupService(
      GroupRepository groupRepository,
      UserRepository userRepository,
      UserGroupRepository userGroupRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.userGroupRepository = userGroupRepository;
  }

  @Override
  public Result<Boolean> addUserToGroup(AddUserToGroupInput input) {
    Optional<Group> group = groupRepository.findById(input.getGroupId());
    Optional<User> user = userRepository.findByEmail(input.getEmail());

    NotPresent notPresent = new NotPresent();
    UserIsGroupAdmin userIsGroupAdmin = new UserIsGroupAdmin(userGroupRepository);
    UserIsNotInGroup notInGroupChecker = new UserIsNotInGroup(userGroupRepository);

    return Result.<Boolean>create()
        .thenCheck(() -> notPresent.check(group))
        .thenCheck(() -> notPresent.check(user))
        .thenCheck(() -> userIsGroupAdmin.check(input.getCurrentUserId(), input.getGroupId()))
        .thenCheck(() -> notInGroupChecker.check(user.get().getId(), input.getGroupId()))
        .then(() -> addUserToGroupIfSuccess(user, group));
  }

  private Boolean addUserToGroupIfSuccess(Optional<User> user, Optional<Group> group) {
    return OptionalUtils.combine(user, group)
        .into(UserGroup::new)
        .map(userGroupRepository::save)
        .isPresent();
  }

  @Override
  public Iterable<User> listUsersGroup(ListUsersGroupInput input) {
    UserCanSeeGroupMembers visibility = new UserCanSeeGroupMembers(userGroupRepository);
    UUID userId = input.getUserId();
    UUID groupId = input.getGroupId();
    boolean memberListVisible = input.isVisibleMemberList();

    return Result.<Iterable<User>>create()
        .thenCheck(() -> visibility.check(userId, groupId, memberListVisible))
        .then(() -> listUsersGroupIfSuccess(input.getGroupId()))
        .orElseGet(List::of)
        .getSuccess();
  }

  private Iterable<User> listUsersGroupIfSuccess(UUID groupId) {
    Optional<Group> group = groupRepository.findById(groupId);

    return group.map(userRepository::findAllByGroup).orElseGet(List::of);
  }

  @Override
  public Result<Boolean> leaveGroup(LeaveGroupInput input) {
    Optional<User> currentUser = userRepository.findById(input.getCurrentUserId());
    Optional<Group> group = groupRepository.findById(input.getGroupId());
    Optional<UserGroup> userGroup =
        OptionalUtils.combine(currentUser, group)
            .flatmapInto(
                (u, g) -> userGroupRepository.findById(new UserGroupKey(u.getId(), g.getId())));

    UserIsInGroup userIsInGroup = new UserIsInGroup();
    UserIsNotUniqueGroupAdmin notUniqueAdmin = new UserIsNotUniqueGroupAdmin();

    return Result.<Boolean>create()
        .thenCheck(() -> userIsInGroup.check(currentUser, group))
        .thenCheck(() -> notUniqueAdmin.check(userGroup))
        .then(() -> leaveGroupIfSuccess(input));
  }

  private Boolean leaveGroupIfSuccess(LeaveGroupInput input) {
    return userGroupRepository
        .findById(new UserGroupKey(input.getCurrentUserId(), input.getGroupId()))
        .map(
            ug -> {
              userGroupRepository.delete(ug);
              return true;
            })
        .orElse(false);
  }

  @Override
  public boolean isAdmin(UUID userId, UUID groupId) {
    return userGroupRepository
        .findById(new UserGroupKey(userId, groupId))
        .map(UserGroup::isAdmin)
        .orElse(false);
  }
}
