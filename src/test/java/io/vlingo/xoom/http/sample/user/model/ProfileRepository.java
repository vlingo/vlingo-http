// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.sample.user.model;

import java.util.HashMap;
import java.util.Map;

public class ProfileRepository {
  private static ProfileRepository instance;
  
  private final Map<String,Profile.State> profiles;

  public static synchronized ProfileRepository instance() {
    if (instance == null) {
      instance = new ProfileRepository();
    }
    return instance;
  }
  
  public Profile.State profileOf(final String userId) {
    final Profile.State profileState = profiles.get(userId);
    
    return profileState == null ? Profile.nonExisting() : profileState;
  }

  public void save(final Profile.State profileState) {
    profiles.put(profileState.id, profileState);
  }

  private ProfileRepository() {
    this.profiles = new HashMap<>();
  }
}
