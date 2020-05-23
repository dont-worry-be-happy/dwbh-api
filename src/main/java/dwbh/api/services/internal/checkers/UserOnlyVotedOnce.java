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

import dwbh.api.domain.User;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.OptionalUtils;
import java.util.Optional;
import patio.common.Result;
import patio.voting.adapters.persistence.entities.VoteEntity;
import patio.voting.adapters.persistence.entities.VotingEntity;
import patio.voting.adapters.persistence.repositories.VoteRepository;

/**
 * Checks that a given user has not voted already.
 *
 * @since 0.1.0
 */
public class UserOnlyVotedOnce {

  private final transient VoteRepository repository;

  /**
   * Constructor receiving the required repository to be able to query the underlying datastore
   *
   * @param repository an instance of {@link VoteRepository}
   * @since 0.1.0
   */
  public UserOnlyVotedOnce(VoteRepository repository) {
    this.repository = repository;
  }

  /**
   * Checks that the user has not voted already or it's an anonymous user
   *
   * @param user the user that could have created the vote
   * @param voting the voting the vote belongs to
   * @return a failing {@link Result} if the user already voted
   * @since 0.1.0
   */
  public Check check(Optional<User> user, Optional<VotingEntity> voting) {
    Optional<VoteEntity> voteFound =
        OptionalUtils.combine(user, voting).flatmapInto(repository::findByCreatedByAndVoting);

    return checkIsTrue(user.isEmpty() || voteFound.isEmpty(), ErrorConstants.USER_ALREADY_VOTE);
  }
}
