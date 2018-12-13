/*
 * Copyright 2018 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.tools.jib.plugins.common.logging;

import com.google.cloud.tools.jib.event.events.LogEvent.Level;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.function.Consumer;

/** Logs messages plainly. */
class PlainConsoleLogger implements ConsoleLogger {

  private final ImmutableMap<Level, Consumer<String>> messageConsumers;
  private final SingleThreadedExecutor singleThreadedExecutor;

  /**
   * Creates a {@link PlainConsoleLogger}.
   *
   * @param messageConsumers map from each {@link Level} to a log message {@link Consumer<String>}
   * @param singleThreadedExecutor a {@link SingleThreadedExecutor} to ensure that all messages are
   *     logged in a sequential, deterministic order
   */
  PlainConsoleLogger(
      ImmutableMap<Level, Consumer<String>> messageConsumers,
      SingleThreadedExecutor singleThreadedExecutor) {
    this.messageConsumers = messageConsumers;
    this.singleThreadedExecutor = singleThreadedExecutor;
  }

  @Override
  public void log(Level logLevel, String message) {
    singleThreadedExecutor.execute(
        () -> Preconditions.checkNotNull(messageConsumers.get(logLevel)).accept(message));
  }
}
