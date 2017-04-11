package kr.qusi.spring.servlet;

import kr.qusi.spring.servlet.view.excel.XlsView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

public class XlsViewResolver extends UrlBasedViewResolver {

    public XlsViewResolver() {
        setViewClass(requiredViewClass());
    }

    @Override
    protected Class<?> requiredViewClass() {
        return XlsView.class;
    }

    @Override
    protected XlsView buildView(String viewName) throws Exception {
        XlsView view = (XlsView) super.buildView(viewName);

        String suffix = getSuffix();
        if (suffix != null) {
            view.setSuffix(suffix);
        }

        return view;
    }

}
