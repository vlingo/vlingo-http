// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.sample.user;

import io.vlingo.xoom.http.sample.user.model.Profile;

public class ProfileData {
  public final String linkedInAccount;
  public final String twitterAccount;
  public final String website;

  public static ProfileData from(final Profile.State profile) {
    return new ProfileData(profile.twitterAccount, profile.linkedInAccount, profile.website);
  }

  public ProfileData(final String twitterAccount, final String linkedInAccount, final String website) {
    this.twitterAccount = twitterAccount;
    this.linkedInAccount = linkedInAccount;
    this.website = website;
  }
}
