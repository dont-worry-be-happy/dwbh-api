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
package dwbh.api.services.internal.checkers;

import static dwbh.api.util.Check.checkIsTrue;

import dwbh.api.domain.Voting;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.util.UUID;

/**
 * Checks whether a given voting exists or not
 *
 * @since 0.1.0
 */
public class VotingExists {

  private final transient VotingRepository repository;

  /**
   * Constructor receiving access to the underlying data store
   *
   * @param repository an instance of {@link VotingRepository}
   * @since 0.1.0
   */
  public VotingExists(VotingRepository repository) {
    this.repository = repository;
  }

  /**
   * Checks whether a given voting which a user belongs to exists or not
   *
   * @param userId the user's id
   * @param votingId the voting's id
   * @return a failing {@link Result} if the voting doesn't exist
   * @since 0.1.0
   */
  public Check check(UUID userId, UUID votingId) {
    Voting voting = repository.findVotingByUserAndVoting(userId, votingId);

    return checkIsTrue(voting != null, ErrorConstants.NOT_FOUND);
  }
}
