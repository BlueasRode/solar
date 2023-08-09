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

package xyz.jonesdev.sonar.velocity.verbose;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import xyz.jonesdev.sonar.api.Sonar;
import xyz.jonesdev.sonar.api.profiler.JVMProfiler;
import xyz.jonesdev.sonar.api.statistics.Statistics;
import xyz.jonesdev.sonar.api.verbose.Verbose;
import xyz.jonesdev.sonar.common.fallback.traffic.TrafficCounter;
import xyz.jonesdev.sonar.common.verbose.VerboseAnimation;

import java.util.Collection;
import java.util.Vector;

import static xyz.jonesdev.sonar.api.format.MemoryFormatter.formatMemory;

@RequiredArgsConstructor
public final class ActionBarVerbose implements Verbose, JVMProfiler {
  private final ProxyServer server;
  @Getter
  private final Collection<String> subscribers = new Vector<>(1);

  public void update() {
    // Clean up blacklisted IPs
    Sonar.get().getFallback().getBlacklisted().cleanUp(false);

    final Component component = Component.text(
      Sonar.get().getConfig().ACTION_BAR_LAYOUT
        .replace("%queued%",
          Sonar.DECIMAL_FORMAT.format(Sonar.get().getFallback().getQueue().getQueuedPlayers().size()))
        .replace("%verifying%", Sonar.DECIMAL_FORMAT.format(Sonar.get().getFallback().getConnected().size()))
        .replace("%verified%", Sonar.DECIMAL_FORMAT.format(Sonar.get().getFallback().getVerified().size()))
        .replace("%blacklisted%",
          Sonar.DECIMAL_FORMAT.format(Sonar.get().getFallback().getBlacklisted().estimatedSize()))
        .replace("%total-joins%", Sonar.DECIMAL_FORMAT.format(Statistics.TOTAL_TRAFFIC.get(0)))
        .replace("%real-joins%", Sonar.DECIMAL_FORMAT.format(Statistics.REAL_TRAFFIC.get(0)))
        .replace("%failed-verify%", Sonar.DECIMAL_FORMAT.format(Statistics.FAILED_VERIFICATIONS.get(0)))
        .replace("%incoming-traffic%", TrafficCounter.INCOMING.getCachedSecond())
        .replace("%outgoing-traffic%", TrafficCounter.OUTGOING.getCachedSecond())
        .replace("%incoming-traffic-ttl%", TrafficCounter.INCOMING.getCachedTtl())
        .replace("%outgoing-traffic-ttl%", TrafficCounter.OUTGOING.getCachedTtl())
        .replace("%used-memory%", formatMemory(getUsedMemory()))
        .replace("%free-memory%", formatMemory(getFreeMemory()))
        .replace("%total-memory%", formatMemory(getTotalMemory()))
        .replace("%max-memory%", formatMemory(getMaxMemory()))
        .replace("%animation%", VerboseAnimation.nextAnimation())
    );

    synchronized (subscribers) {
      for (final String subscriber : subscribers) {
        server.getPlayer(subscriber).ifPresent(player -> {
          player.sendActionBar(component);
        });
      }
    }
  }
}
