package kr.qusi.spring.servlet.view.excel;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
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

}
