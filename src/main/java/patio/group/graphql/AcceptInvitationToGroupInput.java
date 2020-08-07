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
package patio.group.graphql;

import java.util.UUID;

/**
 * AddUserToGroupInput input. It contains the ids for a user and a group
 *
 * @since 0.1.0
 */
public class AcceptInvitationToGroupInput {
  private final UUID currentUserId;
  private final String otp;

  /**
   * Returns the group invitation's otp
   *
   * @return the email of the user
   * @since 0.1.0
   */
  public String getOtp() {
    return otp;
  }

  /**
   * Returns the id of the current user
   *
   * @return the id of the current user
   * @since 0.1.0
   */
  public UUID getCurrentUserId() {
    return currentUserId;
  }

  /**
   * Initializes the input with the user email and the group id
   *
   * @param currentUserId the id of the current user
   * @param otp the group invitation's otp
   * @since 0.1.0
   */
  public AcceptInvitationToGroupInput(UUID currentUserId, String otp) {
    this.currentUserId = currentUserId;
    this.otp = otp;
  }
}
