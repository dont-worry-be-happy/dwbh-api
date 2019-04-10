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
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a given user's vote
 *
 * @since 0.1.0
 */
public final class Vote {
  private transient UUID id;
  private transient Voting voting;
  private transient User createdBy;
  private transient OffsetDateTime createdAt;
  private transient String comment;
  private transient Integer score;

  private Vote() {
    /* empty */
  }

  /**
   * A builder to build an instance of type {@link Vote}
   *
   * @return an instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder<Vote> newBuilder() {
    return Builder.build(Vote::new);
  }

  /**
   * Returns vote's id
   *
   * @return vote's id
   * @since 0.1.0
   */
  public UUID getId() {
    return id;
  }

  /**
   * Sets vote's id
   *
   * @param id the vote's identifier
   * @since 0.1.0
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Return vote's voting record
   *
   * @return an instance of type {@link Voting}
   * @since 0.1.0
   */
  public Voting getVoting() {
    return voting;
  }

  /**
   * Sets the voting this vote belongs to
   *
   * @param voting the {@link Voting} this vote belongs to
   * @since 0.1.0
   */
  public void setVoting(Voting voting) {
    this.voting = voting;
  }

  /**
   * Returns the user who voted
   *
   * @return an instance of type {@link User}
   * @since 0.1.0
   */
  public User getCreatedBy() {
    return createdBy;
  }

  /**
   * Sets the creator of the vote
   *
   * @param createdBy who created the vote
   * @since 0.1.0
   */
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Returns the moment the user voted
   *
   * @return an instance of type {@link OffsetDateTime}
   * @since 0.1.0
   */
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets when the vote has been created
   *
   * @param createdAt when the vote's been created
   * @since 0.1.0
   */
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Returns any comment that the user wanted to comment about the vote
   *
   * @return a simple {@link String} with the comment
   * @since 0.1.0
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets any extra comment the user may want to add
   *
   * @param comment a string with any comment
   * @since 0.1.0
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Returns the score that the user wanted to vote
   *
   * @return a simple {@link Integer} with the score
   * @since 0.1.0
   */
  public Integer getScore() {
    return score;
  }

  /**
   * Sets the vote score
   *
   * @param score sets the vote's score
   * @since 0.1.0
   */
  public void setScore(Integer score) {
    this.score = score;
  }
}
