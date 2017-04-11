package kr.qusi.spring.servlet.view.excel;

import kr.qusi.spring.servlet.view.encoding.FilenameEncoder;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Excel2View extends AbstractUrlBasedView {

    public static final String BUNDLE = Bundle.class.getName();

    /** Excel 2003 이하 확장자 */
    public static final String EXTENSION_XLS = ".xls";

    /** Excel 2003 이하 ContentType */
    public static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

    /** Excel 2007 이상 확장자 */
    public static final String EXTENSION_XLSX = ".xlsx";

    /** Excel 2007 이상 ContentType */
    public static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /** Zip 확장자 */
    public static final String EXTENSION_ZIP = ".zip";

    /** Zip ContentType */
    public static final String CONTENT_TYPE_ZIP = "application/zip";

    /** 접미어 (확장자) */
    private String suffix;

    /** 파일명 인코더 */
    private FilenameEncoder filenameEncoder;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (model.get(BUNDLE) == null || !(model.get(BUNDLE) instanceof Bundle))
            throw new IllegalArgumentException("Bundle not found");

        Bundle bundle = (Bundle) model.get(BUNDLE);
        String basename = getBasename(request, bundle);
        Resource template = getTemplate(request, bundle);
        List<Map<String, Object>> extras = bundle.getExtrasAsList();

        // 단일파일
        if (extras.size() == 1) {
            prepareAttachment(request, response, basename + getSuffix());

            transformXLS(template, extras.get(0)).write(response.getOutputStream());
        }
        // 복수파일 (zip 압축)
        else {
            File tempDir = createTempDirectory();

            for (int i = 0; i < extras.size(); i++) {
                FileOutputStream os = null;
                try {
                    String filename = basename + "_" + getSuffix();
                    os = new FileOutputStream(new File(getTempDir(), filename));

                    transformXLS(template, extras.get(i)).write(os);
                } finally {
                    IOUtils.closeQuietly(os);
                }
            }

            // 압축파일 다운로드
            prepareAttachment(request, response, basename + EXTENSION_ZIP);
            zip(tempDir, response.getOutputStream());
        }

        response.flushBuffer();
    }

    private Workbook transformXLS(Resource template, Map<String, Object> extras) throws IOException, InvalidFormatException {
        return new XLSTransformer().transformXLS(template.getInputStream(), extras);
    }

    protected String getBasename(HttpServletRequest request, Bundle bundle) throws FileNotFoundException {
        return bundle.getFilename() != null ?
                FilenameUtils.getBaseName(bundle.getFilename()) : FilenameUtils.getBaseName(getTemplate(request, bundle).getFilename());
    }

    protected Resource getTemplate(HttpServletRequest request, Bundle bundle) throws FileNotFoundException {
        // 템플릿 파일목록 생성
        List<String> tmplNames = new ArrayList<>();
        if (bundle.getTemplate() != null)
            tmplNames.add(bundle.getTemplate() + getSuffix());
        tmplNames.add(getUrl());

        // 템플릿 파일 찾기
        for (String tmplName : tmplNames) {
            Resource template = new ServletContextResource(request.getServletContext(), tmplName);
            if (template.exists())
                return template;
        }

        throw new FileNotFoundException("Excel template not found");
    }

    protected File createTempDirectory() throws IOException {
        File file = File.createTempFile(Excel2View.class.getSimpleName(), "", getTempDir());
        FileUtils.forceDelete(file);
        FileUtils.forceMkdir(file);

        return file;
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

    @Override
    public void setContentType(String contentType) {
        if (contentType == null)
            throw new IllegalArgumentException("'contentType' cannot be null");
        if (!contentType.equals(CONTENT_TYPE_XLS) || !contentType.equals(CONTENT_TYPE_XLSX) || !contentType.equals(CONTENT_TYPE_ZIP))
            throw new IllegalArgumentException("Invalid 'contentType'");

        super.setContentType(contentType);
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        String lower = suffix == null ? null : suffix.toLowerCase();
        if (lower == null)
            throw new IllegalArgumentException("'suffix' cannot be null");
        if (!lower.equals(EXTENSION_XLS) || !lower.equals(EXTENSION_XLSX))
            throw new IllegalArgumentException("'.xls' and '.xlsx' only");

        this.suffix = lower;
    }

    public FilenameEncoder getFilenameEncoder() {
        return filenameEncoder;
    }

    public void setFilenameEncoder(FilenameEncoder filenameEncoder) {
        this.filenameEncoder = filenameEncoder;
    }

    // ========================================
    // ZIP
    // ========================================

    /**
     * ZIP 압축
     *
     * @param src  압축대상
     * @param dest 출력 Stream
     */
    protected void zip(File src, OutputStream dest) {
        ZipOutputStream os = null;

        try {
            os = new ZipOutputStream(new BufferedOutputStream(dest));

            if (src.isFile()) {
                zipFile(src, os);
            } else if (src.isDirectory()) {
                zipDirectory(src, os);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * 폴더압축 (재귀안됨)
     *
     * @param src  폴더경로
     * @param dest ZipOutputStream
     * @throws IOException
     */
    protected void zipDirectory(File src, ZipOutputStream dest) throws IOException {
        if (src == null || !src.isDirectory())
            return;

        File[] files = src.listFiles();
        if (files == null || files.length == 0)
            return;

        for (File f : files) {
            zipFile(f, dest);
        }
    }

    /**
     * 단일 파일압축
     *
     * @param src  파일경로
     * @param dest ZipOutputStream
     * @throws IOException
     */
    protected void zipFile(File src, ZipOutputStream dest) throws IOException {
        if (src == null || !src.isFile())
            return;

        FileInputStream is = null;

        try {
            is = new FileInputStream(src);

            dest.putNextEntry(new ZipEntry(src.getName()));
            IOUtils.copy(new FileInputStream(src), dest);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    // ========================================
    // Bundle - 엑셀 적재정보
    // ========================================

    public static class Bundle {

        /** 분할 크기 최댓값 (xls 최대행 65k 또는 성능상 이유로 제한) */
        private static final int MAX_SPLIT_SIZE = 60000;

        /** 분할 크기 기본값 */
        private static final int DEFAULT_SPLIT_SIZE = 10000;

        /** 저장될 파일명 */
        private String filename;

        /** 템플릿 명 */
        private String template;

        /** 분할 크기 (기본값: 10000) */
        private int splitSize = DEFAULT_SPLIT_SIZE;

        /** 적재 데이터 */
        private Map<String, Object> extras;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public int getSplitSize() {
            return splitSize;
        }

        public void setSplitSize(int splitSize) {
            if (MAX_SPLIT_SIZE < splitSize)
                throw new IllegalArgumentException("The maximum value of 'splitSize' is " + MAX_SPLIT_SIZE);

            this.splitSize = splitSize;
        }

        public void putExtra(String key, Object value) {
            if (extras == null)
                extras = new HashMap<>();

            // Map 은 입력순서를 유지하기위해 LinkedHashMap 처리
            if (value instanceof Map) {
                extras.put(key, new LinkedHashMap((Map) value));
            } else {
                extras.put(key, value);
            }
        }

        public Object getExtra(String key) {
            return extras != null ? extras.get(key) : null;
        }

        public Map<String, Object> getExtras() {
            return extras == null ? null : Collections.unmodifiableMap(extras);
        }

        public List<Map<String, Object>> getExtrasAsList() {
            if (extras == null)
                return null;

            int maxSize = getMaxSize();
            int count = (int) Math.ceil((double) maxSize / (double) getSplitSize());
            List<Map<String, Object>> list = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                int startIdx = i * getSplitSize();
                int endIdx = (i + 1) * getSplitSize();

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
                    } else if (oObj instanceof Map) {
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

        /**
         * 적재된 데이터 Map, List 형식에서 최대크기 반환
         */
        private int getMaxSize() {
            if (extras == null)
                return 0;

            int maxSize = 0;
            for (Object o : extras.values()) {
                if (o instanceof List) {
                    maxSize = Math.max(maxSize, ((List) o).size());
                } else if (o instanceof Map) {
                    maxSize = Math.max(maxSize, ((Map) o).size());
                }
            }

            return maxSize;
        }

    }

}
