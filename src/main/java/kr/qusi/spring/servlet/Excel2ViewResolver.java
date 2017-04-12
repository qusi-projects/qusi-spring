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
    public void setSuffix(String suffix) {
        String lower = suffix == null ? null : suffix.toLowerCase();
        if (!(Excel2View.EXTENSION_XLS.equals(lower) || Excel2View.EXTENSION_XLSX.equals(lower)))
            throw new IllegalArgumentException("'.xls' and '.xlsx' only");

        if (getContentType() == null) {
            if (Excel2View.EXTENSION_XLS.equals(lower)) {
                setContentType(Excel2View.CONTENT_TYPE_XLS);
            }
            else if (Excel2View.EXTENSION_XLSX.equals(lower)) {
                setContentType(Excel2View.CONTENT_TYPE_XLSX);
            }
        }

        super.setSuffix(lower);
    }

    @Override
    protected Excel2View buildView(String viewName) throws Exception {
        Excel2View view = (Excel2View) super.buildView(viewName);

        String suffix = getSuffix();
        if (suffix != null) {
            view.setSuffix(suffix);
        }

        return view;
    }

}
