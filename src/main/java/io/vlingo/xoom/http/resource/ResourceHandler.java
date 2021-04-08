// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.resource;

import io.vlingo.xoom.actors.CompletesEventually;
import io.vlingo.xoom.actors.Logger;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Scheduler;
import io.vlingo.xoom.http.ContentType;
import io.vlingo.xoom.http.Context;
import io.vlingo.xoom.http.Header.Headers;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.Response.Status;
import io.vlingo.xoom.http.ResponseHeader;

/**
 * The abstract base class of all classes that represent REST resources
 * that are described by {@code Actions}, handle incoming requests, and produce
 * outgoing responses.
 * <p>NOTE: Concrete extender instances are pooled and will be continually
 * reused to handle requests. Therefore these must not maintain business
 * state that accumulates or otherwise changes across requests.</p>
 * <p>NOTE: Resources that provide an independent {@code routes()}
 * implementation are not required to extend {@code ResourceHandler}
 * and are not pooled by the {@code DispatcherActor}.</p>
 * <p>@see io.vlingo.xoom.http.resource.DynamicResource</p>
 */
public abstract class ResourceHandler {
  /** My {@code Context} which is injected for each new request I handle. */
  Context context;
  /** My {@code Stage} within which my backing {@code Actor} resides. */
  Stage stage;

  /**
   * Answer the {@code Resource<?>} fluently defined by the {@code ResourceBuilder} DSL.
   * Must be overridden to use.
   * @return {@code Resource<?>}
   */
  public Resource<?> routes() {
    throw new UnsupportedOperationException("Undefined resource; must override.");
  }

  /**
   * Construct my default state.
   */
  protected ResourceHandler() {
  }

  /**
   * Answer my {@code completes}.
   * @return CompletesEventually
   */
  protected CompletesEventually completes() {
    return context.completes;
  }

  /**
   * Answer my {@code context}.
   * @return Context
   */
  protected Context context() {
    return context;
  }

  /**
   * Answer my {@code logger}, which is the {@code defaultLogger} of my {@code World}.
   * @return Logger
   */
  protected Logger logger() {
    return stage.world().defaultLogger();
  }

  /**
   * Answer my {@code scheduler}, which is owned by my {@code Stage}.
   * @return Scheduler
   */
  protected Scheduler scheduler() {
    return stage.scheduler();
  }

  /**
   * Answer my {@code stage}, within which my backing {@code Actor} resides.
   * @return Stage
   */
  protected Stage stage() {
    return stage;
  }

  /**
   * Answer my {@code contentType}, which is by default {@code "text/plain", "us-ascii"}.
   * @return ContentType
   */
  protected ContentType contentType() {
    return ContentType.of("text/plain", "us-ascii");
  }

  /**
   * Answer a {@code Response} with the {@code status} and {@code entity}
   * with a {@code Content-Type} header per my {@code contentType()}, which
   * may be overridden.
   * @param status the Status of the Response
   * @param entity the String entity of the Response
   * @return Response
   */
  protected Response entityResponseOf(final Status status, final String entity) {
    return entityResponseOf(status, Headers.empty(), entity);
  }

  /**
   * Answer a {@code Response} with the {@code status}, {@code headers}, and {@code entity}
   * with a {@code Content-Type} header per my {@code contentType()}, which may be overridden.
   * @param status the Status of the Response
   * @param headers the {@code Headers<ResponseHeader>} to which the {@code Content-Type} header is appended
   * @param entity the String entity of the Response
   * @return Response
   */
  protected Response entityResponseOf(final Status status, final Headers<ResponseHeader> headers, final String entity) {
    return Response.of(status, headers.and(contentType().toResponseHeader()), entity);
  }

  /**
   * FOR INTERNAL TEST USE ONLY.
   * @param context the Context to set as my context
   * @param stage the Stage to set as my stage
   */
  public void __internal__test_set_up(final Context context, final Stage stage) {
    this.context = context;
    this.stage = stage;
  }
}
