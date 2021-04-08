// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.sample.user.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
  private static UserRepository instance;
  
  private final Map<String,User.State> users;

  public static synchronized UserRepository instance() {
    if (instance == null) {
      instance = new UserRepository();
    }
    return instance;
  }

  public static void reset() {
    instance = null;
  }

  public User.State userOf(final String userId) {
    final User.State userState = users.get(userId);
    
    return userState == null ? User.nonExisting() : userState;
  }

  public Collection<User.State> users() {
    return Collections.unmodifiableCollection(users.values());
  }

  public void save(final User.State userState) {
    users.put(userState.id, userState);
  }

  private UserRepository() {
    this.users = new ConcurrentHashMap<>();
  }
}
