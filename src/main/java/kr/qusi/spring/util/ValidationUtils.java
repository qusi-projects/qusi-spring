package kr.qusi.spring.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 검증 유틸리티 클래스
 * 
 * @since 1.0.0
 * @author yongseoklee
 */
@Slf4j
public abstract class ValidationUtils {

    /* =======================================
     * 검증오류 발생시 사용할 메시지의 키 상수
     * ======================================= */
    /** 필수 입력 **/
    protected static String CODE_REQUIRED = "valid.required";

    /** 최대 길이 제한 **/
    protected static String CODE_MAX_LENGTH = "valid.maxLength";

    /** 최소 길이 제한 **/
    protected static String CODE_MIN_LENGTH = "valid.minLength";

    /** 문자열 범위 제한 **/
    protected static String CODE_RANGE = "valid.range";

    /** 이메일 **/
    protected static String CODE_EMAIL = "valid.email";

    /** 무선전화 형식 **/
    protected static String CODE_MOBILE = "valid.mobile";

    /** 유선전화 형식 **/
    protected static String CODE_TEL = "valid.tel";

    /** 유선 & 무선 전화번호 형식 **/
    protected static String CODE_TEL_OR_MOBILE = "valid.telOrMobile";

    /** 숫자만 **/
    protected static String CODE_NUMERIC = "valid.numeric";

    /** Y or N **/
    protected static String CODE_YN = "valid.yn";

    /* =======================================
     * 검증 패턴
     * ======================================= */
    /** 숫자 **/
    public static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");

    /** 휴대전화 */
    public static final Pattern MOBILE_PATTERN = Pattern.compile("^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$");

    /** 유선전화 */
    public static final Pattern TEL_PATTERN = Pattern.compile("^(02|0[3-9]{1}[0-9]{1})-(\\d{4}|\\d{3})-\\d{4}$");

    /** Y or N */
    public static final Pattern YN_PATTERN = Pattern.compile("^(Y|N)$");

    protected ValidationUtils() {

    }

    /**
     * RequestParam, PathVariable 용 BindingResult
     * Controller 에서 ModelAttribute 를 사용하지 않는 경우 BindingResult 를 생성하지 못하는 오류가
     * 발생합니다.
     * Controller 에 있는 BindingResult 를 삭제하고 createBindingResult 를 사용하면 됩니다.
     *
     * @param request
     * @return BindingResult
     */
    public static BindingResult createBindingResult(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");

        Map<String, Object> map = new LinkedHashMap<String, Object>();

        // Request Params
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames != null && parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = request.getParameter(key);
            log.debug("[RequestParam] key: {}, value: {}", key, value);

            map.put(key, value);
        }

