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

import dwbh.api.domain.Group;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.internal.TablesHelper.GroupsTableHelper;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;
import org.jooq.DSLContext;

/**
 * Repository regarding database operations over {@link Group}
 *
 * @since 0.1.0
 */
@Singleton
public class JooqGroupRepository implements GroupRepository {

  private final transient DSLContext context;

  /**
   * Initializes the repository by setting the JOOQ {@link DSLContext}
   *
   * @param context JOOQ DSL context ({@link DSLContext}
   * @since 0.1.0
   */
  public JooqGroupRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Lists all available groups
   *
   * @return a list of available groups
   * @since 0.1.0
   */
  @Override
  public List<Group> listGroups() {
    return context.selectFrom(TablesHelper.GROUPS_TABLE).fetch(RecordMapperHelper::toGroup);
  }

  /**
   * Get a specific group
   *
   * @param groupId group identifier
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  @Override
  public Group getGroup(UUID groupId) {
    return (Group)
        context
            .selectFrom(TablesHelper.GROUPS_TABLE)
            .where(TablesHelper.GroupsTableHelper.ID.eq(groupId))
            .fetchOne(RecordMapperHelper::toGroup);
  }

  /**
   * Creates or Updates a group
   *
   * @param groupId group's id
   * @param name group's name
   * @param visibleMemberList indicates if the group allows the members to see the member list
   * @param anonymousVote indicates if the group allows anonymous votes
   * @param daysOfWeek days of the week when reminders are sent
   * @param time moment of the day when reminders are sent
   * @return The created or updated {@link Group}
   * @since 0.1.0
   */
  @Override
  public Group upsertGroup(
      UUID groupId,
      String name,
      boolean visibleMemberList,
      boolean anonymousVote,
      List<DayOfWeek> daysOfWeek,
      OffsetTime time) {

    context
        .insertInto(
            TablesHelper.GROUPS_TABLE,
            GroupsTableHelper.ID,
            GroupsTableHelper.NAME,
            GroupsTableHelper.VISIBLE_MEMBER_LIST,
            GroupsTableHelper.ANONYMOUS_VOTE,
            GroupsTableHelper.TIME,
            GroupsTableHelper.DAYS_OF_WEEK)
        .values(groupId, name, visibleMemberList, anonymousVote, time, daysOfWeek)
        .onConflict(GroupsTableHelper.ID)
        .doUpdate()
        .set(GroupsTableHelper.NAME, name)
        .set(GroupsTableHelper.VISIBLE_MEMBER_LIST, visibleMemberList)
        .set(GroupsTableHelper.ANONYMOUS_VOTE, anonymousVote)
        .set(GroupsTableHelper.TIME, time)
        .set(GroupsTableHelper.DAYS_OF_WEEK, daysOfWeek)
        .execute();

    return Group.builder()
        .with(group -> group.setName(name))
        .with(group -> group.setId(groupId))
        .with(group -> group.setVisibleMemberList(visibleMemberList))
        .with(group -> group.setAnonymousVote(anonymousVote))
        .with(group -> group.setVotingDays(daysOfWeek))
        .with(group -> group.setVotingTime(time))
        .build();
  }
}
