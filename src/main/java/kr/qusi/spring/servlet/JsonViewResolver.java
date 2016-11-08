package kr.qusi.spring.servlet;

import kr.qusi.spring.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

/**
 * @since 0.0.1
 * @author yongseoklee
 */
public class JsonViewResolver implements ViewResolver {

    @Override
    public View resolveViewName(String s, Locale locale) throws Exception {
        return new MappingJackson2JsonView();
    }

}
