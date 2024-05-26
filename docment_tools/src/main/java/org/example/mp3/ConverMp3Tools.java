package org.example.mp3;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/5/26 12:03
 */


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import org.bytedeco.javacpp.Loader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ConverMp3Tools {
    /**
     * 获取视频的文件pcm文件地址
     *
     * @param url MP4
     * @return
     * @throws Exception
     */
    public static String getMp4Pcm(String url, String tmpDir) throws Exception {
        Optional<String> pcmPath = Optional.empty();
        try {
            pcmPath = convertMP4toPCM(Paths.get(url), Paths.get(tmpDir));
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("转换pcm异常:" + exception.getMessage());
        }
        if (pcmPath.isPresent()) {
            return pcmPath.get();
        } else {
            throw new Exception("视频转换音频失败");
        }
    }
    /**
     * 将单个PM4文件进行片头和片尾歌曲删除后，转换为PCM文件
     *
     * @param mp4Path
     * @param pcmDir
     * @return 转换完成后的pcm文件路径
     */
    public static Optional<String> convertMP4toPCM(Path mp4Path, Path pcmDir) {
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        // 基于ffmpeg进行pcm转换
        // 基于输入路径的md5值来命名，也可以基于系统时间戳来命名
        String pcmFile = pcmDir.resolve(UUID.randomUUID() + ".pcm").toString();
        ProcessBuilder pcmBuilder =
                new ProcessBuilder(
                        ffmpeg,
                        "-y",
                        "-i",
                        mp4Path.toAbsolutePath().toString(),
                        "-vn",
                        "-acodec",
                        "pcm_s16le",
                        "-f",
                        "s16le",
                        "-ac",
                        "1",
                        "-ar",
                        "16000",
                        pcmFile);
        try {
            // inheritIO是指将 子流程的IO与当前java流程的IO设置为相同
            pcmBuilder.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            System.out.println("ffmpeg将mp4转换为pcm时出错");
            return Optional.empty();
        }
        // 返回pcm文件路径
        return Optional.of(pcmFile);
    }

    /**
     * 根据PCM文件构建wav的header字段
     *
     * @param srate Sample rate - 8000, 16000, etc.
     * @param channel Number of channels - Mono = 1, Stereo = 2, etc..
     * @param format Number of bits per sample (16 here)
     * @throws IOException
     */
    public static byte[] buildWavHeader(int dataLength, int srate, int channel, int format)
            throws IOException {
        byte[] header = new byte[44];

        long totalDataLen = dataLength + 36;
        long bitrate = srate * channel * format;

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = (byte) format;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channel;
        header[23] = 0;
        header[24] = (byte) (srate & 0xff);
        header[25] = (byte) ((srate >> 8) & 0xff);
        header[26] = (byte) ((srate >> 16) & 0xff);
        header[27] = (byte) ((srate >> 24) & 0xff);
        header[28] = (byte) ((bitrate / 8) & 0xff);
        header[29] = (byte) (((bitrate / 8) >> 8) & 0xff);
        header[30] = (byte) (((bitrate / 8) >> 16) & 0xff);
        header[31] = (byte) (((bitrate / 8) >> 24) & 0xff);
        header[32] = (byte) ((channel * format) / 8);
        header[33] = 0;
        header[34] = 16;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (dataLength & 0xff);
        header[41] = (byte) ((dataLength >> 8) & 0xff);
        header[42] = (byte) ((dataLength >> 16) & 0xff);
        header[43] = (byte) ((dataLength >> 24) & 0xff);

        return header;
    }

    /**
     * 默认写入的pcm数据是16000采样率，16bit，可以按照需要修改
     *
     * @param filePath
     * @param pcmPath
     */
    public static String writeToFile(String filePath, String pcmPath) {
        BufferedOutputStream bos = null;
        byte[] pcmData = FileUtil.readBytes(pcmPath);
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] header = buildWavHeader(pcmData.length, 16000, 1, 16);
            bos.write(header, 0, 44);
            bos.write(pcmData);
            bos.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return filePath;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                    FileUtil.del(pcmPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 提取音频
     * @param mp4Path MP4地址
     * @param tmpDir 临时文件夹
     * @param resultPath 最终结果音频地址
     * @return 音频地址
     * @throws Exception 异常
     */
    public static String extractAudio(String mp4Path, String tmpDir, String resultPath)
            throws Exception {
        String pcmPath = getMp4Pcm(mp4Path, tmpDir);
        return writeToFile(resultPath, pcmPath);
    }

    /**
     * 批量提取音频
     * @param mp4Path MP4地址
     * @param tmpDir 临时文件夹
     * @throws Exception
     */
    public static void runAudios(String mp4Path,String tmpDir){
        List<File> files = FileUtil.loopFiles(mp4Path);
        List<File> collect = files.stream().filter(item -> {
            String suffix = item.getName().substring(item.getName().lastIndexOf("."), item.getName().length());
            return ".mp4".equals(suffix);
        }).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<CompletableFuture> futures = new ArrayList<>();
        collect.forEach(item->{
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                String filePath = item.getName().substring(0, item.getName().indexOf(".")) + ".mp3";
                try {
                    if(checkfile(filePath)){
                        extractAudio(item.getAbsolutePath(), tmpDir, mp4Path + "\\" + filePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService);
            futures.add(future);
        });
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get();
            executorService.shutdown();
            System.out.println("转换完成！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    /**
     * 检查文件是否存在-多处都有判断
     *
     * @param path
     * @return
     */

    private static boolean checkfile(String path) {
        File file = new File(path);
        try {
            if (file.exists()) {
                System.out.println("=========音频文件存在=============" + path);
                return true;
            } else {
                System.out.println("============音频文件不存在" + path);
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("拒绝对文件进行读访问");
        }
        return false;
    }
    public static void main(String[] args) throws Exception {
//        extractAudio(
//                "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\09_固定资产（1）.mp4",
//                "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）",
//                "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\测试.mp3");
        runAudios("D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）","D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）");
    }

}
