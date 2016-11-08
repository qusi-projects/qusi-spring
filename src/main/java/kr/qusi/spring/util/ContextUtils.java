package kr.qusi.spring.util;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * Spring Context 유틸
 * ServletContext, ApplicationContext 또는 Bean 을 취득하는 역활을 한다.
 * 단, {@link ContextUtils} 은 스프링프레임워크 생명주기에서 작동하며 그 외는 Null 을 반환한다.
 *
 * @author yongseoklee
 */
public abstract class ContextUtils {

    protected ContextUtils() {

    }

    /**
     * ServletContext 취득
     *
     * @return {@link ServletContext} or null
     */
    public static ServletContext getServletContext() {
        HttpSession session = RequestUtils.getSession();
        if (session == null)
            return null;

        return session.getServletContext();
    }

    /**
     * ApplicationContext 취득
     *
     * @return {@link ApplicationContext} or null
     */
    public static ApplicationContext getApplicationContext() {
        ServletContext servletContext = getServletContext();
        if (servletContext == null)
            return null;

        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }

    /**
     * Application 에 등록된 Bean 취득
     *
     * @param name  Bean 이름
     * @param clazz Bean 클래스
     * @return Bean or null
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext == null)
            return null;

        return applicationContext.getBean(name, clazz);
    }

    /**
     * Application 에 등록된 Bean 취득
     * 클래스 유형으로 등록된 빈 반환
     *
     * @param clazz Bean 클래스
     * @return Bean or null
     */
    public static <T> T getBean(Class<T> clazz) {
        ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext == null)
            return null;

        return applicationContext.getBean(clazz);
    }

}
