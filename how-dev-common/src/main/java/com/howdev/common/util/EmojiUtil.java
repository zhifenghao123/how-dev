package com.howdev.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * EmojiUtil class
 *
 * @author haozhifeng
 * @date 2023/03/21
 */
public class EmojiUtil {
    private static final String EMOJI_UNICODE = "[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]";
    private static final String EMOJI_REPLACE_MENT = "[emoji]";

    public EmojiUtil() {
    }

    public static Map emojiReplaceMap(String source) {
        return JacksonUtil.toObject(EmojiUtil.emojiReplace(source), Map.class);
    }

    public static String emojiReplace(String source) {
        if (StringUtils.isEmpty(source)) {
            return source;
        } else {
            source = source.replaceAll(EMOJI_UNICODE, EMOJI_REPLACE_MENT);

            try {
                StringWriter writer = new StringWriter(source.length() * 2);
                translate(source, writer);
                String ret = writer.toString();
                char[] cs = ret.toCharArray();
                StringBuilder sb = new StringBuilder();
                int i = 0;
                int size = cs.length;

                while (true) {
                    while (i < size) {
                        if ('\\' == cs[i] && i + 2 < size && '[' == cs[i + 1] && 'e' == cs[i + 2]) {
                            sb.append("[e");
                            i += 3;
                        } else {
                            sb.append(cs[i]);
                            ++i;
                        }
                    }

                    return sb.toString();
                }
            } catch (Exception var8) {
                return source;
            }
        }
    }

    private static void translate(CharSequence input, Writer out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if (input != null) {
            int pos = 0;
            int len = input.length();

            while (true) {
                while (pos < len) {
                    int consumed = translate(input, pos, out);
                    if (consumed == 0) {
                        char c1 = input.charAt(pos);
                        out.write(c1);
                        ++pos;
                        if (Character.isHighSurrogate(c1) && pos < len) {
                            char c2 = input.charAt(pos);
                            if (Character.isLowSurrogate(c2)) {
                                out.write(c2);
                                ++pos;
                            }
                        }
                    } else {
                        for (int pt = 0; pt < consumed; ++pt) {
                            pos += Character.charCount(Character.codePointAt(input, pos));
                        }
                    }
                }

                return;
            }
        }
    }

    private static int translate(CharSequence input, int index, Writer out) throws IOException {
        if (input.charAt(index) == '\\' && index + 1 < input.length() && input.charAt(index + 1) == 'u') {
            int i;
            for (i = 2; index + i < input.length() && input.charAt(index + i) == 'u'; ++i) {
            }

            if (index + i < input.length() && input.charAt(index + i) == '+') {
                ++i;
            }

            if (index + i + 4 <= input.length()) {
                CharSequence unicode = input.subSequence(index + i, index + i + 4);

                try {
                    int value = Integer.parseInt(unicode.toString(), 16);
                    if (isEmojiCharacter((char) value)) {
                        out.write("\\u" + unicode);
                    } else {
                        out.write(EMOJI_REPLACE_MENT);
                    }
                } catch (NumberFormatException var6) {
                    throw new IllegalArgumentException("Unable to parse unicode value: " + unicode, var6);
                }

                return i + 4;
            } else {
                throw new IllegalArgumentException("Less than 4 hex digits in unicode value: '" + input.subSequence(index, input.length()) + "' due to end of CharSequence");
            }
        } else {
            return 0;
        }
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return codePoint == 0 || codePoint == '\t' || codePoint == '\n' || codePoint == '\r' || codePoint >= ' ' && codePoint <= '\ud7ff' || codePoint >= '\ue000' && codePoint <= '�' || codePoint >= 65536 && codePoint <= 1114111;
    }


}
