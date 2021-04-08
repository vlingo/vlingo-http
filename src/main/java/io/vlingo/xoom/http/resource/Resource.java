/*
 * Copyright © 2012-2021 VLINGO LABS. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */

package io.vlingo.xoom.http.resource;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Logger;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.http.Context;
import io.vlingo.xoom.http.Method;

public abstract class Resource<T> {
  public final String name;
  public final int handlerPoolSize;

  private final ResourceRequestHandler[] handlerPool;
  private final AtomicLong handlerPoolIndex;

  public abstract void dispatchToHandlerWith(final Context context, final Action.MappedParameters mappedParameters);

  abstract Action.MatchResults matchWith(final Method method, final URI uri);

  protected abstract void log(final Logger logger);

  protected abstract ResourceHandler resourceHandlerInstance(final Stage stage);

  void allocateHandlerPool(final Stage stage) {
    for (int idx = 0; idx < handlerPoolSize; ++idx) {
      handlerPool[idx] =
        stage.actorFor(
          ResourceRequestHandler.class,
          Definition.has(
            ResourceRequestHandlerActor.class,
            Definition.parameters(resourceHandlerInstance(stage))));
    }
  }

  protected ResourceRequestHandler pooledHandler() {
    final int index = (int) (handlerPoolIndex.incrementAndGet() % handlerPoolSize);
    return handlerPool[index];
  }

  Resource(final String name,
           final int handlerPoolSize) {
    this.name = name;
    this.handlerPoolSize = handlerPoolSize;
    this.handlerPool = new ResourceRequestHandler[handlerPoolSize];
    this.handlerPoolIndex = new AtomicLong(0);
  }

}
