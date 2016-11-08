package kr.qusi.spring.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * 로그인 매니저
 * 
 * @since 1.0.0
 * @author yongseoklee
 */
public abstract class AbstractLoginManager {

    protected AbstractLoginManager() {

    }

    /**
     * 로그인 여부
     * 
     * @return true or false
     */
    public static boolean isLogin() {
        return getAuthentication() != null;
    }

    /**
     * Authentication 조회
     * 
     * @return Authentication
     */
    public static Authentication getAuthentication() {
        SecurityContext context = null;
        try {
            context = SecurityContextHolder.getContext();
        }
        catch (Exception e) {

        }
        if (context == null)
            return null;

        Authentication authentication = context.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken)
            return null;

        return authentication;
    }

    /**
     * Default 조회
     *
     * @return
     */
    public static Object getDetails() {
        Authentication authentication = getAuthentication();
        if (authentication == null)
            return null;

        return authentication.getDetails();
    }

    /**
     * 권한 체크
     *
     * @param role
     * @return true or false
     */
    public static boolean hasRole(String role) {
        Authentication authentication = getAuthentication();
        if (authentication == null)
            return false;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null)
            return false;

        return authorities.contains(new SimpleGrantedAuthority(role));
    }

}
