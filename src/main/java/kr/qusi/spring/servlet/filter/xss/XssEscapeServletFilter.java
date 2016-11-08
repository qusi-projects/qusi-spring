package kr.qusi.spring.servlet.filter.xss;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 네이버 루시 서블릿 필터에서 ContextPath 대응되지 않아 수정
 *
 * @author yongseoklee
 * @see com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter
 */
public class XssEscapeServletFilter implements Filter {

    private XssEscapeFilter xssEscapeFilter = XssEscapeFilter.getInstance();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XssEscapeServletFilterWrapper(request, xssEscapeFilter), response);
    }

    @Override
    public void destroy() {

    }

}