        // Path Variables
        if (request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) != null) {
            Map<String, Object> pathVariables = (Map) request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            for (String key : pathVariables.keySet()) {
                Object value = pathVariables.get(key);
                log.debug("[PathVariable] key: {}, value: {}", key, value);

                map.put(key, value);
            }
        }

        return new MapBindingResult(map, "map");
    }

    /**
     * 입력 값이 비어있거나 Null 인 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     */
    public static void required(Errors errors, Object target, String field, String label) {
        required(errors, target, field, label, CODE_REQUIRED);
    }

    /**
     * 입력 값이 비어있거나 Null 인 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     * @param code
     */
    public static void required(Errors errors, Object target, String field, String label, String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (target == null || StringUtils.isEmpty(String.valueOf(target)))
            errors.rejectValue(field, code, new Object[] { label }, null);
    }

    /**
     * 입력 값의 최대 길이 넘는 경우 Reject
     *
     * @param errors
     * @param target
     * @param length
     * @param field
     * @param label
     */
    public static void maxLength(Errors errors, String target, int length, String field, String label) {
        maxLength(errors, target, length, field, label, CODE_MAX_LENGTH);
    }

    /**
     * 입력 값의 최대 길이 넘는 경우 Reject
     *
     * @param errors
     * @param target
     * @param length
     * @param field
     * @param label
     * @param code
     */
    public static void maxLength(Errors errors, String target, int length, String field, String label,
                                 String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (length < StringUtils.length(target))
            errors.rejectValue(field, code, new Object[] { label, length }, null);
    }

    /**
     * 입력 값의 최소 길이보다 작은 경우 Reject
     *
     * @param errors
     * @param target
     * @param length
     * @param field
     * @param label
     */
    public static void minLength(Errors errors, String target, int length, String field, String label) {
        minLength(errors, target, length, field, label, CODE_MIN_LENGTH);
    }

    /**
     * 입력 값의 최소 길이보다 작은 경우 Reject
     *
     * @param errors
     * @param target
     * @param length
     * @param field
     * @param label
     * @param code
     */
    public static void minLength(Errors errors, String target, int length, String field, String label,
                                 String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (StringUtils.length(target) < length)
            errors.rejectValue(field, code, new Object[] { label, length }, null);
    }

    /**
     * 문자열 길이가 범위에 포함되지 않는경우 Reject
     *
     * @param errors
     * @param target
     * @param min
     * @param max
     * @param field
     * @param label
     */
    public static void range(Errors errors, String target, int min, int max, String field, String label) {
        range(errors, target, min, max, field, label, CODE_RANGE);
    }

    /**
     * 문자열 길이가 범위에 포함되지 않는경우 Reject
     *
     * @param errors
     * @param target
     * @param min
     * @param max
     * @param field
     * @param label
     * @param code
     */
    public static void range(Errors errors, String target, int min, int max, String field, String label,
                             String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");
        if (max < min)
            throw new IllegalArgumentException("Min must be less than a Max");

        int length = StringUtils.length(target);
        if (!(min < length && length < max))
            errors.rejectValue(field, code, new Object[] { label, min, max }, null);
    }

    /**
     * 입력 값의 이메일형식이 아닌 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     */
    public static void email(Errors errors, String target, String field, String label) {
        email(errors, target, field, label, CODE_EMAIL);
    }

    /**
     * 입력 값의 이메일형식이 아닌 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     * @param code
     */
    public static void email(Errors errors, String target, String field, String label, String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (!EmailValidator.getInstance().isValid(target))
            errors.rejectValue(field, code, new Object[] { label }, null);
    }

    /**
     * 선 전화번호 형식이 아닌 경우 Reject
     * 010, 011, 016, 017, 018, 019 허용
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     */
    public static void mobile(Errors errors, String target, String field, String label) {
        mobile(errors, target, field, label, CODE_MOBILE);
    }

    /**
     * 무선 전화번호 형식이 아닌 경우 Reject
     * 010, 011, 016, 017, 018, 019 허용
     *
     * 010-1234-5678 (O)
     * 011-111-2222 (O)
     * 011-2222-3333 (O)
     *
     * 070-2222-3333 (X)
     * 010-22223-333 (X)
     * 011-22-33333 (X)
     * 01011112222 (X)
     * 010-22223333 (X)
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     * @param code
     */
    public static void mobile(Errors errors, String target, String field, String label, String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (target == null || !MOBILE_PATTERN.matcher(target).find())
            errors.rejectValue(field, code, new Object[] { label }, null);
    }

    /**
     * 유선 전화번호 형식이 아닌 경우 Reject
     * 02, 031, 032 등 허용
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     */
    public static void tel(Errors errors, String target, String field, String label) {
        tel(errors, target, field, label, CODE_TEL);
    }

    /**
     * 유선 전화번호 형식이 아닌 경우 Reject
     * 02, 031, 032 등 허용
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     * @param code
     */
    public static void tel(Errors errors, String target, String field, String label, String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (target == null || !TEL_PATTERN.matcher(target).find())
            errors.rejectValue(field, code, new Object[] { label }, null);
    }

    /**
     * 유선 & 무선 전화번호 형식이 아닌 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     */
    public static void telOrMobile(Errors errors, String target, String field, String label) {
        telOrMobile(errors, target, field, label, CODE_TEL_OR_MOBILE);
    }

    /**
     * 유선 & 무선 전화번호 형식이 아닌 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     * @param code
     */
    public static void telOrMobile(Errors errors, String target, String field, String label, String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (target == null || !(TEL_PATTERN.matcher(target).find() || MOBILE_PATTERN.matcher(target).find()))
            errors.rejectValue(field, code, new Object[] { label }, null);
    }

    /**
     * 입력 값이 숫자만 입력이 아닌 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     */
    public static void numeric(Errors errors, String target, String field, String label) {
        numeric(errors, target, field, label, CODE_NUMERIC);
    }

    /**
     * 입력 값이 숫자만 입력이 아닌 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     * @param code
     */
    public static void numeric(Errors errors, String target, String field, String label, String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (target == null || !NUMERIC_PATTERN.matcher(target).find())
            errors.rejectValue(field, code, new Object[] { label }, null);
    }


    /**
     * 입력 값이 Y 또는 N 아닌 경우 Reject
     *
     * @param errors
     * @param target
     * @param field
     * @param label
     */
    public static void yn(Errors errors, String target, String field, String label) {
        yn(errors, target, field, label, CODE_YN);
    }

    /**
     * 입력 값이 Y 또는 N 아닌 경우 Reject
     * 
     * @param errors
     * @param target
     * @param field
     * @param label
     * @param code
     */
    public static void yn(Errors errors, String target, String field, String label, String code) {
        Assert.notNull(label, "Label must not be null");
        Assert.notNull(errors, "Errors object must not be null");

        if (target == null || !YN_PATTERN.matcher(target).find())
            errors.rejectValue(field, code, new Object[] { label }, null);
    }

}
