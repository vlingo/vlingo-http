// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.http.resource;

import io.vlingo.actors.*;
import io.vlingo.common.BasicCompletes;
import io.vlingo.common.Completes;
import io.vlingo.common.SerializableConsumer;
import io.vlingo.http.Response;

public class ClientConsumer__Proxy implements ClientConsumer {

  private static final transient String representationConclude0 = "conclude()";
  private static final transient String requestWithRepresentation1 = "requestWith(io.vlingo.http.Request)";
  private static final transient String consumeRepresentation2 = "consume(io.vlingo.wire.message.ConsumerByteBuffer)";
  private static final transient String intervalSignalRepresentation3 = "intervalSignal(io.vlingo.actors.Scheduled, java.lang.Object)";
  private static final transient String stopRepresentation4 = "stop()";

  private final transient  Actor actor;
  private final transient Mailbox mailbox;

  private final Class<ClientConsumer> protocol = ClientConsumer.class;
  private final Address address;

  public ClientConsumer__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.address = actor.address();
    this.mailbox = mailbox;
  }

  @Override
  public Completes<Response> requestWith(io.vlingo.http.Request arg0, io.vlingo.common.Completes<Response> arg1) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ClientConsumer> consumer = (actor) -> actor.requestWith(arg0, arg1);
      final Completes<Response> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, ClientConsumer.class, consumer, Returns.value(completes), requestWithRepresentation1); }
      else { mailbox.send(new LocalMessage<ClientConsumer>(actor, ClientConsumer.class, consumer, Returns.value(completes), requestWithRepresentation1)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, requestWithRepresentation1));
    }
    return null;
  }
  @Override
  public void consume(io.vlingo.wire.message.ConsumerByteBuffer arg0) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ClientConsumer> consumer = (actor) -> actor.consume(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ClientConsumer.class, consumer, null, consumeRepresentation2); }
      else { mailbox.send(new LocalMessage<ClientConsumer>(actor, ClientConsumer.class, consumer, consumeRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, consumeRepresentation2));
    }
  }
  @Override
  public void intervalSignal(io.vlingo.common.Scheduled<Object> arg0, java.lang.Object arg1) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ClientConsumer> consumer = (actor) -> actor.intervalSignal(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ClientConsumer.class, consumer, null, intervalSignalRepresentation3); }
      else { mailbox.send(new LocalMessage<ClientConsumer>(actor, ClientConsumer.class, consumer, intervalSignalRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, intervalSignalRepresentation3));
    }
  }
  @Override
  public void conclude() {
    if (!actor.isStopped()) {
      final SerializableConsumer<Stoppable> consumer = (actor) -> actor.conclude();
      if (mailbox.isPreallocated()) { mailbox.send(actor, Stoppable.class, consumer, null, representationConclude0); }
      else { mailbox.send(new LocalMessage<Stoppable>(actor, Stoppable.class, consumer, representationConclude0)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, representationConclude0));
    }
  }
  @Override
  public void stop() {
    if (!actor.isStopped()) {
      final SerializableConsumer<ClientConsumer> consumer = (actor) -> actor.stop();
      if (mailbox.isPreallocated()) { mailbox.send(actor, ClientConsumer.class, consumer, null, stopRepresentation4); }
      else { mailbox.send(new LocalMessage<ClientConsumer>(actor, ClientConsumer.class, consumer, stopRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, stopRepresentation4));
    }
  }
  @Override
  public boolean isStopped() {
    return actor.isStopped();
  }
}
