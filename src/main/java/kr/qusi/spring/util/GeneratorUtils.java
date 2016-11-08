package kr.qusi.spring.util;

import java.util.Random;

/**
 * 문자열 무작위 생성
 *
 * @author yongseoklee
 */
public abstract class GeneratorUtils {

    protected GeneratorUtils() {

    }

    public static final String ALPHABET = "alphabet";

    public static final String ALPHABET_UPPER = "alphabet_upper";

    public static final String ALPHABET_LOWER = "alphabet_lower";

    public static final String NUMERIC = "numeric";

    public static final String ALPHANUMERIC = "alphanumeric";

    public static final char[] ALPHABET_UPPER_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static final char[] ALPHABET_LOWER_ARRAY = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static final char[] ALPHABET_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static final char[] NUMERIC_ARRAY = "0123456789".toCharArray();

    public static final char[] ALPHANUMERIC_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            .toCharArray();

    public static StringBuffer createAlphabet(int length) {
        return createString(ALPHABET, length);
    }

    public static StringBuffer createAlphabetUpper(int length) {
        return createString(ALPHABET_UPPER, length);
    }

    public static StringBuffer createAlphabetLower(int length) {
        return createString(ALPHABET_LOWER, length);
    }

    public static StringBuffer createNumeric(int length) {
        return createString(NUMERIC, length);
    }

    public static StringBuffer createAlphaNumeric(int length) {
        return createString(ALPHANUMERIC, length);
    }

    public static StringBuffer createString(String type, int length) {
        char[] arary = getArary(type);
        if (arary == null)
            return null;

        StringBuffer result = new StringBuffer("");

        for (int i = 0; i < length; i++)
            result.append(arary[new Random().nextInt(arary.length)]);

        return result;
    }

    public static char[] getArary(String type) {
        if (("" + type).equals(ALPHABET))
            return ALPHABET_ARRAY;
        if (("" + type).equals(ALPHABET_UPPER))
            return ALPHABET_UPPER_ARRAY;
        if (("" + type).equals(ALPHABET_LOWER))
            return ALPHABET_LOWER_ARRAY;
        else if (("" + type).equals(NUMERIC))
            return NUMERIC_ARRAY;
        else if (("" + type).equals(ALPHANUMERIC))
            return ALPHANUMERIC_ARRAY;

        return null;
    }

}
