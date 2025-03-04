package me.mrgraycat.eglow.util.packets.chat.rgb.gradient;

import me.mrgraycat.eglow.util.packets.chat.EnumChatFormat;
import me.mrgraycat.eglow.util.packets.chat.TextColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gradient applier for &lt;gradient:#RRGGBB:#RRGGBB>Text&lt;/gradient>
 * and &lt;gradient:#RRGGBB|L:#RRGGBB>Text&lt;/gradient>
 */
public class KyoriGradient implements GradientPattern {

    private final Pattern pattern = Pattern.compile("<gradient:#[0-9a-fA-F]{6}:#[0-9a-fA-F]{6}>[^<]*</gradient>");

    private final Pattern patternLegacy = Pattern.compile("<gradient:#[0-9a-fA-F]{6}\\|.:#[0-9a-fA-F]{6}>[^<]*</gradient>");
    
    @Override
    public String applyPattern(String text, boolean ignorePlaceholders) {
        if (!text.contains("<grad")) return text;
        String replaced = text;
        Matcher m = patternLegacy.matcher(replaced);
        while (m.find()) {
            String format = m.group();
            EnumChatFormat legacyColor = EnumChatFormat.getByChar(format.charAt(18));
            if ((ignorePlaceholders && format.contains("%")) || legacyColor == null) continue;
            TextColor start = new TextColor(format.substring(11, 17), legacyColor);
            String message = format.substring(28, format.length()-11);
            TextColor end = new TextColor(format.substring(21, 27));
            String applied = asGradient(start, message, end);
            replaced = replaced.replace(format, applied);
        }
        m = pattern.matcher(replaced);
        while (m.find()) {
            String format = m.group();
            if (ignorePlaceholders && format.contains("%")) continue;
            TextColor start = new TextColor(format.substring(11, 17));
            String message = format.substring(26, format.length()-11);
            TextColor end = new TextColor(format.substring(19, 25));
            String applied = asGradient(start, message, end);
            replaced = replaced.replace(format, applied);
        }
        return replaced;
    }
}