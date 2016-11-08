package kr.qusi.spring.servlet.view.encoding;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public class DefaultFilenameEncoder extends FilenameEncoder {

    @Override
    public String encode(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        String userAgentString = request.getHeader("User-Agent");
        if (userAgentString == null)
            return filename;

        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        Browser browser = userAgent.getBrowser();
        String encoding = request.getCharacterEncoding();

        if (Browser.IE.equals(browser.getGroup()))
            return java.net.URLEncoder.encode(filename, encoding);
        else
            return new String(filename.getBytes(encoding), "ISO-8859-1");
    }

}
