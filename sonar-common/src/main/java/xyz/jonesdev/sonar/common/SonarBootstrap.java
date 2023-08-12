/*
 * Copyright (C) 2023 Sonar Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.jonesdev.sonar.common;

import org.jetbrains.annotations.NotNull;
import xyz.jonesdev.cappuccino.Cappuccino;
import xyz.jonesdev.cappuccino.ExpiringCache;
import xyz.jonesdev.sonar.api.Sonar;
import xyz.jonesdev.sonar.api.SonarSupplier;
import xyz.jonesdev.sonar.api.fallback.FallbackRatelimiter;
import xyz.jonesdev.sonar.api.timer.DelayTimer;
import xyz.jonesdev.sonar.common.fallback.protocol.FallbackPreparer;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public interface SonarBootstrap<T> extends Sonar {
  default void initialize(final @NotNull T plugin) {
    final DelayTimer timer = new DelayTimer();

    // Set the API to this instance
    SonarSupplier.set(this);

    // Initialize plugin
    load(plugin);

    // Start main plugin enable task
    getLogger().info("Enabling Sonar {}...", getVersion());

    // Run the per-platform initialization method
    enable();

    // Done
    getLogger().info("Done ({}s)!", timer.formattedDelay());
  }

  void load(final @NotNull T plugin);

  void enable();

  default void reload() {
    // Load the configuration
    getConfig().load();

    // Warn player if they reloaded and changed the database type
    if (getVerifiedPlayerController() != null
      && getVerifiedPlayerController().getCachedDatabaseType() != getConfig().DATABASE_TYPE) {
      Sonar.get().getLogger().warn("Reloading the server after changing the database type"
        + " is generally not recommended as it can sometimes cause data loss.");
    }

    // Prepare cached packets
    FallbackPreparer.prepare();

    // Update ratelimiter
    final ExpiringCache<InetAddress> expiringCache = Cappuccino.buildExpiring(
      getConfig().VERIFICATION_DELAY, TimeUnit.MILLISECONDS, 250L
    );
    FallbackRatelimiter.INSTANCE.setExpiringCache(expiringCache);
  }

  default void disable() {
    getLogger().info("Starting shutdown process...");
    // ...
    getLogger().info("Successfully shut down. Goodbye!");
  }
}
