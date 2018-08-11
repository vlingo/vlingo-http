package io.vlingo.http.resource;

import io.vlingo.actors.Stage;
import io.vlingo.http.Context;
import io.vlingo.http.Method;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

public class DynamicResource extends Resource {
  private SpecificResourceHandler instance;
  final List<Predicate> handlers;

  protected DynamicResource(final String name, final int handlerPoolSize, final List<Predicate> handlers) {
    super(name, handlerPoolSize);
    this.handlers = handlers;
  }

  public void dispatchToHandlerWith(Context context, Action.MappedParameters mappedParameters) {
    Consumer<SpecificResourceHandler> consumer = (resource) -> handlers
      .stream()
      .filter(handler -> handler.equals(handler))
      .findFirst()
      .get()
      .routeHandler.handler(context.request, this.instance);
    pooledHandler().handleFor(context, consumer);
  }

  Action.MatchResults matchWith(Method method, URI uri) {
    /**
     * TODO a way to reuse Action implementation.
     */
    throw new NotImplementedException();
  }

  protected ResourceHandler resourceHandlerInstance(Stage stage) {
    this.instance = new SpecificResourceHandler(stage);
    return this.instance;
  }

  private static class SpecificResourceHandler extends ResourceHandler {
    SpecificResourceHandler(final Stage stage) {
      this.stage = stage;
    }
  }
}