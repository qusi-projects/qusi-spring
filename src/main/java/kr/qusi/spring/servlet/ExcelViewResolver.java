package kr.qusi.spring.servlet;

import kr.qusi.spring.servlet.view.excel.ExcelView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Excel ViewResolver
 *
 * @since 0.0.1
 * @author yongseoklee
 */
public class ExcelViewResolver extends UrlBasedViewResolver {

    public ExcelViewResolver() {
        setViewClass(requiredViewClass());
    }

    @Override
    protected Class<?> requiredViewClass() {
        return ExcelView.class;
    }

    @Deprecated
    @Override
    public void setSuffix(String suffix) {
        super.setSuffix(null);
    }

}
