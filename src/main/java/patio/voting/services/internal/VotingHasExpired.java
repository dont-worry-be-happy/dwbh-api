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

import static patio.common.domain.utils.Check.checkIsFalse;
import static patio.infrastructure.utils.ErrorConstants.VOTING_HAS_EXPIRED;

import java.time.OffsetDateTime;
import java.util.Optional;
import patio.common.domain.utils.Check;
import patio.common.domain.utils.Result;
import patio.voting.domain.Voting;

/**
 * Checks whether a voting has expired or not
 *
 * @since 0.1.0
 */
public class VotingHasExpired {

  /**
   * Checks if the voting has expired or not
   *
   * @param voting the voting
   * @return a failing {@link Result} if the voting has expired
   */
  public Check check(Optional<Voting> voting) {
    boolean hasExpired =
        voting
            .map(Voting::getCreatedAtDateTime)
            .map(createdAt -> createdAt.plusDays(1).isBefore(OffsetDateTime.now()))
            .orElse(true);

    return checkIsFalse(hasExpired, VOTING_HAS_EXPIRED);
  }
}