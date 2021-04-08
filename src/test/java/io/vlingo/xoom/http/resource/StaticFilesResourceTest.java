// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.resource.TestResponseChannelConsumer.Progress;
import io.vlingo.xoom.wire.channel.ResponseChannelConsumer;
import io.vlingo.xoom.wire.fdx.bidirectional.ClientRequestResponseChannel;
import io.vlingo.xoom.wire.fdx.bidirectional.netty.client.NettyClientRequestResponseChannel;
import io.vlingo.xoom.wire.message.ByteBufferAllocator;
import io.vlingo.xoom.wire.message.Converters;
import io.vlingo.xoom.wire.node.Address;
import io.vlingo.xoom.wire.node.AddressType;
import io.vlingo.xoom.wire.node.Host;

public class StaticFilesResourceTest {
  private static final AtomicInteger baseServerPort = new AtomicInteger(19001);

  private final ByteBuffer buffer = ByteBufferAllocator.allocate(65535);
  private ClientRequestResponseChannel client;
  private ResponseChannelConsumer consumer;
  private String contentRoot;
  private Progress progress;
  private java.util.Properties properties;
  private Server server;
  private int serverPort;
  private World world;

  @Test
  public void testThatServesRootDefaultStaticFile() throws IOException {
    final String resource = "/index.html";
    final String content = readTextFile(contentRoot + resource);
    final String request = getRequest("/");
    final AccessSafely consumeCalls = progress.expectConsumeTimes(1);

    client.requestWith(toByteBuffer(request));

    while (consumeCalls.totalWrites() < 1) {
      client.probeChannel();
    }
    consumeCalls.readFrom("completed");

    final Response contentResponse = progress.responses.poll();

    assertEquals(1, progress.consumeCount.get());
    assertEquals(Response.Status.Ok, contentResponse.status);
    assertEquals(content, contentResponse.entity.content());
  }

  @Test
  public void testThatServesDefaultStaticFile() throws IOException {
    final String resource = "/views/test 2/index.html";
    final String content = readTextFile(contentRoot + resource);
    final String request = getRequest("/views/test 2/");
    final AccessSafely consumeCalls = progress.expectConsumeTimes(1);

    client.requestWith(toByteBuffer(request));

    while (consumeCalls.totalWrites() < 1) {
      client.probeChannel();
    }
    consumeCalls.readFrom("completed");

    final Response contentResponse = progress.responses.poll();
    assertEquals(1, progress.consumeCount.get());
    assertEquals(Response.Status.Ok, contentResponse.status);
    assertEquals(content, contentResponse.entity.content());
  }

  @Test
  public void testThatServesRootStaticFile() throws IOException {
    final String resource = "/index.html";
    final String content = readTextFile(contentRoot + resource);
    final String request = getRequest(resource);
    final AccessSafely consumeCalls = progress.expectConsumeTimes(1);

    client.requestWith(toByteBuffer(request));

    while (consumeCalls.totalWrites() < 1) {
      client.probeChannel();
    }
    consumeCalls.readFrom("completed");

    final Response contentResponse = progress.responses.poll();

    assertEquals(1, progress.consumeCount.get());
    assertEquals(Response.Status.Ok, contentResponse.status);
    assertEquals(content, contentResponse.entity.content());
  }

  @Test
  public void testThatServesCssSubDirectoryStaticFile() throws IOException {
    final String resource = "/css/styles.css";
    final String content = readTextFile(contentRoot + resource);
    final String request = getRequest(resource);
    final AccessSafely consumeCalls = progress.expectConsumeTimes(1);

    client.requestWith(toByteBuffer(request));

    while (consumeCalls.totalWrites() < 1) {
      client.probeChannel();
    }
    consumeCalls.readFrom("completed");

    final Response contentResponse = progress.responses.poll();

    assertEquals(1, progress.consumeCount.get());
    assertEquals(Response.Status.Ok, contentResponse.status);
    assertEquals(content, contentResponse.entity.content());
  }

  @Test
  public void testThatServesJsSubDirectoryStaticFile() throws IOException {
    final String resource = "/js/vuetify.js";
    final String content = readTextFile(contentRoot + resource);
    final String request = getRequest(resource);
    final AccessSafely consumeCalls = progress.expectConsumeTimes(1);

    client.requestWith(toByteBuffer(request));

    while (consumeCalls.totalWrites() < 1) {
      client.probeChannel();
    }
    consumeCalls.readFrom("completed");

    final Response contentResponse = progress.responses.poll();

    assertEquals(1, progress.consumeCount.get());
    assertEquals(Response.Status.Ok, contentResponse.status);
    assertEquals(content, contentResponse.entity.content());
  }

