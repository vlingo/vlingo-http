// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http;

import java.net.URI;
import java.nio.ByteBuffer;

import io.vlingo.xoom.http.Header.Headers;

/**
 * A request from a client, including headers, body, method, URI, and version.
 * Factory methods are provided for fluent creation.
 */
public class Request {
  public final Body body;
  public final Headers<RequestHeader> headers;
  public final Method method;
  public final URI uri;
  public final Version version;

  // TODO: Currently supports only HTTP/1.1

  public static Request from(final ByteBuffer requestContent) {
    return RequestParser.parserFor(requestContent).fullRequest();
  }

  public static Request from(final Method method, final URI uri, final Version version, final Headers<RequestHeader> headers, final Body body) {
    return new Request(method, uri, version, headers, body);
  }

  // ===========================================
  // fluent API follows
  // ===========================================

  public static Request has(final Method method) {
    return new Request(method);
  }

  public Request and(final Body body) {
    return new Request(this.method, this.uri, this.version, this.headers, body);
  }

  public Request and(final ChunkedBody body) {
    return new Request(this.method, this.uri, this.version, this.headers, body.asPlainBody());
  }

  public Request and(final RequestHeader header) {
    final Headers<RequestHeader> headers = Headers.empty();
    return new Request(this.method, this.uri, this.version, headers.and(this.headers).and(header), this.body);
  }

  public Request and(final Headers<RequestHeader> headers) {
    return new Request(this.method, this.uri, this.version, headers, this.body);
  }

  public Request and(final URI uri) {
    return new Request(this.method, uri, this.version, this.headers, this.body);
  }

  public Request and(final Version version) {
    return new Request(this.method, this.uri, version, this.headers, this.body);
  }

  // ===========================================
  // less fluent API follows
  // ===========================================

  public static Request method(final Method method) {
    return new Request(method);
  }

  public Request body(final String body) {
    return new Request(this.method, this.uri, this.version, this.headers, Body.from(body));
  }

  public Request header(final String name, final String value) {
    final Headers<RequestHeader> headers = Headers.empty();
    return new Request(this.method, this.uri, this.version, headers.and(this.headers).and(RequestHeader.of(name, value)), this.body);
  }

  public Request header(final String name, final int value) {
    return header(name, String.valueOf(value));
  }

  public Request uri(final String uri) {
    return new Request(this.method, URI.create(uri), this.version, this.headers, this.body);
  }

  public Request version(final String version) {
    return new Request(this.method, this.uri, Version.from(version), this.headers, this.body);
  }

  // ===========================================
  // instance
  // ===========================================

  public Header headerOf(final String name) {
    for (final Header header : headers) {
      if (header.matchesNameOf(name)) {
        return header;
      }
    }
    return null;
  }

  public boolean headerMatches(final String name, final String value) {
    final Header header = headerOf(name);
    return header == null ? false : header.matchesValueOf(value);
  }

  public String headerValueOr(final String headerName, final String defaultValue) {
    final Header header = headerOf(headerName);
    return header == null ? defaultValue : header.value;
  }

  public QueryParameters queryParameters() {
    return new QueryParameters(uri.getQuery());
  }

  @Override
  public String toString() {
    return "" + method + " " + uri + " "  + version + "\n" + headers + "\n" + body;
  }

  Request(final Method method, final URI uri, final Version version, final Headers<RequestHeader> headers, final Body body) {
    this.method = method;
    this.uri = uri;
    this.version = version;
    this.body = body;

    if (body != null && body.hasContent() && headers.headerOf(RequestHeader.ContentLength) == null) {
      this.headers = headers.and(RequestHeader.contentLength(body.content()));
    } else {
      this.headers = headers;
    }
  }

  private Request(final Method method) {
    this(method, URI.create("/"), Version.Http1_1, Headers.empty(), Body.from(""));
  }
}
