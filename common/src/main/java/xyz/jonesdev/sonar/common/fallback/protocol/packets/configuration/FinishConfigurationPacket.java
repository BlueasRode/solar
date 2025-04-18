/*
 * Copyright (C) 2025 Sonar Contributors
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

package xyz.jonesdev.sonar.common.fallback.protocol.packets.configuration;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import xyz.jonesdev.sonar.api.fallback.protocol.ProtocolVersion;
import xyz.jonesdev.sonar.common.fallback.protocol.FallbackPacket;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FinishConfigurationPacket implements FallbackPacket {
  public static final FinishConfigurationPacket INSTANCE = new FinishConfigurationPacket();

  @Override
  public void encode(final ByteBuf byteBuf, final ProtocolVersion protocolVersion) {
  }

  @Override
  public void decode(final ByteBuf byteBuf, final ProtocolVersion protocolVersion) {
  }

  @Override
  public int expectedMaxLength(final ProtocolVersion protocolVersion) {
    // This packet is always empty; see https://wiki.vg/Protocol#Finish_Configuration
    return 0;
  }
}
