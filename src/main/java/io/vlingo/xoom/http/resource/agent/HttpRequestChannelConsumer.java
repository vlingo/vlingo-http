// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.resource.agent;

import io.vlingo.xoom.http.Request;
import io.vlingo.xoom.wire.channel.RequestChannelConsumer;
import io.vlingo.xoom.wire.channel.RequestResponseContext;

public interface HttpRequestChannelConsumer extends RequestChannelConsumer {

  /**
   * Consumes the {@code request} and will eventually provide a response via {@code context}.
   *
   * @param context the {@code HttpRequestResponseContext<?>} of the request and response
   * @param request the Request made to the server
   */
  void consume(final RequestResponseContext<?> context, final Request request);
}
