/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.user.repositories;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.user.domain.GroupMember;
import patio.user.domain.User;

/** All database actions related to {@link User} entity */
public interface UserRepository extends PageableRepository<User, UUID> {

  /**
   * Gets a list of {@link User} instances by their ids sorted by these ids
   *
   * @param ids list of ids of the {@link User} instances to get
   * @return a list of {@link User} instances
   */
  List<User> findAllByIdInList(List<UUID> ids);

  /**
   * Finds any member (or potential member) for a given {@link Group}
   *
   * @param group the {@link Group} the users belong to
   * @return a list of userGroups {@link UserGroup}
   */
  @Query("SELECT ug.user FROM UserGroup ug WHERE ug.group = :group")
  List<User> findAllByGroup(Group group);

  /**
   * Finds any member (or potential member) for a {@link Group} returning GroupMembers objects
   *
   * @param group the {@link Group} the users belong to
   * @return a list of {@link GroupMember}
   */
  @Query(
      "SELECT new patio.user.domain.GroupMember(u.id, u.name, u.email, u.registrationPending, ug.acceptancePending) "
          + "FROM UserGroup ug JOIN ug.user u "
          + "WHERE ug.group = :group ")
  List<GroupMember> findAllGroupMembersByGroup(Group group);

  /**
   * Tries to find a given user in the database and if it's not there, then the {@link User} is
   * persisted
   *
   * @param user instance of the user to find/persist
   * @return the persisted instance of {@link User}
   */
  Optional<User> findByEmailOrCreate(User user);

  /**
   * Gets a persisted {@link User} by its email
   *
   * @param email the user's email
   * @return an {@link Optional} of the {@link User}
   */
  Optional<User> findByEmail(String email);

  /**
   * Gets a persisted {@link User} by its email
   *
   * @param emailList the user's email list
   * @return an {@link Optional} of the {@link User}
   */
  Stream<User> findAllByEmailInList(List<String> emailList);

  /**
   * Gets a persisted {@link User} by its OTP
   *
   * @param otpCode the user's OTP code
   * @return an {@link Optional} of the {@link User}
   */
  Optional<User> findByOtp(String otpCode);
}
