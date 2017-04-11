package kr.qusi.spring.servlet.view.excel;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Excel2View extends AbstractUrlBasedView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

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
