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
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class ConverTxtTools {
    /**
     * mp3转文字
     *
     * @param modelPath 模型路径
     * @param mp3Path   mp3路径
     */
    public static void Mp3toText(String modelPath, String mp3Path) throws Exception {
        LibVosk.setLogLevel(LogLevel.DEBUG);

        try (Model model = new Model(modelPath);
             InputStream ais = AudioSystem.getAudioInputStream(FileUtil.getInputStream(mp3Path));
             Recognizer recognizer = new Recognizer(model, 16000)) {

            int bytes;
            byte[] b = new byte[4096];
            while ((bytes = ais.read(b)) >= 0) {
                recognizer.acceptWaveForm(b, bytes);
            }

            System.out.println(recognizer.getFinalResult() + System.lineSeparator());
        }
    }
    /**
     * 文字转mp3
     *
     * @param text 文字内容
     */
    public static void TextToMp3(String text,String savePath){
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

    public static void main(String[] args)throws Exception {
//        Mp3toText("D:\\application\\model\\vosk-model-cn-0.22","D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\09.mp3");
        Mp3toText("D:\\application\\model\\vosk-model-small-cn-0.22","D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\09.mp3");
        TextToMp3("大风起兮，云飞扬，安得猛士兮，守四方！","D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\test.mp3");
        System.out.println("执行完毕");
    }

}
