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
package dwbh.api.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents a result that may contain whether a successful content or a failure content (errors)
 * or both. If it's sure the result should only contain a successful response you can use the type
 * directly, otherwise the use of this type is encouraged.
 *
 * @since 0.1.0
 */
public class Result<T> {

  private final T success;
  private final List<Error> errorList;

  /**
   * Initializes the result type with the successful and failing possible results
   *
   * @param success the successful outcome
   * @param errorList a list with possible errors
   * @see Error
   * @since 0.1.0
   */
  public Result(T success, List<Error> errorList) {
    this.success = success;
    this.errorList = errorList;
  }

  /**
   * Returns successful output
   *
   * @return the success content
   * @since 0.1.0
   */
  public T getSuccess() {
    return success;
  }

  /**
   * Returns the list of possible failures
   *
   * @return the list of errors ({@link Error})
   * @since 0.1.0
   */
  public List<Error> getErrorList() {
    return errorList;
  }

  /**
   * Whether the instance is a success
   *
   * @return true if the result doesn't have any error
   * @since 0.1.0
   */
  public boolean isSuccess() {
    return !this.hasErrors();
  }

  /**
   * Whether the instance has errors or not
   *
   * @return true if the result does have errors
   * @since 0.1.0
   */
  public boolean hasErrors() {
    return Optional.ofNullable(this.errorList).map(list -> !list.isEmpty()).orElse(false);
  }

  /**
   * This method creates a possible failing {@link Result} from a given {@link Check}. Once you have
   * the result you can chain calls to {@link Result#thenCheck(Supplier)} if you want to check more
   * conditions or simply call {@link Result#then(Supplier)} if there're no more checkers and you
   * want to end the execution with the final success result.
   *
   * @param <U> the expected result type
   * @param check check to start a {@link Result} chain
   * @return a partial and possibly failing {@link Result}
   * @since 0.1.0
   */
  public static <U> Result<U> fromCheck(Check check) {
    return Result.result(null).toResult(check);
  }

  /**
   * This method is used to concatenate checkers executions. It will short-circuit as soon as any of
   * the checkers returns a failing {@link Result}
   *
   * @param supplier a function providing the result of a {@link Check}
   * @return a result of the {@link Check} execution
   * @since 0.1.0
   */
  public Result<T> thenCheck(Supplier<Check> supplier) {
    return this.hasErrors() ? this : toResult(supplier.get());
  }

  /**
   * Because every checker can define the "check" method in its own terms, this method helps
   * translating the resulting {@link Check} result to a {@link Result} instance.
   *
   * @param <U> the expected result type
   * @param check an instance of {@link Check} to be converted to {@link Result}
   * @return an instance of {@link Result}. It could contain errors if the passed {@link Check}
   *     failed. An empty successful result otherwise
   * @since 0.1.0
   */
  private <U> Result<U> toResult(Check check) {
    Optional<Result<U>> optional =
        Optional.ofNullable(check).filter(Check::hasError).map(Check::getError).map(Result::error);

    return optional.orElse(Result.result(null));
  }

  /**
   * In case the current result has errors {@link Result} it will provide an alternative outcome
   *
   * @param supplier an execution returning an alternative {@link Result}
   * @return the passed supplier's result if the current result has errors or the current result if
   *     it's a success
   * @since 0.1.0
   */
  public Result<T> orElseGet(Supplier<T> supplier) {
    return this.hasErrors() ? Result.result(supplier.get()) : this;
  }

  /**
   * If the current instance is a success but we want to provide an alternative {@link Result}. This
   * is useful at the end of a series of checkers. If all of them passed then we can provide the
   * expected business logic output.
   *
   * @param supplier a supplier returning an alternative success result
   * @return the supplier's result if the current result is successful. The current result
   *     otherwise.
   * @since 0.1.0
   */
  public Result<T> then(Supplier<T> supplier) {
    return this.isSuccess() ? Result.result(supplier.get()) : this;
  }

  /**
   * Use this method when creating a result with no errors
   *
   * @param success successful outcome
   * @param <T> the success type
   * @return a successful {@link Result} instance with no errors
   * @since 0.1.0
   */
  public static <T> Result<T> result(T success) {
    return new Result<T>(success, List.of());
  }

  /**
   * Use this method when creating a result with only one {@link Error}
   *
   * @param error the failing output of type {@link Error}
   * @param <T> the success type (doesnt apply here)
   * @return an instance of {@link Result} containing one error
   * @since 0.1.0
   */
  public static <T> Result<T> error(Error error) {
    return new Result<T>(null, List.of(error));
  }

  /**
   * Shortcut for creating a {@link Result} with only one {@link Error}
   *
   * @param code the error's code
   * @param message the error's message
   * @param <T> the success type (doesnt apply here)
   * @return an instance of {@link Result} representing a failing result
   * @since 0.1.0
   */
  public static <T> Result<T> error(String code, String message) {
    return new Result<T>(null, List.of(new Error(code, message)));
  }
}
