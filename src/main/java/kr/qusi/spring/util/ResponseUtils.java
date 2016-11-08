package kr.qusi.spring.util;

import javax.servlet.http.HttpServletResponse;

public abstract class ResponseUtils {

    protected ResponseUtils() {

    }

    public static void setNoCache(HttpServletResponse response) {
        // Setting response's headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
                                                                                    // 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.
    }

}
