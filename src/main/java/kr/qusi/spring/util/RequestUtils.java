package kr.qusi.spring.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class RequestUtils {

    protected RequestUtils() {

    }

    /**
     * 현재 Context 에서 HttpServletRequest 취득
     *
     * @return {@link HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return null;

        return ((ServletRequestAttributes) attributes).getRequest();
    }

    /**
     * 현재 Context 에서 HttpSession 취득
     *
     * @return {@link HttpSession}
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        if (request == null)
            return null;

        return request.getSession(false);
    }

    /**
     * Remote IP (Client IP) 취득
     *
     * @param request
     * @return ip or null
     * @see <a href="http://lesstif.com/pages/viewpage.action?pageId=20775886">
     * Proxy(프락시) 환경에서 client IP 를 얻기 위한 X-Forwarded-For(XFF) http header</a>
     */
    public static String getCurrentIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        String[] ipSplit = StringUtils.split(ip, ",");
        if (ipSplit == null)
            return null;

        return ipSplit[0];
    }

    /**
     * RequestURL 및 QueryString 획득
     *
     * @param request
     * @return
     */
    public static String getRequestURLWithQueryString(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && 0 < queryString.length())
            requestURL.append("?").append(queryString);

        return requestURL.toString();
    }

    /**
     * RequestURI 및 QueryString 획득
     *
     * @param request
     * @return
     */
    public static String getRequestURIWithQueryString(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null && 0 < queryString.length())
            requestURI = requestURI + "?" + queryString;

        return requestURI;
    }

}
