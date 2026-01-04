package com.bobobo.plugins.clb0.util.log;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class LogCompressor {
    public boolean compressFile(File sourceFile, File targetFile) {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile);
             GZIPOutputStream gzos = new GZIPOutputStream(fos);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                gzos.write(buffer, 0, bytesRead);
            }

            gzos.finish();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public File getCompressedFileName(File originalFile) {
        return new File(originalFile.getParent(), originalFile.getName() + ".gz");
    }
}
