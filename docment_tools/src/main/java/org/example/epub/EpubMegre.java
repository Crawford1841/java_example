package org.example.epub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class EpubMegre {

    public static void main(String[] args) throws IOException {
        Path epub1 = Paths.get("E:\\新建文件夹\\156程序员的个人财富课\\00开篇词｜为什么说程序员最适合学财富管理？.epub");
        Path epub2 = Paths.get("E:\\新建文件夹\\156程序员的个人财富课\\01-财富框架：建立属于你自己的财富双塔.epub");
        Path mergedEpub = Paths.get("E:\\新建文件夹\\pdf\\程序员的个人财富课.epub");

        mergeEpubs(epub1, epub2, mergedEpub);
    }

    public static void mergeEpubs(Path epub1, Path epub2, Path output) throws IOException {
        Set<String> existingFiles = new HashSet<>();

        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(Files.newOutputStream(output))) {
            // Handle mimetype file first
            addMimetype(epub1, zos, existingFiles);
            addMimetype(epub2, zos, existingFiles);

            // Extract and copy contents of the first EPUB
            copyEpubContents(epub1, zos, existingFiles);

            // Extract and copy contents of the second EPUB
            copyEpubContents(epub2, zos, existingFiles);
        }
    }

    private static void addMimetype(Path epub, ZipArchiveOutputStream zos, Set<String> existingFiles) throws IOException {
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(Files.newInputStream(epub))) {
            ZipArchiveEntry entry = zis.getNextZipEntry();
            if (entry != null && "mimetype".equals(entry.getName()) && !existingFiles.contains(entry.getName())) {
                byte[] mimetypeData = readAllBytes(zis);

                ZipArchiveEntry mimeTypeEntry = new ZipArchiveEntry(entry.getName());
                mimeTypeEntry.setMethod(ZipArchiveOutputStream.STORED);
                mimeTypeEntry.setSize(mimetypeData.length);
                mimeTypeEntry.setCompressedSize(mimetypeData.length);
                mimeTypeEntry.setCrc(computeCRC32(mimetypeData)); // 必须设置CRC

                zos.putArchiveEntry(mimeTypeEntry);
                zos.write(mimetypeData);
                zos.closeArchiveEntry();

                existingFiles.add(entry.getName());
            }
        }
    }

    private static void copyEpubContents(Path epub, ZipArchiveOutputStream zos, Set<String> existingFiles) throws IOException {
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(Files.newInputStream(epub))) {
            ZipArchiveEntry entry;
            while ((entry = zis.getNextZipEntry()) != null) {
                if (!existingFiles.contains(entry.getName())) {
                    zos.putArchiveEntry(new ZipArchiveEntry(entry.getName()));
                    try (InputStream in = zis) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    }
                    zos.closeArchiveEntry();
                    existingFiles.add(entry.getName());
                }
            }
        }
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private static long computeCRC32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }
}
