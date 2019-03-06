package dwbh.api.repositories;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.repositories.TablesHelper.UsersGroupsTableHelper;
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
public class UserGroupRepository {

  private final transient DSLContext context;

  /**
   * Initializes the repository by setting the JOOQ {@link DSLContext}
   *
   * @param context JOOQ DSL context ({@link DSLContext}
   * @since 0.1.0
   */
  public UserGroupRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Lists groups in which an user is a member
   *
   * @param userId user identifier
   * @return a list of groups in which an user is a member
   * @since 0.1.0
   */
  public List<Group> listGroupsUser(UUID userId) {
    return context
        .select(
            TablesHelper.GroupsTableHelper.ID,
            TablesHelper.GroupsTableHelper.NAME,
            TablesHelper.GroupsTableHelper.VISIBLE_MEMBER_LIST,
            TablesHelper.GroupsTableHelper.ANONYMOUS_VOTE)
        .from(
            TablesHelper.USERS_GROUPS_TABLE
                .join(TablesHelper.GROUPS_TABLE)
                .on(
                    TablesHelper.UsersGroupsTableHelper.GROUP_ID.eq(
                        TablesHelper.GroupsTableHelper.ID)))
        .where(TablesHelper.UsersGroupsTableHelper.USER_ID.eq(userId))
        .fetch(GroupRepository::toGroup);
  }

  /**
   * Lists users on a group
   *
   * @param groupId group identifier
   * @return a list of users that belongs to a group
   * @since 0.1.0
   */
  public List<User> listUsersGroup(UUID groupId) {
    return context
        .select(
            TablesHelper.UsersTableHelper.ID,
            TablesHelper.UsersTableHelper.NAME,
            TablesHelper.UsersTableHelper.EMAIL,
            TablesHelper.UsersTableHelper.PASSWORD,
            TablesHelper.UsersTableHelper.OTP)
        .from(
            TablesHelper.USERS_GROUPS_TABLE
                .join(TablesHelper.USERS_TABLE)
                .on(
                    TablesHelper.UsersGroupsTableHelper.USER_ID.eq(
                        TablesHelper.UsersTableHelper.ID)))
        .where(TablesHelper.UsersGroupsTableHelper.GROUP_ID.eq(groupId))
        .fetch(UserRepository::toUser);
  }

  /**
   * Add an user to a group
   *
   * @param user The user to add
   * @param group The group to add to
   * @param isAdmin indicates if the user will be an admin of the group
   * @since 0.1.0
   */
  public void addUserToGroup(User user, Group group, boolean isAdmin) {
    context
        .insertInto(TablesHelper.USERS_GROUPS_TABLE)
        .set(UsersGroupsTableHelper.GROUP_ID, group.getId())
        .set(UsersGroupsTableHelper.USER_ID, user.getId())
        .set(UsersGroupsTableHelper.IS_ADMIN, isAdmin)
        .execute();
  }
}