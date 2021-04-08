// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.resource;

import io.vlingo.xoom.common.serialization.JsonSerialization;

public class DefaultJsonMapper implements Mapper {
  public static final Mapper instance = new DefaultJsonMapper();
  
  @Override
  @SuppressWarnings("unchecked")
  public <T> T from(final String data, final Class<T> type) {
    if (type.getName().equals("java.lang.String")) {
      return (T) data;
    }
    return JsonSerialization.deserialized(data, type);
  }

  @Override
  public <T> String from(final T data) {
    return JsonSerialization.serialized(data);
  }
}
