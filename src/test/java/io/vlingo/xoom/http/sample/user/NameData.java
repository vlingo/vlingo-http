// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.sample.user;

import io.vlingo.xoom.http.sample.user.model.Name;

import java.util.Objects;

public class NameData {
  public final String given;
  public final String family;

  public static NameData from(final String given, final String family) {
    return new NameData(given, family);
  }

  public static NameData from(final Name name) {
    return new NameData(name.given, name.family);
  }
  
  public NameData(final String given, final String family) {
    this.given = given;
    this.family = family;
  }

  @Override
  public String toString() {
    return "NameData[given=" + given + ", family=" + family + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NameData)) return false;
    NameData nameData = (NameData) o;
    return Objects.equals(given, nameData.given) &&
      Objects.equals(family, nameData.family);
  }

  @Override
  public int hashCode() {
    return Objects.hash(given, family);
  }
}
