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
package patio.voting.graphql;

import static patio.common.graphql.ArgumentUtils.extractPaginationFrom;

import graphql.schema.DataFetchingEnvironment;
import javax.inject.Singleton;
import patio.common.domain.utils.PaginationRequest;
import patio.common.domain.utils.PaginationResult;
import patio.group.domain.Group;
import patio.voting.domain.Vote;
import patio.voting.domain.VotingStats;
import patio.voting.services.VotingStatsService;

/**
 * All related GraphQL operations over the {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class VotingStatsFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient VotingStatsService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service class handling the logic over groups
   * @since 0.1.0
   */
  public VotingStatsFetcher(VotingStatsService service) {
    this.service = service;
  }

  /**
   * Fetches the voting statistics for a group between a time interval
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Vote}
   * @since 0.1.0
   */
  public PaginationResult<VotingStats> getVotingStatsByGroup(DataFetchingEnvironment env) {
    GetStatsByGroupInput input = VotingStatsFetcherUtils.createGetStatsByGroupInput(env);
    PaginationRequest pagination = extractPaginationFrom(env);

    return service.getVotingStatsByGroup(input, pagination);
  }
}