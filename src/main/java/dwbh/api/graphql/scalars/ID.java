package dwbh.api.graphql.scalars;

import static dwbh.api.graphql.scalars.ScalarsUtils.*;
import static dwbh.api.services.internal.FunctionsUtils.safely;

import graphql.language.StringValue;
import graphql.schema.*;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Represents the serialization/deserialization operations over the scalar type ID
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.ShortClassName")
public class ID extends GraphQLScalarType {

  private static final Function<String, String> REMOVE_SIMPLE_QUOTES =
      (source) -> source.replaceAll("'", "");

  /**
   * Initializes operations
   *
   * @since 0.1.0
   */
  public ID() {
    super(
        "ID",
        "Represents the type of any entity's identifier",
        new Coercing() {
          @Override
          public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
            UUID id = (UUID) dataFetcherResult;

            return id.toString();
          }

          @Override
          public Object parseValue(Object input) throws CoercingParseValueException {
            return Optional.ofNullable(input)
                .map(Object::toString)
                .map(REMOVE_SIMPLE_QUOTES)
                .flatMap(safely(UUID::fromString, (th) -> throwValueException("ID", th)))
                .orElse(null);
          }

          @Override
          public Object parseLiteral(Object input) throws CoercingParseLiteralException {
            return Optional.ofNullable(input)
                .filter(o -> o instanceof StringValue)
                .map(StringValue.class::cast)
                .map(StringValue::getValue)
                .map(REMOVE_SIMPLE_QUOTES)
                .flatMap(safely(UUID::fromString, (th) -> throwLiteralException("ID", th)))
                .orElse(null);
          }
        });
  }
}
