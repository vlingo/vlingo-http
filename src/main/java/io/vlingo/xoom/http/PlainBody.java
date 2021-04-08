// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http;

public class PlainBody implements Body {

  /** My content. */
  public final String content;

  /**
   * @see io.vlingo.xoom.http.Body#content()
   */
  @Override
  public String content() {
    return content;
  }

  @Override
  public byte[] binaryContent() {
    return content.getBytes();
  }

  /**
   * @see io.vlingo.xoom.http.Body#hasContent()
   */
  @Override
  public boolean hasContent() {
    return !content.isEmpty();
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return content;
  }

  /**
   * Construct my default state with the {@code body} as content.
   * @param body the String body content
   */
  PlainBody(final String body) {
    this.content = body;
  }

  /**
   * Construct my default state with empty body content.
   */
  PlainBody() {
    this.content = "";
  }
}
