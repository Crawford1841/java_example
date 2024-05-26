package org.example.video;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/5/25 22:36
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ConverVideoTools {
    private String filerealname;                            //文件名不包括后缀名
    private String filename;                                //包括后缀名
    private String videofolder = Contants.videofolder;        // 别的格式视频的目录
    private String targetfolder = Contants.targetfolder;    // flv视频的目录
    private String ffmpegpath = Contants.ffmpegpath;         // ffmpeg.exe的目录
    private String mencoderpath = Contants.mencoderpath;    // mencoder的目录
    private String imageRealPath = Contants.imageRealPath;   // 截图的存放目录


    /**
     * 转换视频格式
     * @param sourceVideoPath  目标视频后缀名 .xxx
     * @param targetExtension 转换视频后缀名 .xxx
     * @param isDelSourseFile 是否删除转换的视频
     * @param isImage          是否截图
     * @return
     */
    public boolean beginConver(String sourceVideoPath, String targetExtension, boolean isDelSourseFile,boolean isImage) {
        File fi = new File(sourceVideoPath);
        filename = fi.getName();             //获取文件名+后缀名
        filerealname = filename.substring(0, filename.lastIndexOf(".")); //获取不带后缀的文件名-后面加.toLowerCase()小写
        System.out.println("----接收到文件(" + sourceVideoPath + ")需要转换-------");
        System.out.println("----开始转文件(" + sourceVideoPath + ")-------------------------- ");
        //执行转码机制
        if (process(sourceVideoPath,targetExtension)) {
            if(isImage){
                System.out.println("视频转码结束，开始截图================= ");
                //视频转码完成，调用截图功能--zoutao
                if (processImg(sourceVideoPath)) {
                    System.out.println("截图成功！ ");
                } else {
                    System.out.println("截图失败！ ");
                }
            }


            //删除原视频+临时视频
			if (isDelSourseFile) {
				deleteFile(sourceVideoPath);
			}

            String temppath = videofolder + filerealname + ".avi";
            File file2 = new File(temppath);
            if (file2.exists()) {
                System.out.println("删除临时文件：" + temppath);
                file2.delete();
            }

            sourceVideoPath = null;
            return true;
        } else {
            sourceVideoPath = null;
            return false;
        }
    }

    private void deleteFile(String sourceVideoPath) {
        File file1 = new File(sourceVideoPath);
        if (file1.exists()){
            System.out.println("删除原文件-可用："+sourceVideoPath);
            file1.delete();
        }
    }


    /**
     * 检查文件是否存在-多处都有判断
     *
     * @param path
     * @return
     */

    private boolean checkfile(String path) {
        File file = new File(path);
        try {
            if (file.exists()) {
                System.out.println("视频文件不存在=============" + path);
                return true;
            } else {
                System.out.println("视频文件存在" + path);
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("拒绝对文件进行读访问");
        }
        return false;
    }


    /**
     * 视频截图功能
     *
     * @param sourceVideoPath 需要被截图的视频路径（包含文件名和后缀名）
     * @return
     */
    public boolean processImg(String sourceVideoPath) {

        //先确保保存截图的文件夹存在
        File TempFile = new File(imageRealPath);
        if (TempFile.exists()) {
            if (TempFile.isDirectory()) {
                System.out.println("该文件夹存在。");
            } else {
                System.out.println("同名的文件存在，不能创建文件夹。");
            }
        } else {
            System.out.println("文件夹不存在，创建该文件夹。");
            TempFile.mkdir();
        }

        File fi = new File(sourceVideoPath);
        filename = fi.getName();            //获取视频文件的名称。
        filerealname = filename.substring(0, filename.lastIndexOf("."));    //获取视频名+不加后缀名 后面加.toLowerCase()转为小写

        List<String> commend = new ArrayList<String>();
        //第一帧： 00:00:01
        //截图命令：time ffmpeg -ss 00:00:01 -i test1.flv -f image2 -y test1.jpg
        commend.add(ffmpegpath);            //指定ffmpeg工具的路径
        commend.add("-ss");
        commend.add("00:00:01");            //1是代表第1秒的时候截图
        commend.add("-i");
        commend.add(sourceVideoPath);        //截图的视频路径
        commend.add("-f");
        commend.add("image2");
        commend.add("-y");
        commend.add(imageRealPath + filerealname + ".jpg");        //生成截图xxx.jpg
        //打印截图命令
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++) {
            test.append(commend.get(i) + " ");
        }
        System.out.println("截图命令:" + test);
        //转码后完成截图功能-还是得用线程来解决--zoutao
        try {
            //调用线程处理命令
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();

            //获取进程的标准输入流
            final InputStream is1 = p.getInputStream();
            //获取进程的错误流
            final InputStream is2 = p.getErrorStream();
            //启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
            new Thread() {
                public void run() {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is1));
                    try {
                        String lineB = null;
                        while ((lineB = br.readLine()) != null) {
                            if (lineB != null) {
                                //System.out.println(lineB);    //必须取走线程信息避免堵塞
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //关闭流
                    finally {
                        try {
                            is1.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
            new Thread() {
                public void run() {
                    BufferedReader br2 = new BufferedReader(
                            new InputStreamReader(is2));
                    try {
                        String lineC = null;
                        while ((lineC = br2.readLine()) != null) {
                            if (lineC != null) {
                                //System.out.println(lineC);   //必须取走线程信息避免堵塞
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            is2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
            // 等Mencoder进程转换结束，再调用ffmepg进程非常重要！！！
            p.waitFor();
            System.out.println("截图进程结束");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 实际转换视频格式的方法
     *
     * @param targetExtension 目标视频后缀名
     * @return
     */
    private boolean process(String sourceVideoPath,String targetExtension) {
        //先判断视频的类型-返回状态码
        int type = checkContentType(sourceVideoPath);
        boolean status = false;
        //根据状态码处理
        if (type == 0) {
            System.out.println("ffmpeg可以转换,统一转为mp4文件");
            status = processVideoFormat(sourceVideoPath, targetExtension);//可以指定转换为什么格式的视频

        } else if (type == 1) {
            //如果type为1，将其他文件先转换为avi，然后在用ffmpeg转换为指定格式
            System.out.println("ffmpeg不可以转换,先调用mencoder转码avi");
            String avifilepath = processAVI(type,sourceVideoPath);

            if (avifilepath == null) {
                // 转码失败--avi文件没有得到
                System.out.println("mencoder转码失败,未生成AVI文件");
                return false;
            } else {
                System.out.println("生成AVI文件成功,ffmpeg开始转码:");
                status = processVideoFormat(avifilepath, targetExtension);
            }
        }
        return status;   //执行完成返回布尔类型true
    }

    /**
     * 检查文件类型
     *
     * @return
     */
    private int checkContentType(String sourceVideoPath) {

        //取得视频后缀-
        String type = sourceVideoPath.substring(sourceVideoPath.lastIndexOf(".") + 1, sourceVideoPath.length()).toLowerCase();
        System.out.println("源视频类型为:" + type);

        // 如果是ffmpeg能解析的格式:(asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等)
        if (type.equals("avi")) {
            return 0;
        } else if (type.equals("mpg")) {
            return 0;
        } else if (type.equals("wmv")) {
            return 0;
        } else if (type.equals("3gp")) {
            return 0;
        } else if (type.equals("mov")) {
            return 0;
        } else if (type.equals("mp4")) {
            return 0;
        } else if (type.equals("asf")) {
            return 0;
        } else if (type.equals("asx")) {
            return 0;
        } else if (type.equals("flv")) {
            return 0;
        } else if (type.equals("mkv")) {
            return 0;
        }


        // 如果是ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
        // 就先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
        else if (type.equals("wmv9")) {
            return 1;
        } else if (type.equals("rm")) {
            return 1;
        } else if (type.equals("rmvb")) {
            return 1;
        }
        System.out.println("上传视频格式异常");
        return 9;
    }


    /**
     * 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
     * 可以先用（mencoder）转换为avi(ffmpeg能解析的)格式.再用ffmpeg解析为指定格式
     *
     * @param type
     * @return
     */
    private String processAVI(int type,String sourceVideoPath) {

        System.out.println("调用了mencoder.exe工具");
        List<String> commend = new ArrayList<String>();
        commend.add(mencoderpath);                //指定mencoder.exe工具的位置
        commend.add(sourceVideoPath);             //指定源视频的位置
        commend.add("-oac");
        commend.add("mp3lame");            //lavc 原mp3lame
        commend.add("-lameopts");
        commend.add("preset=64");
        commend.add("-ovc");
        commend.add("xvid");        //mpg4(xvid),AVC(h.264/x264),只有h264才是公认的MP4标准编码，如果ck播放不了，就来调整这里
        commend.add("-xvidencopts");  //xvidencopts或x264encopts
        commend.add("bitrate=600");        //600或440
        commend.add("-of");
        commend.add("avi");
        commend.add("-o");
        commend.add(videofolder + filerealname + ".avi");   //存放路径+名称，生成.avi视频
        //打印出转换命令-zoutao
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++) {
            test.append(commend.get(i) + " ");
        }
        System.out.println("mencoder输入的命令:" + test);
        // cmd命令：mencoder 1.rmvb -oac mp3lame -lameopts preset=64 -ovc xvid
        // -xvidencopts bitrate=600 -of avi -o rmvb.avi

        try {
            //调用线程命令启动转码
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();   //多线程处理加快速度-解决数据丢失
            //doWaitFor(p);

            //获取进程的标准输入流
            final InputStream is1 = p.getInputStream();
            //获取进程的错误流
            final InputStream is2 = p.getErrorStream();
            //启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
            new Thread() {
                public void run() {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                    try {
                        String lineB = null;
                        while ((lineB = br.readLine()) != null) {
                            if (lineB != null) {
                                System.out.println(lineB);    //打印mencoder转换过程代码-可注释
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            is1.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
            new Thread() {
                public void run() {
                    BufferedReader br2 = new BufferedReader(
                            new InputStreamReader(is2));
                    try {
                        String lineC = null;
                        while ((lineC = br2.readLine()) != null) {
                            if (lineC != null) {
                                System.out.println(lineC);    //打印mencoder转换过程代码
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            is2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
            // 等Mencoder进程转换结束，再调用ffmepg进程非常重要！！！
            p.waitFor();
            System.out.println("Mencoder进程结束");
            return videofolder + filerealname + ".avi";        //返回转为AVI以后的视频地址

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 转换为指定格式--zoutao
     * ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
     *
     * @param oldfilepath
     * @param targetExtension 目标格式后缀名 .xxx
     * @return
     */
    private boolean processVideoFormat(String oldfilepath, String targetExtension) {
        System.out.println("调用了ffmpeg.exe工具");
        //先确保保存转码后的视频的文件夹存在
        File TempFile = new File(targetfolder);
        if (TempFile.exists()) {
            if (TempFile.isDirectory()) {
                System.out.println("该文件夹存在。");
            } else {
                System.out.println("同名的文件存在，不能创建文件夹。");
            }
        } else {
            System.out.println("文件夹不存在，创建该文件夹。");
            TempFile.mkdir();
        }
        List<String> commend = new ArrayList<String>();
        commend.add(ffmpegpath);         //ffmpeg.exe工具地址
        commend.add("-i");
        commend.add(oldfilepath);            //源视频路径
        commend.add("-vcodec");
        commend.add("h263");  //
        commend.add("-ab");        //新增4条
        commend.add("128");      //高品质:128 低品质:64
        commend.add("-acodec");
        commend.add("mp3");      //音频编码器：原libmp3lame
        commend.add("-ac");
        commend.add("2");       //原1
        commend.add("-ar");
        commend.add("22050");   //音频采样率22.05kHz
        commend.add("-r");
        commend.add("29.97");  //高品质:29.97 低品质:15
        commend.add("-c:v");
        commend.add("libx264");    //视频编码器：视频是h.264编码格式
        commend.add("-strict");
        commend.add("-2");
        commend.add(targetfolder + filerealname + targetExtension);  // //转码后的路径+名称，是指定后缀的视频

        //打印命令--
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++) {
            test.append(commend.get(i) + " ");
        }
        System.out.println("ffmpeg输入的命令:" + test);

        try {
            //多线程处理加快速度-解决rmvb数据丢失builder名称要相同
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();   //多线程处理加快速度-解决数据丢失

            final InputStream is1 = p.getInputStream();
            final InputStream is2 = p.getErrorStream();
            new Thread() {
                public void run() {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is1));
                    try {
                        String lineB = null;
                        while ((lineB = br.readLine()) != null) {
                            if (lineB != null)
                                System.out.println(lineB);    //打印mencoder转换过程代码
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            new Thread() {
                public void run() {
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
                    try {
                        String lineC = null;
                        while ((lineC = br2.readLine()) != null) {
                            if (lineC != null)
                                System.out.println(lineC);    //打印mencoder转换过程代码
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            p.waitFor();        //进程等待机制，必须要有，否则不生成mp4！！！
            System.out.println("生成mp4视频为:" + videofolder + filerealname + ".mp4");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void runChangeVedios(String originSubifx, String targetSubfix) {
        ConverVideoTools converVideoTools = new ConverVideoTools();
//        converVideoTools.beginConver("D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\09_固定资产（1）.mkv",".mp4",false,false);
        File file = new File(videofolder);
        File[] files = file.listFiles();
        List<File> collect = Arrays.stream(files).collect(Collectors.toList());
        List<File> vedios = collect.stream().filter(item -> {
            String suffix = item.getName().substring(item.getName().lastIndexOf("."), item.getName().length());
            return originSubifx.equals(suffix);
        }).collect(Collectors.toList());

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for(int i=0;i<vedios.size();i++){
            File item = vedios.get(i);
            //检测本地是否存在
            String changeVedio = item.getAbsolutePath().substring(0, item.getAbsolutePath().lastIndexOf("."))+targetSubfix;

            if (checkfile(changeVedio)) {
                System.out.println(item.getAbsolutePath() + "========该文件存在=========");
                continue;
            }
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                converVideoTools.beginConver(item.getAbsolutePath(), targetSubfix, false, false);
            }, executorService);
            futures.add(voidCompletableFuture);
        }
        CompletableFuture<Void> all= CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            all.get();
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("=========视频转换完毕=========");

    }

    public static void main(String[] args) {
        ConverVideoTools converVideoTools = new ConverVideoTools();
        converVideoTools.runChangeVedios(".mkv", ".mp4");
    }

}

class Contants {
    /**
     * @Description:(3.工具类主类)设置转码工具的各个路径
     * @param:@param args
     * @return:void
     */

    public static final String ffmpegpath = "D:\\application\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe";        // ffmpeg工具安装位置
    public static final String mencoderpath = "D:\\application\\mplayer-svn-38151-x86_64";    // mencoder工具安装的位置

    public static final String videofolder = "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\";    // 需要被转换格式的视频目录

    public static final String targetfolder = "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\"; // 转码后视频保存的目录
    public static final String imageRealPath = "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\"; // 截图的存放目录


}