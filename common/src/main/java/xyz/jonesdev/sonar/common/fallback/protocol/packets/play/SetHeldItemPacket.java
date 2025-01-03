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

package xyz.jonesdev.sonar.common.fallback.protocol.packets.play;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import xyz.jonesdev.sonar.api.fallback.protocol.ProtocolVersion;
import xyz.jonesdev.sonar.common.fallback.protocol.FallbackPacket;
import xyz.jonesdev.sonar.common.util.ProtocolUtil;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class SetHeldItemPacket implements FallbackPacket {
  private int slot;

  @Override
  public void encode(final @NotNull ByteBuf byteBuf, final @NotNull ProtocolVersion protocolVersion) throws Exception {
    if (protocolVersion.greaterThanOrEquals(ProtocolVersion.MINECRAFT_1_21_4)) {
      ProtocolUtil.writeVarInt(byteBuf, slot);
    } else {
      byteBuf.writeByte(slot);
    }
  }

  @Override
  public void decode(final @NotNull ByteBuf byteBuf, final ProtocolVersion protocolVersion) {
    slot = byteBuf.readShort();
  }
}
