package io.vlingo.xoom.http.resource;

import io.vlingo.xoom.http.media.ContentMediaType;

import java.util.*;

public class MediaTypeMapper {

  private final Map<ContentMediaType, Mapper> mappersByContentType;

  public MediaTypeMapper(Map<ContentMediaType, Mapper> mappersByContentType) {
    this.mappersByContentType = mappersByContentType;
  }

  public <T> T from(final String data, final ContentMediaType contentMediaType, final Class<T> type) {
    final ContentMediaType baseType = contentMediaType.toBaseType();
    if (mappersByContentType.containsKey(baseType)) {
      return mappersByContentType.get(baseType).from(data, type);
    }
    throw new MediaTypeNotSupportedException(contentMediaType.toString());
  }

  public <T> String from(final T data, final ContentMediaType contentMediaType) {
    final ContentMediaType baseType = contentMediaType.toBaseType();
    if (mappersByContentType.containsKey(baseType)) {
      return  mappersByContentType.get(baseType).from(data);
    }
    throw new MediaTypeNotSupportedException(contentMediaType.toString());
  }

  public ContentMediaType[] mappedMediaTypes() {
    return this.mappersByContentType.keySet().toArray(new ContentMediaType[0]);
  }

  public static class Builder {
    private Map<ContentMediaType, Mapper> mappersByContentType;

    public Builder() {
      this.mappersByContentType = new HashMap<>();
    }

    Builder addMapperFor(ContentMediaType contentMediaType, Mapper mapper) {
      mappersByContentType.computeIfPresent(contentMediaType,
        (ct, mp) -> {throw new  IllegalArgumentException("Content mimeType already added");});
      mappersByContentType.put(contentMediaType, mapper);
      return this;
    }

    MediaTypeMapper build() {
      return new MediaTypeMapper(mappersByContentType);
    }
  }
}


