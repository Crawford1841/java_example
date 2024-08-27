package org.example.task;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/8/17 7:22
 */

import org.example.basic.Logger;

import java.io.BufferedReader;
import java.io.FileReader;

public class Task implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader("D:\\workspace\\java_example\\java_log\\src\\main\\resources\\日志错误.txt"));
            StringBuilder longLog = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                longLog.append(line);
            }
            while (true) {
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
                Logger.info("{}{}{}{}{}{}{}{}",longLog,longLog,longLog,longLog,longLog,longLog,longLog,longLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
