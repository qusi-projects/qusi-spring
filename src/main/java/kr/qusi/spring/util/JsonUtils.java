package kr.qusi.spring.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Slf4j
public abstract class JsonUtils {

    protected JsonUtils() {

    }

    /**
     * Request 가 JSON 요청인지 판단
     *
     * @param request HttpServletRequest
     * @return true or false
     */
    public static boolean isJsonRequest(HttpServletRequest request) {
        if (request == null || request.getHeader("Accept") == null)
            return false;

        String accept = request.getHeader("Accept").toLowerCase(Locale.ENGLISH);
        return MediaType.APPLICATION_JSON_VALUE.equals(accept);
    }

    /**
     * Object 를 JSON 형태로 Response 에 직접 출력할때 사용
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param object   Json 변환 대상
     * @return 변환된 Json
     */
    public static String responseWriter(HttpServletRequest request, HttpServletResponse response, Object object) {
        return responseWriter(request, response, object, request.getCharacterEncoding());
    }

    /**
     * Object 를 JSON 형태로 Response 에 직접 출력할때 사용
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param object   Json 변환 대상
     * @param encoding 인코딩
     * @return 변환된 Json
     */
    public static String responseWriter(HttpServletRequest request, HttpServletResponse response, Object object, String encoding) {
        try {
            String json = new ObjectMapper().writeValueAsString(object);

            // IE9이하에서 Response 의 ContentType 이 application/json 경우 간혹 다운로드 액션이 작동하는
            // 이슈가 있음.
            // 해당 이슈를 해결하기 위해 IE9 하위버전은 ContentType 을 text/html 으로 설정함.
            // http://blog.degree.no/2012/09/jquery-json-ie8ie9-treats-response-as-downloadable-file
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            Browser browser = userAgent.getBrowser();

            if (Browser.IE5.equals(browser) || Browser.IE5_5.equals(browser) || Browser.IE6.equals(browser)
                    || Browser.IE7.equals(browser) || Browser.IE8.equals(browser) || Browser.IE9.equals(browser)) {
                log.debug("[{} Compatibility] ContentType 변경, application/json to text/html", browser);

                response.setContentType(MediaType.TEXT_HTML_VALUE);
            } else {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            }

            if (encoding != null) {
                response.setCharacterEncoding(encoding);
            }
            response.getWriter().write(json);

            return json;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
