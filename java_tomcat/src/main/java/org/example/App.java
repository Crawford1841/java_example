package org.example;

import org.example.server.Server;

/**
 * 需求：软件工程师自定义一个Tomcat提供给码农使用，码农只需要按照规定步骤，即可编写出自己的
 * 应用程序发布到HeroCat中供用户使用。
 */
public class App {
    /**
     * 码农使用tomcatCat的步骤：
     * 码农编写自己的应用程序：
     *      导入HeroCat依赖坐标，并编写启动类
     *      将自定义Servlet 放置到指定包下：例如 com.hero.webapp
     * 码农发布自己的服务：
     *      码农将自己的接口URL按照固定规则发布：
     *          按照后缀， .do 、 .action 、 无后缀
     *      不管用何种规则：都将映射到自定义的Servlet（类名映射，忽略大小写）举例
     * 用户在访问应用程序：
     *      按照URL地址访问服务
     *      如果没有指定的Servlet，则访问默认的Servlet
     *
     *      http://localhost:8080/skuServlet?name=xiong
     */


    /**
     * 工程师开发Tomcat的思路
     * 第一步：创建HeroCat工程，导入依赖坐标
     * 第二步：定义Servlet规范，HeroRequest、HeroResponse、HeroServlet
     * Servlet的规范其实是语言层面定义JavaEE
     * 第三步：实现Servlet规范HttpHeroRequest、HttpHeroResponse、DefaultHeroServlet【兜底】
     * 第四步：编写HeroCat核心代码：
     *      HeroCatServer基于Netty实现：Servlet容器
     *      HeroCatHandler处理请求，映射到Servlet的容器的自定义Servlet（Map容器）中去
     * 第五步：打包发布HeroCat
     */

    public static void main(String[] args) throws Exception {
        Server server = new Server("org.example.webapp");
        server.start();
    }
}
