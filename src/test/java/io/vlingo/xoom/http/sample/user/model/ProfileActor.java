// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.sample.user.model;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;

public class ProfileActor extends Actor implements Profile {
  private State state;

  @Override
  public Completes<State> withTwitterAccount(final String twitterAccount) {
    state = state.withTwitterAccount(twitterAccount);
    return completes().with(state);
  }

  @Override
  public Completes<State> withLinkedInAccount(final String linkedInAccount) {
    state = state.withLinkedInAccount(linkedInAccount);
    return completes().with(state);
  }

  @Override
  public Completes<State> withWebSite(final String website) {
    state = state.withWebSite(website);
    return completes().with(state);
  }
}
