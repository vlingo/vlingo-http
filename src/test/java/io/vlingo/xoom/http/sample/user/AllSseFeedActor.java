// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.sample.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.ActorInstantiatorRegistry;
import io.vlingo.xoom.common.Tuple2;
import io.vlingo.xoom.http.resource.sse.SseEvent;
import io.vlingo.xoom.http.resource.sse.SseFeed;
import io.vlingo.xoom.http.resource.sse.SseSubscriber;

public class AllSseFeedActor extends Actor implements SseFeed {
  private final int RetryThreshold = 3000;

  static {
    ActorInstantiatorRegistry.register(AllSseFeedActor.class, new AllSseFeedInstantiator());
  }

  private final SseEvent.Builder builder;
  private final int currentStreamId;
  private final int defaultId;
  private final int feedPayload;
  private final String streamName;

  public static void registerInstantiator() {
    ActorInstantiatorRegistry.register(AllSseFeedActor.class, new AllSseFeedInstantiator());
  }

  public AllSseFeedActor(final String streamName, final int feedPayload, final String feedDefaultId) {
    this.streamName = streamName;
    this.feedPayload = feedPayload;
    this.currentStreamId = 1;
    this.defaultId = defaultId(feedDefaultId, currentStreamId);
    this.builder = SseEvent.Builder.instance();
    logger().info("SseFeed started for stream: " + this.streamName);
  }

  @Override
  public void to(final Collection<SseSubscriber> subscribers) {
    for (final SseSubscriber subscriber : subscribers) {
      final boolean fresh = !subscriber.hasCurrentEventId();
      final int retry = fresh ? RetryThreshold : SseEvent.NoRetry;
      final int startId = fresh ? defaultId : Integer.parseInt(subscriber.currentEventId());
      final int endId = startId + feedPayload - 1;
      Tuple2<List<SseEvent>, Integer> result = readSubStream(startId, endId, retry);
      subscriber.client().send(result._1);
      subscriber.currentEventId(String.valueOf(result._2));
    }
  }

  private int defaultId(final String feedDefaultId, final int defaultDefaultId) {
    final int maybeDefaultId = Integer.parseInt(feedDefaultId);
    return maybeDefaultId <= 0 ? defaultDefaultId : maybeDefaultId;
  }

  private Tuple2<List<SseEvent>, Integer> readSubStream(final int startId, final int endId, final int retry) {
    final List<SseEvent> substream = new ArrayList<>();
    int type = 0;
    int id = startId;
    for ( ; id <= endId; ++id) {
      substream.add(builder.clear().event("mimeType-" + ('A' + type)).id(id).data("data-" + id).retry(retry).toEvent());
      type = type > 26 ? 0 : type + 1;
    }

    return Tuple2.from(substream, id + 1);
  }

  private static class AllSseFeedInstantiator extends SseFeedInstantiator<AllSseFeedActor> {
    private static final long serialVersionUID = 530319176678558252L;

    public AllSseFeedInstantiator() { }

    @Override
    public AllSseFeedActor instantiate() {
      return new AllSseFeedActor(streamName, feedPayload, feedDefaultId);
    }
  }
}
