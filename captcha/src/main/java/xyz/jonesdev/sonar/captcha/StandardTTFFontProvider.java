package xyz.jonesdev.sonar.captcha;

import java.awt.Font;
import java.io.InputStream;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

final class StandardTTFFontProvider {
    private static final String[] FONT_NAMES = new String[]{"Kingthings_Trypewriter_2"};
    static final Font[] FONTS;
    static final int STANDARD_FONT_SIZE = 25;

    private static Font loadFont(@NotNull String path) {
        try {
            InputStream inputStream = StandardCaptchaGenerator.class.getResourceAsStream(path);
            Font var3;
            try {
                Font customFont = Font.createFont(0, (InputStream)Objects.requireNonNull(inputStream));
                var3 = customFont.deriveFont(0, 25.0F);
            } catch (Throwable var5) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                    }
                }
                throw var5;
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return var3;
        } catch (Exception var6) {
            throw new IllegalStateException(var6);
        }
    }

    private StandardTTFFontProvider() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        FONTS = new Font[FONT_NAMES.length];
        for (int i = 0; i < FONT_NAMES.length; ++i) {
            FONTS[i] = loadFont(String.format("/assets/fonts/%s.ttf", FONT_NAMES[i]));
        }
    }
}
