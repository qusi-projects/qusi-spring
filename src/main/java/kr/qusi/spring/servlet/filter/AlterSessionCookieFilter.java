package kr.qusi.spring.servlet.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class AlterSessionCookieFilter extends OncePerRequestFilter {

    public static final String DEFAULT_COOKIE_NAME = "JSESSIONID";

    private boolean httpOnly = true;

    private String cookieName = DEFAULT_COOKIE_NAME;

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        SessionSecureCookieHttpServletRequestWrapper wrapper = new SessionSecureCookieHttpServletRequestWrapper(
                request);
        wrapper.setResponse(response);
        wrapper.setHttpOnly(this.httpOnly);
        wrapper.setSessionCookieName(this.cookieName);

        filterChain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {

    }

    public static class SessionSecureCookieHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private static final String ALREADY_OVERWRITTEN_SUFFIX = ".OVERWRITTEN";

        private HttpServletResponse response;

        private boolean httpOnly = true;

        private String sessionCookieName = null;

        public SessionSecureCookieHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        private void doFilter(HttpSession session) {
            if (session == null || response == null || this.getSessionCookieName() == null)
                return;

            Object overwritten = this.getAttribute(this.getOverwrittenAttributeName());

            if (overwritten == null && isSecure()) {
                Cookie cookie = new Cookie(this.getSessionCookieName(), session.getId());
                cookie.setMaxAge(-1);
                cookie.setSecure(false);
                cookie.setHttpOnly(this.isHttpOnly());

                String contextPath = getContextPath();
                if (contextPath != null && 0 < contextPath.length())
                    cookie.setPath(contextPath);
                else
                    cookie.setPath("/");

                response.addCookie(cookie);
                this.setAttribute(this.getOverwrittenAttributeName(), Boolean.TRUE);
            }
        }

        @Override
        public HttpSession getSession() {
            HttpSession session = super.getSession();
            doFilter(session);
            return session;
        }

        @Override
        public HttpSession getSession(boolean create) {
            HttpSession session = super.getSession(create);
            doFilter(session);
            return session;
        }

        public void setResponse(HttpServletResponse response) {
            this.response = response;
        }

        public boolean isHttpOnly() {
            return httpOnly;
        }

        public void setHttpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
        }

        public String getSessionCookieName() {
            return sessionCookieName;
        }

        public void setSessionCookieName(String sessionCookieName) {
            this.sessionCookieName = sessionCookieName;
        }

        protected String getOverwrittenAttributeName() {
            return getClass().getName() + ALREADY_OVERWRITTEN_SUFFIX;
        }

    }

}
