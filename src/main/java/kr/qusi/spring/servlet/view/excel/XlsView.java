package kr.qusi.spring.servlet.view.excel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.qusi.spring.servlet.view.encoding.DefaultFilenameEncoder;
import kr.qusi.spring.servlet.view.encoding.FilenameEncoder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

@Slf4j
public class XlsView extends AbstractUrlBasedView {

    public static final String BUNDLE = Bundle.class.getName();

    private static final String SUFFIX = ".xls";

    private static final String CONTENT_TYPE = "application/vnd.ms-excel";

    @Getter
    @Setter
    private String suffix;

    @Getter
    @Setter
    private FilenameEncoder filenameEncoder;

    public XlsView() {
        this(null);
    }

    public XlsView(String url) {
        super(url);
        setSuffix(SUFFIX);
        setContentType(CONTENT_TYPE);
        setFilenameEncoder(new DefaultFilenameEncoder());
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isValid(model)) {
            throw new IllegalArgumentException("Bundle not found");
        }

        Bundle bundle = (Bundle) model.get(BUNDLE);
        String filename = getFilename(request, bundle);
        Resource template = getTemplate(request, bundle);

        // 다운로드 헤더 준비
        prepareAttachment(request, response, filename);

        String basename = FilenameUtils.getBaseName(filename);
        File tmpDir = new File("C:/Users/yongseoklee/Desktop", basename);
        tmpDir.mkdir();

        // 엑셀 변환
        List<Map<String, Object>> extras = bundle.getExtrasAsList();
        for (int i = 0; i < extras.size(); i++) {
            Map<String, Object> map = extras.get(i);
            XLSTransformer transformer = new XLSTransformer();
            Workbook workbook = transformer.transformXLS(template.getInputStream(), map);

            FileOutputStream fos = new FileOutputStream(new File(tmpDir, i + "_" + filename));
            workbook.write(fos);
            IOUtils.closeQuietly(fos);
        }

        prepareAttachment(request, response, basename +".zip");
        setContentType("application/zip");

        ServletOutputStream out = response.getOutputStream();
        ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(out));
        for (File f : tmpDir.listFiles()) {
            zip.putNextEntry(new ZipEntry(f.getName()));
            IOUtils.copy(new FileInputStream(f), zip);
        }

        zip.close();


        // 다운로드
//        ServletOutputStream out = response.getOutputStream();
//        workbook.write(out);
//        out.flush();
    }

    protected void splitExtras() {

    }

    protected boolean isSplit(Map<String, Object> extras) {
        if (extras == null || extras.size() == 0)
            return false;

        final int limit = 10240;

        for (Object o : extras.values()) {
            if (o instanceof Collection && limit < ((Collection) o).size()) {
                return true;
            }
            else if (o instanceof Map && limit < ((Map) o).size()) {
                return true;
            }
        }

        return false;
    }

    protected boolean isValid(Map<String, Object> model) {
        return model.get(BUNDLE) != null && model.get(BUNDLE) instanceof Bundle;
    }

    protected String getFilename(HttpServletRequest request, Bundle bundle) throws FileNotFoundException {
        String filename = bundle.getFilename() != null
            ? bundle.getFilename() : getTemplate(request, bundle).getFilename();

        return FilenameUtils.getBaseName(filename) + getSuffix();
    }

    protected Resource getTemplate(HttpServletRequest request, Bundle bundle) throws FileNotFoundException {
        // 템플릿 파일목록 생성
        List<String> tmplNames = new ArrayList<>();
        if (bundle.getTemplateName() != null)
            tmplNames.add(bundle.getTemplateName() + getSuffix());
        tmplNames.add(getUrl());

        // 템플릿 파일 찾기
        for (String tmplName : tmplNames) {
            Resource template = new ServletContextResource(request.getServletContext(), tmplName);
            if (template.exists())
                return template;
        }

        throw new FileNotFoundException("Excel template not found");
    }

    /**
     * 다운로드 헤더 준비
     *
     * @param request
     * @param response
     * @param filename
     * @throws UnsupportedEncodingException
     */
    protected void prepareAttachment(HttpServletRequest request, HttpServletResponse response, String filename) throws UnsupportedEncodingException {
        String encodeFilename = getFilenameEncoder().encode(request, filename);
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", encodeFilename));
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    public static class Bundle {

        private static final int DEFAULT_LIMIT = 10000;

        @Getter
        @Setter
        private String filename;

        @Getter
        @Setter
        private String templateName;

        @Getter
        @Setter
        private int limit = DEFAULT_LIMIT;

        private Map<String, Object> extras;

        public void addExtra(String key, Object value) {
            if (extras == null)
                extras = new HashMap<>();

            if (value instanceof Map) {
                extras.put(key, new LinkedHashMap((Map) value));
            } else {
                extras.put(key, value);
            }
        }

        public Object getExtra(String key) {
            return extras != null ? extras.get(key) : null;
        }

        public List<Map<String, Object>> getExtrasAsList() {
            if (extras == null)
                return null;

            int maxSize = getMaxSize();
            int count = (int) Math.ceil((double) maxSize / (double) getLimit());
            List<Map<String, Object>> list = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                int startIdx = i * getLimit();
                int endIdx = (i + 1) * getLimit();

                Map<String, Object> extra = new HashMap<>();

                for (String extraKey : extras.keySet()) {
                    Object oObj = extras.get(extraKey);
                    Object nObj = oObj;

                    if (oObj instanceof List) {
                        List oList = ((List) oObj);

                        if (startIdx < oList.size()) {
                            nObj = oList.subList(startIdx, Math.min(endIdx, oList.size()));
                        } else {
                            nObj = Collections.EMPTY_LIST;
                        }
                    }
                    else if (oObj instanceof Map) {
                        Map oMap = ((Map) oObj);

                        if (startIdx < oMap.size()) {
                            List<String> oKeys = new ArrayList<String>(oMap.keySet())
                                .subList(startIdx, Math.min(endIdx, oMap.size()));

                            nObj = new HashMap<String, Object>();
                            for (String oKey : oKeys) {
                                ((Map) nObj).put(oKey, oMap.get(oKey));
                            }
                        } else {
                            nObj = Collections.EMPTY_MAP;
                        }
                    }

                    extra.put(extraKey, nObj);
                }

                list.add(extra);
            }

            return list;
        }

        public int getMaxSize() {
            if (extras == null)
                return 0;

            int maxSize = 0;
            for (Object o : extras.values()) {
                if (o instanceof List) {
                    maxSize = Math.max(maxSize, ((List) o).size());
                }
                else if (o instanceof Map) {
                    maxSize = Math.max(maxSize, ((Map) o).size());
                }
            }

            return maxSize;
        }
    }

}
