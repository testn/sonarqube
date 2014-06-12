/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.qualityprofile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.sonar.core.log.Loggable;
import org.sonar.core.qualityprofile.db.ActiveRuleKey;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.Map;

public class ActiveRuleChange implements Loggable {

  static enum Type {
    ACTIVATED, DEACTIVATED, UPDATED
  }

  private final Type type;
  private final ActiveRuleKey key;
  private String severity = null;
  private ActiveRule.Inheritance inheritance = null;
  private Map<String, String> parameters = Maps.newHashMap();

  private long start;

  private ActiveRuleChange(Type type, ActiveRuleKey key) {
    this.type = type;
    this.key = key;
    this.start = System.currentTimeMillis();
  }

  public ActiveRuleKey getKey() {
    return key;
  }

  public Type getType() {
    return type;
  }

  @CheckForNull
  public String getSeverity() {
    return severity;
  }

  public ActiveRuleChange setSeverity(@Nullable String severity) {
    this.severity = severity;
    return this;
  }

  public ActiveRuleChange setInheritance(@Nullable ActiveRule.Inheritance inheritance) {
    this.inheritance = inheritance;
    return this;
  }

  @CheckForNull
  public ActiveRule.Inheritance getInheritance() {
    return this.inheritance;
  }

  @CheckForNull
  public Map<String, String> getParameters() {
    return parameters;
  }

  public ActiveRuleChange setParameter(String key, @Nullable String value) {
    parameters.put(key, value);
    return this;
  }

  public ActiveRuleChange setParameters(Map<String, String> m) {
    parameters.clear();
    parameters.putAll(m);
    return this;
  }

  @Override
  public Map<String, String> getDetails() {
    ImmutableMap.Builder<String, String> details = ImmutableMap.builder();
    if (getType() != null) {
      details.put("type", getType().name());
    }
    if (getKey() != null) {
      details.put("key", getKey().toString());
      details.put("ruleKey", getKey().ruleKey().toString());
      details.put("profileKey", getKey().qProfile().toString());
    }
    return details.build();
  }

  @Override
  public int getExecutionTime() {
    return (int) (System.currentTimeMillis() - start);
  }

  public static ActiveRuleChange createFor(Type type, ActiveRuleKey key) {
    return new ActiveRuleChange(type, key);
  }
}
