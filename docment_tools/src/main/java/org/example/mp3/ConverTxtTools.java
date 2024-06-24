package org.example.mp3;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/5/26 12:54
 * 语音转文字
 * 文字转语音
 */

import cn.hutool.core.io.FileUtil;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
public class ConverTxtTools {
    /**
     * mp3转文字
     *
     * @param modelPath 模型路径
     * @param mp3Path   mp3路径
     */
    public static void Mp3toText(String modelPath, String mp3Path, String savePath) throws Exception {
        LibVosk.setLogLevel(LogLevel.DEBUG);
        String txtName = mp3Path.substring(mp3Path.lastIndexOf("\\") + 1, mp3Path.lastIndexOf("."));
        Model model = new Model(modelPath);
        InputStream ais = AudioSystem.getAudioInputStream(FileUtil.getInputStream(mp3Path));
        Recognizer recognizer = new Recognizer(model, 16000);

        int bytes;
        byte[] b = new byte[4096];
        while ((bytes = ais.read(b)) >= 0) {
            recognizer.acceptWaveForm(b, bytes);
        }
        String result = recognizer.getFinalResult() + System.lineSeparator();
        FileUtil.writeBytes(result.getBytes(), savePath + "\\" + txtName+".txt");
        System.out.println(result);
    }

    /**
     * 文字转mp3
     *
     * @param text 文字内容
     */
    public static void TextToMp3(String text, String savePath) {
        ActiveXComponent ax;
        try {
            ax = new ActiveXComponent("Sapi.SpVoice");
            // 运行时输出语音内容
            Dispatch spVoice = ax.getObject();
            // 音量 0-100
            ax.setProperty("Volume", new Variant(100));
            // 语音朗读速度 -10 到 +10
            ax.setProperty("Rate", new Variant(-2));
            // 执行朗读
            Dispatch.call(spVoice, "Speak", new Variant(text));

            // 下面是构建文件流把生成语音文件
            ax = new ActiveXComponent("Sapi.SpFileStream");
            Dispatch spFileStream = ax.getObject();

            ax = new ActiveXComponent("Sapi.SpAudioFormat");
            Dispatch spAudioFormat = ax.getObject();

            // 设置音频流格式
            Dispatch.put(spAudioFormat, "Type", new Variant(22));
            // 设置文件输出流格式
            Dispatch.putRef(spFileStream, "Format", spAudioFormat);
            // 调用输出 文件流打开方法，创建一个.wav文件
            Dispatch.call(spFileStream, "Open", new Variant(savePath), new Variant(3), new Variant(true));
            // 设置声音对象的音频输出流为输出文件对象
            Dispatch.putRef(spVoice, "AudioOutputStream", spFileStream);
            // 设置音量 0到100
            Dispatch.put(spVoice, "Volume", new Variant(100));
            // 设置朗读速度
            Dispatch.put(spVoice, "Rate", new Variant(-2));
            // 开始朗读
            Dispatch.call(spVoice, "Speak", new Variant(text));

            // 关闭输出文件
            Dispatch.call(spFileStream, "Close");
            Dispatch.putRef(spVoice, "AudioOutputStream", null);

            spAudioFormat.safeRelease();
            spFileStream.safeRelease();
            spVoice.safeRelease();
            ax.safeRelease();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runMp3ChangeText(String modelPath, String mp3Path, String savePath) throws Exception {
        List<File> files = FileUtil.loopFiles(mp3Path);
        List<File> collect = files.stream().filter(item -> {
            String suffix = item.getName().substring(item.getName().lastIndexOf("."), item.getName().length());
            return ".mp3".equals(suffix);
        }).collect(Collectors.toList());
        collect.forEach(item -> {
            try {
                String file =savePath +"\\"+ item.getName().substring(0,item.getName().lastIndexOf("."))+".txt";
                if(!checkfile(file)){
                    Mp3toText(modelPath, item.getAbsolutePath(), savePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
                System.out.println("========文本文件存在=============" + path);
                return true;
            } else {
                System.out.println("========文本文件不存在=========" + path);
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("拒绝对文件进行读访问");
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        /**
         * 复杂的模型精准度高，但是速度会稍微慢一点
         */
        String simpleModel = "D:\\application\\model\\vosk-model-small-cn-0.22";
        String model = "D:\\application\\model\\vosk-model-cn-0.22";
        String resouce = "D:\\BaiduNetdiskDownload";
        runMp3ChangeText(model, resouce, resouce);
//        TextToMp3("大风起兮，云飞扬，安得猛士兮，守四方！", "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\test.mp3");
        System.out.println("执行完毕");
    }

}