  @Test
  public void testThatServesViewsSubDirectoryStaticFile() throws IOException {
    final String resource = "/views/About.vue";
    final String content = readTextFile(contentRoot + resource);
    final String request = getRequest(resource);
    final AccessSafely consumeCalls = progress.expectConsumeTimes(1);

    client.requestWith(toByteBuffer(request));

    while (consumeCalls.totalWrites() < 1) {
      client.probeChannel();
    }
    consumeCalls.readFrom("completed");

    final Response contentResponse = progress.responses.poll();

    assertEquals(1, progress.consumeCount.get());
    assertEquals(Response.Status.Ok, contentResponse.status);
    assertEquals(content, contentResponse.entity.content());
  }

  @Before
  public void setUp() throws Exception {

    world = World.startWithDefaults("static-file-resources");

    serverPort = baseServerPort.getAndIncrement();

    properties = new java.util.Properties();
    properties.setProperty("server.http.port", ""+serverPort);
    properties.setProperty("server.dispatcher.pool", "10");
    properties.setProperty("server.buffer.pool.size", "100");
    properties.setProperty("server.message.buffer.size", "65535");
    properties.setProperty("server.probe.interval", "2");
    properties.setProperty("server.probe.timeout", "2");
    properties.setProperty("server.processor.pool.size", "10");
    properties.setProperty("server.request.missing.content.timeout", "100");

    properties.setProperty("static.files.resource.pool", "5");
    contentRoot = "content";
    properties.setProperty("static.files.resource.root", contentRoot);
    properties.setProperty("static.files.resource.subpaths", "[/, /css, /js, /views]");

    properties.setProperty("feed.producer.name.events", "/feeds/events");
    properties.setProperty("feed.producer.events.class", "io.vlingo.xoom.http.sample.user.EventsFeedProducerActor");
    properties.setProperty("feed.producer.events.payload", "20");
    properties.setProperty("feed.producer.events.pool", "10");

    properties.setProperty("sse.stream.name.all", "/eventstreams/all");
    properties.setProperty("sse.stream.all.feed.class", "io.vlingo.xoom.http.sample.user.AllSseFeedActor");
    properties.setProperty("sse.stream.all.feed.payload", "50");
    properties.setProperty("sse.stream.all.feed.interval", "1000");
    properties.setProperty("sse.stream.all.feed.default.id", "-1");
    properties.setProperty("sse.stream.all.pool", "10");

    properties.setProperty("resource.name.profile", "[define, query]");

    properties.setProperty("resource.profile.handler", "io.vlingo.xoom.http.sample.user.ProfileResource");
    properties.setProperty("resource.profile.pool", "5");
    properties.setProperty("resource.profile.disallowPathParametersWithSlash", "false");

    properties.setProperty("action.profile.define.method", "PUT");
    properties.setProperty("action.profile.define.uri", "/users/{userId}/profile");
    properties.setProperty("action.profile.define.to", "define(String userId, body:io.vlingo.xoom.http.sample.user.ProfileData profileData)");
    properties.setProperty("action.profile.define.mapper", "io.vlingo.xoom.http.sample.user.ProfileDataMapper");

    properties.setProperty("action.profile.query.method", "GET");
    properties.setProperty("action.profile.query.uri", "/users/{userId}/profile");
    properties.setProperty("action.profile.query.to", "query(String userId)");
    properties.setProperty("action.profile.query.mapper", "io.vlingo.xoom.http.sample.user.ProfileDataMapper");

    server = Server.startWith(world.stage(), properties);
    assertTrue(server.startUp().await(500L));

    progress = new Progress();
    consumer = world.actorFor(ResponseChannelConsumer.class, Definition.has(TestResponseChannelConsumer.class, Definition.parameters(progress)));
    client = new NettyClientRequestResponseChannel(Address.from(Host.of("localhost"), serverPort, AddressType.NONE), consumer, 100, 10240);
  }


  @After
  public void tearDown() throws Exception {
    this.client.close();

    this.server.shutDown();

    Thread.sleep(200);

    this.world.terminate();
  }

  private String getRequest(final String filePath) {
    return "GET " + String.join("%20", filePath.split(" ")) + " HTTP/1.1\nHost: vlingo.io\n\n";
  }

  private byte[] readFile(final String path) throws IOException {
    final InputStream contentStream = StaticFilesResource.class.getResourceAsStream("/" + path);
    if (contentStream != null && contentStream.available() > 0) {
      return IOUtils.toByteArray(contentStream);
    }
    throw new IllegalArgumentException("File not found.");
  }

  private String readTextFile(final String path) throws IOException {
    return new String(readFile(path), Charset.forName("UTF-8"));
  }

  private ByteBuffer toByteBuffer(final String requestContent) {
    buffer.clear();
    buffer.put(Converters.textToBytes(requestContent));
    buffer.flip();
    return buffer;
  }
}
