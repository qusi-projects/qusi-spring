package kr.qusi.spring.servlet.view.excel;

import kr.qusi.spring.servlet.view.encoding.DefaultFilenameEncoder;
import kr.qusi.spring.servlet.view.encoding.FilenameEncoder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Excel View
 *
 * @author yongseoklee
 * @since 0.0.1
 */
@Slf4j
public class ExcelView extends AbstractUrlBasedView {

    /**
     * Excel contentType
     */
    public static final String CONTENT_TYPE = "application/vnd.ms-excel";

    /**
     * Excel 97-2004 extension
     */
    public static final String DEFAULT_XLS_EXTENSION = "xls";

    /**
     * Excel 2007 or higher extension
     */
    public static final String DEFAULT_XLSX_EXTENSION = "xlsx";

    public static final String DEFAULT_FILENAME_KEY = "ms.excel.filename";

    private String filenameKey = DEFAULT_FILENAME_KEY;

    private FilenameEncoder filenameEncoder = new DefaultFilenameEncoder();

    public ExcelView() {
        setContentType(CONTENT_TYPE);
    }

    public ExcelView(String url) {
        super(url);
        setContentType(CONTENT_TYPE);
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    /**
     * 다운로드 헤더 준비
     *
     * @param request
     * @param response
     * @param filename
     * @throws UnsupportedEncodingException
     */
    protected void prepareAttachmentFilename(HttpServletRequest request, HttpServletResponse response, String filename)
            throws UnsupportedEncodingException {
        String encodeFilename = this.filenameEncoder.encode(request, filename);
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", encodeFilename));
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Resource template = this.getTemplateResource(request);
        String filename = this.getFilename(model, request);

        prepareAttachmentFilename(request, response, filename);

        XLSTransformer transformer = new XLSTransformer();

        Workbook workbook = transformer.transformXLS(template.getInputStream(), model);

        // Flush byte array to servlet output stream.
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

    public String getFilenameKey() {
        return filenameKey;
    }

    public void setFilenameKey(String filenameKey) {
        this.filenameKey = filenameKey;
    }

    /**
     * 다운로드 파일명 조회
     *
     * @param model
     * @return
     * @throws FileNotFoundException
     */
    public String getFilename(Map<String, Object> model, HttpServletRequest request) throws FileNotFoundException {
        String filename = this.getTemplateResource(request).getFilename();
        String extension = StringUtils.getFilenameExtension(filename);

        if (model.containsKey(this.getFilenameKey())) {
            filename = String.valueOf(model.get(this.getFilenameKey()));
            filename += ".";
            filename += extension;
        }

        log.debug("Excel filename: {}", filename);
        return filename;
    }

    /**
     * 템플릿 조회
     *
     * @return
     * @throws FileNotFoundException
     */
    public Resource getTemplateResource(HttpServletRequest request) throws FileNotFoundException {
        Resource template;

        // xls template
        template = new ServletContextResource(request.getServletContext(), this.getUrl() + "." + DEFAULT_XLS_EXTENSION);
        if (template.exists()) {
            log.debug("Excel template: {}", template);
            return template;
        }

        // xlsx template
        template = new ServletContextResource(request.getServletContext(),
                this.getUrl() + "." + DEFAULT_XLSX_EXTENSION);
        if (template.exists()) {
            log.debug("Excel template: {}", template);
            return template;
        }

        throw new FileNotFoundException("Excel template not found");
    }

    public void setFilenameEncoder(FilenameEncoder filenameEncoder) {
        this.filenameEncoder = filenameEncoder;
    }

}