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
package dwbh.api.domain;

import dwbh.api.util.Builder;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Represents the users of dwbh
 *
 * @since 0.1.0
 */
public final class User {
  private UUID id;
  private String name;
  private String email;
  private String password;
  private String otp;

  private User() {
    /* empty */
  }

  /**
   * Creates a builder to create instances of type {@link User}
   *
   * @return a builder to create instances of type {@link User}
   * @since 0.1.0
   */
  public static Builder<User> builder() {
    return Builder.build(User::new);
  }

  /**
   * Gets name.
   *
   * @return Value of name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets otp.
   *
   * @return Value of otp.
   */
  public String getOtp() {
    return otp;
  }

  /**
   * Sets new password.
   *
   * @param password New value of password.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Sets new email.
   *
   * @param email New value of email.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Sets new name.
   *
   * @param name New value of name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets password.
   *
   * @return Value of password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Gets email.
   *
   * @return Value of email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets new otp.
   *
   * @param otp New value of otp.
   */
  public void setOtp(String otp) {
    this.otp = otp;
  }

  /**
   * Gets id.
   *
   * @return Value of id.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Generates a user's md5 hash which can be used for third party services such as Gravatar.
   *
   * @return gets a md5 hash from the user's email
   * @since 0.1.0
   */
  public String getHash() {
    return Optional.ofNullable(this.email)
        .map(String::trim)
        .map(String::toLowerCase)
        .map(DigestUtils::md5Hex)
        .orElse("");
  }

  /**
   * Sets new id.
   *
   * @param id New value of id.
   */
  public void setId(UUID id) {
    this.id = id;
  }
}
