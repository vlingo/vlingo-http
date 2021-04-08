// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.resource;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.DeadLetter;
import io.vlingo.xoom.actors.LocalMessage;
import io.vlingo.xoom.actors.Mailbox;
import io.vlingo.xoom.common.SerializableConsumer;
import io.vlingo.xoom.http.Context;
import io.vlingo.xoom.http.resource.Action.MappedParameters;

public class ResourceRequestHandler__Proxy implements ResourceRequestHandler {

  private static final String handleForRepresentation1 = "handleFor(io.vlingo.xoom.http.Context, java.util.function.Consumer)";
  private static final String handleForRepresentation2 = "handleFor(io.vlingo.xoom.http.Context, io.vlingo.xoom.http.Action.MappedParameters, io.vlingo.xoom.http.RequestHandler)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ResourceRequestHandler__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void handleFor(io.vlingo.xoom.http.Context arg0, java.util.function.Consumer arg1) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ResourceRequestHandler> consumer = (actor) -> actor.handleFor(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ResourceRequestHandler.class, consumer, null, handleForRepresentation1); }
      else { mailbox.send(new LocalMessage<ResourceRequestHandler>(actor, ResourceRequestHandler.class, consumer, handleForRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, handleForRepresentation1));
    }
  }

  @Override
  public void handleFor(final Context arg0, final MappedParameters arg1, final RequestHandler arg2) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ResourceRequestHandler> consumer = (actor) -> actor.handleFor(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ResourceRequestHandler.class, consumer, null, handleForRepresentation2); }
      else { mailbox.send(new LocalMessage<ResourceRequestHandler>(actor, ResourceRequestHandler.class, consumer, handleForRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, handleForRepresentation2));
    }
  }
}
