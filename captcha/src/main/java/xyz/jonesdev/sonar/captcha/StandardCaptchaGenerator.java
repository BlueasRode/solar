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

package xyz.jonesdev.sonar.captcha;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

@Getter
@RequiredArgsConstructor
public final class StandardCaptchaGenerator implements CaptchaGenerator {
  private static final Random RANDOM = new Random();
  
  private final int width = 128, height = 128;
  private final @Nullable File background;
  private @Nullable BufferedImage backgroundImage;

  @Override
  public @NotNull BufferedImage createImage(final char @NotNull [] answer) {
    final BufferedImage image = createBackgroundImage();
    final Graphics2D graphics = image.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Rysowanie nagłówka
    drawHeader(graphics);
    
    // Rysowanie znaków CAPTCHA
    drawCharacters(graphics, answer);
    
    // Rysowanie stopki
    drawFooter(graphics);
    
    graphics.dispose();
    return image;
  }

  private @NotNull BufferedImage createBackgroundImage() {
    final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    final Graphics2D graphics = image.createGraphics();
    
    // Ustawienie jednolitego szarego tła
    graphics.setColor(new Color(150, 150, 150)); // Szary kolor
    graphics.fillRect(0, 0, width, height);
    
    graphics.dispose();
    return image;
  }

  private void drawHeader(final @NotNull Graphics2D graphics) {
    graphics.setColor(Color.BLACK);
    graphics.setFont(new Font("Arial", Font.BOLD, 12));
    String header = "EyfenCord - Captcha";
    FontMetrics fm = graphics.getFontMetrics();
    int x = (width - fm.stringWidth(header)) / 2;
    graphics.drawString(header, x, 20);
  }

  private void drawFooter(final @NotNull Graphics2D graphics) {
    graphics.setColor(Color.BLACK);
    graphics.setFont(new Font("Arial", Font.PLAIN, 10));
    String footer = "WPISZ KOD NA CZACIE";
    FontMetrics fm = graphics.getFontMetrics();
    int x = (width - fm.stringWidth(footer)) / 2;
    graphics.drawString(footer, x, height - 10);
  }

  private void drawCharacters(final @NotNull Graphics2D graphics,
                              final char @NotNull [] answer) {
    final FontRenderContext ctx = graphics.getFontRenderContext();
    final GlyphVector[] glyphs = new GlyphVector[answer.length];

    // Ustawienie prostego fontu
    for (int i = 0; i < answer.length; i++) {
      final Font font = new Font("Arial", Font.BOLD, 20);
      glyphs[i] = font.createGlyphVector(ctx, String.valueOf(answer[i]));
    }

    final double scalingXY = 1.5; // Mniejsze skalowanie dla czytelności

    // Obliczanie pozycji początkowej
    final double totalWidth = Arrays.stream(glyphs)
      .mapToDouble(glyph -> glyph.getLogicalBounds().getWidth() * scalingXY)
      .sum();
    double beginX = (width - totalWidth) / 2;
    double beginY = (height / 2) + 10;

    // Rysowanie znaków
    graphics.setColor(Color.BLACK);
    for (final GlyphVector glyph : glyphs) {
      final AffineTransform transformation = AffineTransform.getTranslateInstance(beginX, beginY);
      transformation.scale(scalingXY, scalingXY);
      final Shape transformedShape = transformation.createTransformedShape(glyph.getOutline());
      graphics.fill(transformedShape);
      beginX += glyph.getVisualBounds().getWidth() * scalingXY + 5; // Mniejszy odstęp między znakami
      beginY += -5 + RANDOM.nextFloat() * 10; // Lekkie losowe przesunięcie w pionie
    }
  }
}
