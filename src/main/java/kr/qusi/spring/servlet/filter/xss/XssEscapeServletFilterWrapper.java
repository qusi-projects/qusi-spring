package kr.qusi.spring.servlet.filter.xss;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 네이버 루시 서블릿 필터에서 ContextPath 대응되지 않아 수정
 *
 * 1. {@link #path} 초기화시 ContextPath 삭제처리
 * 2. {@link #getParameterMap()} ReturnType 오류수정
 * 변경전: Map<String, Object> getParameterMap();
 * 변경후: Map<String, String[]> getParameterMap();
 *
 * @author yongseoklee
 * @see com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilterWrapper
 */
public class XssEscapeServletFilterWrapper extends HttpServletRequestWrapper {

    private XssEscapeFilter xssEscapeFilter;

    private String path = null;

    public XssEscapeServletFilterWrapper(ServletRequest request, XssEscapeFilter xssEscapeFilter) {
        super((HttpServletRequest) request);
        this.xssEscapeFilter = xssEscapeFilter;
        this.path = ((HttpServletRequest) request).getRequestURI();
        this.path = this.path.replaceFirst(((HttpServletRequest) request).getContextPath(), "");
    }

    @Override
    public String getParameter(String paramName) {
        String value = super.getParameter(paramName);
        return doFilter(paramName, value);
    }

    @Override
    public String[] getParameterValues(String paramName) {
        String values[] = super.getParameterValues(paramName);
        if (values == null) {
            return values;
        }
        for (int index = 0; index < values.length; index++) {
            values[index] = doFilter(paramName, values[index]);
        }
        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramMap = super.getParameterMap();
        Map<String, String[]> newFilteredParamMap = new HashMap<String, String[]>();

        Set<Map.Entry<String, String[]>> entries = paramMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String paramName = entry.getKey();
            String[] valueObj = entry.getValue();
            String[] filteredValue = new String[valueObj.length];
            for (int index = 0; index < valueObj.length; index++) {
                filteredValue[index] = doFilter(paramName, valueObj[index]);
            }

            newFilteredParamMap.put(entry.getKey(), filteredValue);
        }

        return newFilteredParamMap;
    }

    /**
     * @param paramName String
     * @param value String
     * @return String
     */
    private String doFilter(String paramName, String value) {
        return xssEscapeFilter.doFilter(path, paramName, value);
    }

}
