package org.example.invest;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/9/25 21:13
 */

public class Math {
    public static void intrest(double fee,int month,double rate){
        double interst = 0;
        for(int i=0;i<month;i++){
            interst +=fee * rate;
            fee-=375;
        }
        System.out.println("优惠了："+interst+"元");
    }

    public static void main(String[] args) {
        intrest(8199,24,0.002);
    }
}
