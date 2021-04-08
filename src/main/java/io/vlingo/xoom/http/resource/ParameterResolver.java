package io.vlingo.xoom.http.resource;

import io.vlingo.xoom.http.media.ContentMediaType;
import io.vlingo.xoom.http.Header;
import io.vlingo.xoom.http.Request;
import io.vlingo.xoom.http.RequestHeader;

import java.util.function.BiFunction;

class ParameterResolver<T> {
  public final Type type;
  public final Class<T> paramClass;
  private final BiFunction<Request, Action.MappedParameters, T> resolver;

  private ParameterResolver(final Type type, final Class<T> paramClass, final BiFunction<Request, Action.MappedParameters, T> resolver) {
    this.type = type;
    this.paramClass = paramClass;
    this.resolver = resolver;
  }

  @SuppressWarnings("unchecked")
  public static <T> ParameterResolver<T> path(final int position, final Class<T> paramClass) {
    return new ParameterResolver<>(Type.PATH, paramClass, (request, mappedParameters) -> {
      Object value = mappedParameters.mapped.get(position).value;
      if (paramClass.isInstance(value)) {
        return (T) value;
      }
      throw new IllegalArgumentException("Value " + value + " is of mimeType " + mappedParameters.mapped.get(position).type + " instead of " + paramClass.getSimpleName());
    });
  }

  public static <T> ParameterResolver<T> body(final Class<T> bodyClass) {
      return body(bodyClass, DefaultMediaTypeMapper.instance());
  }

  public static <T> ParameterResolver<T> body(final Class<T> bodyClass, final Mapper mapper) {
    return new ParameterResolver<>(Type.BODY, bodyClass, ((request, mappedParameters) ->
      mapper.from(request.body.toString(), bodyClass)));
  }

  public static <T> ParameterResolver<T> body(final Class<T> bodyClass, final MediaTypeMapper mediaTypeMapper) {
    return new ParameterResolver<T>(Type.BODY, bodyClass, ((request, mappedParameters) -> {
      // This is a fall-back when content-type not provided for backwards compat for curl/cmd line users
      String assumedBodyContentType = ContentMediaType.Json().toString();
      String bodyMediaType = request.headerValueOr(RequestHeader.ContentType, assumedBodyContentType);
      return mediaTypeMapper.from(request.body.toString(), ContentMediaType.parseFromDescriptor(bodyMediaType), bodyClass);
    }));
  }

  public static ParameterResolver<Header> header(final String headerName) {
    return new ParameterResolver<>(Type.HEADER, Header.class, ((request, mappedParameters) ->
      request.headerOf(headerName)));
  }

  public static ParameterResolver<String> query(final String name) {
    return query(name, String.class, null);
  }

  public static <T> ParameterResolver<T> query(final String name, final Class<T> type) {
    return query(name, type, null);
  }

  public static <T> ParameterResolver<T> query(final String name, final Class<T> type, final T defaultValue) {
    return new ParameterResolver<>(Type.QUERY, type, (((request, mappedParameters) -> {
      String value;
      try {
        value = request.queryParameters().valuesOf(name).get(0);
      } catch (IllegalArgumentException | NullPointerException e) {
        return  defaultValue;
      }
      if (type == Integer.class) {
        return type.cast(Integer.valueOf(value));
      } else if (type == String.class) {
        return type.cast(value);
      } else if (type == Float.class) {
        return type.cast(Float.valueOf(value));
      } else if (type == Long.class) {
        return type.cast(Integer.valueOf(value));
      } else if (type == Boolean.class) {
        return type.cast(Boolean.valueOf(value));
      } else if (type == Short.class) {
        return type.cast(Short.valueOf(value));
      } else if (type == Byte.class) {
        return type.cast(Byte.valueOf(value));
      }
      throw new IllegalArgumentException("unknown mimeType " + type.getSimpleName());
    })));
  }

  public T apply(final Request request, final Action.MappedParameters mappedParameters) {
    return resolver.apply(request, mappedParameters);
  }

  enum Type {
    PATH,
    BODY,
    HEADER,
    QUERY
  }
}
