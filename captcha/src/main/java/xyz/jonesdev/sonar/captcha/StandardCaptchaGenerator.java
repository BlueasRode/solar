package xyz.jonesdev.sonar.captcha;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import javax.imageio.ImageIO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.jonesdev.sonar.api.fallback.captcha.CaptchaGenerator;

public final class StandardCaptchaGenerator implements CaptchaGenerator {
    private static final Random RANDOM = new Random();
    private final int width = 128;
    private final int height = 128;
    private final @Nullable File background;
    private @Nullable BufferedImage backgroundImage;

    public @NotNull BufferedImage createImage(char @NotNull [] answer) {
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set a solid background color (gray to match the image)
        graphics.setColor(new Color(150, 150, 150)); // Gray background
        graphics.fillRect(0, 0, 128, 128);

        // Draw the border (brownish color to match the image)
        graphics.setColor(new Color(139, 69, 19)); // Brown border
        graphics.setStroke(new BasicStroke(5));
        graphics.drawRect(2, 2, 124, 124);

        // Draw static text "LiteCore - Captcha" at the top
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.BOLD, 12));
        graphics.drawString("LiteCore - Captcha", 10, 20);

        // Draw the CAPTCHA characters
        this.drawCharacters(graphics, answer);

        // Draw static text "WPISZ KOD NA CZACIE" at the bottom
        graphics.setFont(new Font("Arial", Font.PLAIN, 10));
        graphics.drawString("WPISZ KOD NA CZACIE", 10, 115);

        // Draw the red bar with white circles at the bottom
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 118, 128, 10); // Red bar
        graphics.setColor(Color.WHITE);
        for (int i = 0; i < 10; i++) {
            graphics.fillOval(10 + i * 12, 120, 5, 5); // Small white circles
        }

        graphics.dispose();
        return image;
    }

    private void drawCharacters(@NotNull Graphics2D graphics, char @NotNull [] answer) {
        FontRenderContext ctx = graphics.getFontRenderContext();
        GlyphVector[] glyphs = new GlyphVector[answer.length];

        for (int i = 0; i < answer.length; ++i) {
            Font font = StandardTTFFontProvider.FONTS[RANDOM.nextInt(StandardTTFFontProvider.FONTS.length)];
            glyphs[i] = font.createGlyphVector(ctx, String.valueOf(answer[i]));
        }

        double scalingXY = 5.0 - (double)Math.min(answer.length, 5) * 0.65;
        double totalWidth = Arrays.stream(glyphs).mapToDouble((glyphx) -> {
            return glyphx.getLogicalBounds().getWidth() * scalingXY - 1.0;
        }).sum();
        double beginX = Math.max(Math.min(64.0 - totalWidth / 2.0, totalWidth), 0.0);
        double beginY = 70.25 + scalingXY;

        graphics.setColor(Color.BLACK); // Black text for CAPTCHA characters
        GlyphVector[] var13 = glyphs;
        int var14 = glyphs.length;

        for (int var15 = 0; var15 < var14; ++var15) {
            GlyphVector glyph = var13[var15];
            AffineTransform transformation = AffineTransform.getTranslateInstance(beginX, beginY);
            double shearXY = Math.sin(beginX + beginY) / 6.0;
            transformation.shear(shearXY, shearXY);
            transformation.scale(scalingXY, scalingXY);
            Shape transformedShape = transformation.createTransformedShape(glyph.getOutline());
            graphics.fill(transformedShape);

            beginX += glyph.getVisualBounds().getWidth() * scalingXY + 2.0;
            beginY += (double)(-10.0F + RANDOM.nextFloat() * 20.0F);
        }
    }

    public int getWidth() {
        Objects.requireNonNull(this);
        return 128;
    }

    public int getHeight() {
        Objects.requireNonNull(this);
        return 128;
    }

    public @Nullable File getBackground() {
        return this.background;
    }

    public @Nullable BufferedImage getBackgroundImage() {
        return this.backgroundImage;
    }

    public StandardCaptchaGenerator(@Nullable File background) {
        this.background = background;
    }
}
