package kr.qusi.spring.servlet;

import kr.qusi.spring.servlet.view.excel.Excel2View;
import kr.qusi.spring.servlet.view.excel.XlsView;
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
    protected XlsView buildView(String viewName) throws Exception {
        XlsView view = (XlsView) super.buildView(viewName);

        String suffix = getSuffix();
        if (suffix != null) {
            view.setSuffix(suffix);
        }

        if (suffix != null && getContentType() == null) {
            if (Excel2View.CONTENT_TYPE_XLS.equalsIgnoreCase(suffix)) {
                view.setContentType(Excel2View.CONTENT_TYPE_XLS);
            }
            else if (Excel2View.CONTENT_TYPE_XLSX.equalsIgnoreCase(suffix)) {
                view.setContentType(Excel2View.CONTENT_TYPE_XLSX);
            }
        }

        return view;
    }

}
