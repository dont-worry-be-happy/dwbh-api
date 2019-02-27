package dwbh.api.repositories;

import java.util.UUID;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

/**
 * Class for model the jooq definitions for Tables
 *
 * @since 0.1.0
 */
public class TablesHelper {
  public static final Table USERS_TABLE = DSL.table("users");
  public static final Table GROUPS_TABLE = DSL.table("groups");
  public static final Table USERS_GROUPS_TABLE = DSL.table("users_groups");
  /**
   * Inner class for model fields for User Table
   *
   * @since 0.1.0
   */
  public static class UsersTableHelper {
    public static final Field<UUID> UUID = DSL.field("uuid", UUID.class);
    public static final Field<String> NAME = DSL.field("name", String.class);
    public static final Field<String> EMAIL = DSL.field("email", String.class);
    public static final Field<String> PASSWORD = DSL.field("password", String.class);
    public static final Field<String> OTP = DSL.field("otp", String.class);
  }

  /**
   * Inner class for model fields for Group Table
   *
   * @since 0.1.0
   */
  public static class GroupsTableHelper {
    public static final Field<UUID> UUID = DSL.field("uuid", UUID.class);
    public static final Field<String> NAME = DSL.field("name", String.class);
    public static final Field<Boolean> VISIBLE_MEMBER_LIST =
        DSL.field("visible_member_list", Boolean.class);
    public static final Field<Boolean> ANONYMOUS_VOTE = DSL.field("anonymous_vote", Boolean.class);
  }

  /**
   * Inner class for model fields for UsersGroups Table
   *
   * @since 0.1.0
   */
  public static class UsersGroupsTableHelper {
    public static final Field<UUID> GROUP_UUID = DSL.field("group_uuid", UUID.class);
    public static final Field<UUID> USER_UUID = DSL.field("user_uuid", UUID.class);
  }
}