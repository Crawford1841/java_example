package org.example.volatil;

/**
 * 演示volatile的缺陷
 */
public class VolatileBug {
    public static void main(String[] args) {
        VolatileCase volatileCase = new VolatileCase();
        for(int i=0;i<2;i++){
            Thread t = new Thread(volatileCase);
            t.start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count="+volatileCase.count);
    }

    static class VolatileCase implements Runnable{
        private volatile int count;
        @Override
        public void run() {
            addCount();
        }
        private void addCount(){
            for(int i=0;i<1000;i++){
                count++;//但是实际情况是三条汇编指令
            }
        }
    }

}


