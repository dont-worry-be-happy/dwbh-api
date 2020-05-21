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
package dwbh.api.graphql.fetchers;

import dwbh.api.domain.User;
import dwbh.api.domain.input.ChangePasswordInput;
import dwbh.api.domain.input.LoginInput;
import dwbh.api.graphql.Context;
import graphql.schema.DataFetchingEnvironment;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * SecurityFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class SecurityFetcherUtils {

  private SecurityFetcherUtils() {
    /* empty */
  }

  /**
   * Creates a {@link LoginInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link LoginInput}
   * @since 0.1.0
   */
  /* default */ static LoginInput login(DataFetchingEnvironment environment) {
    String email = environment.getArgument("email");
    String password = environment.getArgument("password");

    return new LoginInput(email, password);
  }

  /**
   * Creates a {@link ChangePasswordInput} from the data coming from the {@link
   * DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link ChangePasswordInput}
   * @since 0.1.0
   */
  /* default */ static ChangePasswordInput changePassword(DataFetchingEnvironment environment) {
    Context ctx = environment.getContext();
    User currentUser = ctx.getAuthenticatedUser();
    String password = environment.getArgument("password");

    return ChangePasswordInput.newBuilder()
        .withUserId(currentUser.getId())
        .withPassword(password)
        .build();
  }
}
