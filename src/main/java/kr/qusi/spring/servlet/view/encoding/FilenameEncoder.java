package kr.qusi.spring.servlet.view.encoding;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public abstract class FilenameEncoder {

    public abstract String encode(HttpServletRequest request, String filename) throws UnsupportedEncodingException;

}
