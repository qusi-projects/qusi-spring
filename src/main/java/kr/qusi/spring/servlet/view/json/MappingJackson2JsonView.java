package kr.qusi.spring.servlet.view.json;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JSON View
 *
 * @since 0.0.1
 * @author yongseoklee
 */
@Slf4j
public class MappingJackson2JsonView extends org.springframework.web.servlet.view.json.MappingJackson2JsonView {

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        super.prepareResponse(request, response);

        // IE9이하에서 Response의 ContentType 이 application/json 경우 간혹 다운로드 액션이 작동하는
        // 이슈가 있음.
        // 해당 이슈를 해결하기 위해 IE9 하위버전은 ContentType을 text/html 으로 설정함.
        // http://blog.degree.no/2012/09/jquery-json-ie8ie9-treats-response-as-downloadable-file
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser browser = userAgent.getBrowser();

        if (Browser.IE5.equals(browser) || Browser.IE5_5.equals(browser) || Browser.IE6.equals(browser)
                || Browser.IE7.equals(browser) || Browser.IE8.equals(browser) || Browser.IE9.equals(browser)) {
            log.debug("[{} Compatibility] ContentType 변경, application/json to text/html", browser);

            response.setContentType("text/html");
        }
    }

}
