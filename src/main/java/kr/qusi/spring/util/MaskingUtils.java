package kr.qusi.spring.util;

/**
 * 문자열 마스킹 유틸리티
 *
 * @author yongseoklee
 * @since 0.5.0
 */
public abstract class MaskingUtils {

    protected MaskingUtils() {

    }

    public static final char DEFAULT_REPLACE = '*';

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param startIdx 시작위치
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, int startIdx) {
        return masking(src, DEFAULT_REPLACE, null, startIdx, src.length());
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param startIdx 시작위치
     * @param length   길이
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, int startIdx, int length) {
        return masking(src, DEFAULT_REPLACE, null, startIdx, length);
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param replace  대치문자
     * @param startIdx 시작위치
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, char replace, int startIdx) {
        return masking(src, replace, null, startIdx, src.length());
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param replace  대치문자
     * @param startIdx 시작위치
     * @param length   길이
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, char replace, int startIdx,
                                 int length) {
        return masking(src, replace, null, startIdx, length);
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param replace  대치문자
     * @param exclude  제외문자
     * @param startIdx 시작위치
     * @param length   길이
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, char replace, char[] exclude,
                                 int startIdx, int length) {
        StringBuffer sb = new StringBuffer(src);

        // 종료 인덱스
        int endIdx = startIdx + length;
        if (sb.length() < endIdx)
            endIdx = sb.length();

        // 치환
        for (int i = startIdx; i < endIdx; i++) {
            boolean isExclude = false;
            // 제외 문자처리
            if (exclude != null && 0 < exclude.length) {
                char currentChar = sb.charAt(i);

                for (char excludeChar : exclude) {
                    if (currentChar == excludeChar)
                        isExclude = true;
                }
            }

            if (!isExclude)
                sb.setCharAt(i, replace);
            // sb.replace(i, i + 1, replace);
        }

        return sb.toString();
    }

}
