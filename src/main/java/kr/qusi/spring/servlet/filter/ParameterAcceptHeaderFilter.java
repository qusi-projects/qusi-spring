package kr.qusi.spring.servlet.filter;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Request Parameter 를 이용하여 Accept 헤더변조
 * <p/>
 * IE 하위버전에서 Accept를 올바르지 않게 전송하여, 요청시 Accept로 구분할 수 없다.
 * 이를 보완하고자 {@link ParameterAcceptHeaderFilter}는 Request parameter에 Extension로 Accept를 핸들링한다.
 * 마치 {@link org.springframework.web.servlet.view.ContentNegotiatingViewResolver}의 favorParameter 또는
 * {@link org.springframework.web.filter.HiddenHttpMethodFilter} 비슷하게 작동한다.
 *
 * @author yongseoklee
 * @see <a href="http://stackoverflow.com/questions/1670329/ie-accept-headers-changing-why">http://stackoverflow.com/questions/1670329/ie-accept-headers-changing-why</a>
 */
public class ParameterAcceptHeaderFilter extends OncePerRequestFilter {

    /**
     * {@link #parameterName} 기본값
     */
    public static final String DEFAULT_PARAMETER_NAME = "_format";

    /**
     * Request parameter 명
     */
    private String parameterName = DEFAULT_PARAMETER_NAME;

    /**
     * extension & accept 정의
     */
    private Map<String, String> acceptTypes;

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * extension & accept 정의
     * <p/>
     * extension 과 accept 는 콤마(,)로 구분하고, 여러행은 세미콜론(;)을 구분합니다.
     * <p/>
     * 예) html,json를 정의하는 방법
     * Extension: html, Accept: text/html
     * Extension: json, Accept: application/json
     * <pre>
     * <init-param>
     * <param-name>acceptTypes</param-name>
     * <param-value>html,text/html;json,application/json</param-value>
     * </init-param>
     * </pre>
     *
     * @param acceptTypes
     */
    public void setAcceptTypes(String acceptTypes) {
        if (acceptTypes == null)
            return;
        String[] _acceptTypes = acceptTypes.split(";");

        for (String _acceptType : _acceptTypes) {
            String[] split = _acceptType.split(",");

            if (this.acceptTypes == null)
                this.acceptTypes = new LinkedHashMap<String, String>();

            String extension = split[0].trim().toLowerCase(Locale.ENGLISH);
            String accept = split[1].trim();
            this.acceptTypes.put(extension, accept);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String parameterValue = request.getParameter(this.parameterName);

        if (acceptTypes != null && StringUtils.hasLength(parameterValue)) {
            String extension = parameterValue.toLowerCase(Locale.ENGLISH);
            String accept = this.acceptTypes.get(extension);

            if (StringUtils.hasLength(accept)) {
                HttpServletRequest wrapper = new AcceptHeaderRequestWrapper(request, accept);
                filterChain.doFilter(wrapper, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static class AcceptHeaderRequestWrapper extends HttpServletRequestWrapper {

        private static final String HEADER_NAME = "accept";

        private final String accept;

        private final Enumeration<String> accepts;

        public AcceptHeaderRequestWrapper(HttpServletRequest request, String accept) {
            super(request);
            this.accept = accept;

            ArrayList<String> list = new ArrayList<String>();
            list.add(accept);
            this.accepts = Collections.enumeration(list);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return name != null && HEADER_NAME.equals(name.toLowerCase(Locale.ENGLISH))
                    ? accepts : super.getHeaders(name);
        }

        @Override
        public String getHeader(String name) {
            return name != null && HEADER_NAME.equals(name.toLowerCase(Locale.ENGLISH))
                    ? accept : super.getHeader(name);
        }
    }

}