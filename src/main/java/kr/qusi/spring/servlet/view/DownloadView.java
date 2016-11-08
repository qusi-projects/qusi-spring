package kr.qusi.spring.servlet.view;

import kr.qusi.spring.servlet.view.encoding.DefaultFilenameEncoder;
import kr.qusi.spring.servlet.view.encoding.FilenameEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 다운로드 View
 *
 * @since 0.0.1
 * @author yongseoklee
 */
@Slf4j
public class DownloadView extends AbstractView {

    private File file;

    private String filename;

    private FilenameEncoder filenameEncoder = new DefaultFilenameEncoder();

    public DownloadView(String path) {
        this(new File(path), null);
    }

    public DownloadView(String path, String filename) {
        this(new File(path), filename);
    }

    public DownloadView(String path, String filename, String contentType) {
        this(new File(path), filename, contentType);
    }

    public DownloadView(File file) {
        this(file, null, null);
    }

    public DownloadView(File file, String filename) {
        this(file, filename, null);
    }

    public DownloadView(File file, String filename, String contentType) {
        this.file = file;
        this.filename = filename;
        if (contentType != null)
            this.setContentType(contentType);
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
        this.prepareAttachmentFilename(request, response, this.getFilename());

        this.setResponseContentType(request, response);

        FileInputStream inputStream = new FileInputStream(this.getFile());
        ServletOutputStream outputStream = response.getOutputStream();

        FileCopyUtils.copy(inputStream, outputStream);

        outputStream.flush();
    }

    public File getFile() {
        return file;
    }

    public String getFilename() {
        if (StringUtils.isEmpty(this.filename))
            return FilenameUtils.getName(this.getFile().getAbsolutePath());

        return filename;
    }

    public void setFilenameEncoder(FilenameEncoder filenameEncoder) {
        this.filenameEncoder = filenameEncoder;
    }

}