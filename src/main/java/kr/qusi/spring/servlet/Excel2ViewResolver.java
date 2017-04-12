package kr.qusi.spring.servlet;

import kr.qusi.spring.servlet.view.excel.Excel2View;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

public class Excel2ViewResolver extends UrlBasedViewResolver {

    public Excel2ViewResolver() {
        setViewClass(requiredViewClass());
    }

    @Override
    protected Class<?> requiredViewClass() {
        return Excel2View.class;
    }

    @Override
    protected Excel2View buildView(String viewName) throws Exception {
        Excel2View view = (Excel2View) super.buildView(viewName);
        view.setViewName(viewName);
        view.setPrefix(getPrefix());
        view.setSuffix(getSuffix());

        return view;
    }

    @Override
    public void setSuffix(String suffix) {
        super.setSuffix(suffix == null ? null : suffix.toLowerCase());
    }

}
