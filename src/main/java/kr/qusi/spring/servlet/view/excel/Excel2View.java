package kr.qusi.spring.servlet.view.excel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class Excel2View extends AbstractUrlBasedView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

    }

    protected void zip(File src, OutputStream dest) {
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(dest));

    }

}
