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
package dwbh.api.services;

import dwbh.api.domain.Group;
import dwbh.api.domain.input.GetGroupInput;
import dwbh.api.domain.input.UpsertGroupInput;
import dwbh.api.util.Result;
import java.util.List;
import java.util.UUID;

/**
 * Business logic contracts regarding {@link Group} domain
 *
 * @since 0.1.0
 */
public interface GroupService {

  /**
   * Fetches the list of available groups in the system
   *
   * @return a list of {@link Group} instances
   * @since 0.1.0
   */
  List<Group> listGroups();

  /**
   * Fetches the list of groups in which an user is a member
   *
   * @param userId user identifier
   * @return a list of {@link Group} instances
   * @since 0.1.0
   */
  List<Group> listGroupsUser(UUID userId);

  /**
   * Creates a new Group
   *
   * @param createGroupInput group information
   * @return The created {@link Group}
   * @since 0.1.0
   */
  Group createGroup(UpsertGroupInput createGroupInput);

  /**
   * Updates a new Group
   *
   * @param input group information
   * @return The updated {@link Group}
   * @since 0.1.0
   */
  Result<Group> updateGroup(UpsertGroupInput input);

  /**
   * Get a specific group
   *
   * @param input required data to retrieve a {@link Group}
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  Result<Group> getGroup(GetGroupInput input);
}
