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
package dwbh.api.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dwbh.api.domain.User;
import dwbh.api.fixtures.Fixtures;
import io.micronaut.test.annotation.MicronautTest;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Tests DATABASE integration regarding {@link User} persistence
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
class UserRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient UserRepository repository;

  @Inject transient Fixtures fixtures;

  @BeforeEach
  void loadFixtures() {
    flyway.migrate();
  }

  @AfterEach
  void cleanFixtures() {
    flyway.clean();
  }

  @Test
  void testListUsers() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsers.sql");

    // when: asking for the list of users
    List<User> userList = repository.listUsers();

    // then: check there're the expected number of users
    assertEquals(userList.size(), 5);
  }

  @Test
  void testGetUser() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsers.sql");

    // when: asking for a user
    User user = repository.getUser(UUID.fromString("1998c588-d93b-4db6-92e2-a9dbb4cf03b5"));

    // then: check the user is retrieved
    assertEquals(user.getName(), "Steve Rogers");
  }

  @Test
  void testGetUserByEmail() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsers.sql");

    // when: asking for a user
    User user = repository.getUserByEmail("bgrim@email.com");

    // then: check the user is retrieved
    assertEquals(user.getName(), "Ben Grim");
  }

  @Test
  void testFindByEmail() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testFindByEmail.sql");

    // and: taking a reference user
    User refUser = repository.listUsers().get(0);

    // when: searching the user by email
    User userByEmail = repository.findByEmail(refUser.getEmail());

    // then: we should get the same values
    assertEquals(refUser.getName(), userByEmail.getName());
    assertEquals(refUser.getEmail(), userByEmail.getEmail());
  }
}
