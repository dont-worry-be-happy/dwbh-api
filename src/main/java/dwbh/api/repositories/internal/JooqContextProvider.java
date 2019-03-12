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
package dwbh.api.repositories.internal;

import io.micronaut.context.annotation.Factory;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;

/**
 * Factory generating an instance of {@link DSLContext} to access JOOQ DSL
 *
 * @since 0.1.0
 */
@Factory
public class JooqContextProvider {

  /**
   * Creates a singleton to access JOOQ DSL
   *
   * @param dataSource required to generated jdbc connection
   * @return an instance of {@link DSLContext}
   * @since 0.1.0
   */
  @Singleton
  public DSLContext get(DataSource dataSource) {
    return new DefaultDSLContext(dataSource, SQLDialect.POSTGRES);
  }
}
