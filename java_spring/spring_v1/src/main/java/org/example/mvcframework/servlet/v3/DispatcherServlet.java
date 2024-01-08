package org.example.mvcframework.servlet.v3;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/6 9:51
 */

import org.example.mvcframework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DispatcherServlet extends HttpServlet {
    //保存用户配置好的配置文件
    private Properties contextConfig = new Properties();
    //保存扫描的所有的类名
    private List<String> classNames = new CopyOnWriteArrayList<>();
    //传说中的IOC容器
    private Map<String, Object> ioc = new ConcurrentHashMap<>();
    //报错url和Method的对应关系
//    private Map<String, Method> handlerMapping = new ConcurrentHashMap<>();
    /**
     * 思考：为什么不用Map
     * 你用Map的话，key，只能是url
     * Handler 本身的功能就是把url和method对应关系，已经具备了Map的功能
     * 根据设计原则：冗余的感觉了，单一职责，最少知道原则，帮助我们更好的理解
     */
    private List<HandlerMapping> handlerMapping = new CopyOnWriteArrayList<>();

    /**
     * 初始化所有的相关的类，IOC容器、servletBean
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        /**
         * 模板方法模式
         */
        //1、加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2、扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
        //3、初始化扫描到的类，并且放入到IOC容器之中
        doInstance();
        //4、完成自动化的依赖注入
        doAutowired();
        //5、初始化HandlerMapping，策略模式实际应用
        doInitHandlerMapping();
        System.out.print("SpringMVC Framework is init");
    }

    private void doInitHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            //保存写在类上面的ReqeustMapping路径
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //默认获取所有的public方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");;
                Pattern pattern = Pattern.compile(regex);
                this.handlerMapping.add(new HandlerMapping(pattern,entry.getValue(),method));

                System.out.println("Mapped " + pattern + "," + method);
            }
        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            //忽略字段的修饰符，不管你是 private / protected / public / default
            for (Field field : entry.getValue().getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                //代码在反射面前，那就是裸奔
                //强制访问
                field.setAccessible(true);
                try {
                    //相当于 demoAction.demoService = ioc.get("org.example.demo.service.IDemoService");
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }

            }

        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }

        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                //什么样的类才需要初始化？加了注解类的才需要初始化，简化代码逻辑，只举例Controller、Service
                if (clazz.isAnnotationPresent(Controller.class)) {
                    Object instance = clazz.newInstance();
                    String beanName = toLowerFristCase(clazz.getSimpleName());
                    //class类名小写
                    ioc.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    //1、默认根据beanName类名首字母小写
                    String beanName = toLowerFristCase(clazz.getSimpleName());
                    //2、使用自定义BeanName
                    Service service = clazz.getAnnotation(Service.class);
                    if (!"".equals(service.value())) {
                        beanName = service.value();
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);
                    //3、根据报名.类名作为bean
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("The BeanName is exists！");
                        }
                        //把接口的类型直接当成key
                        ioc.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 如果类明本身就是小写，确实会出问题，这里需要注意
     *
     * @param simpleName
     * @return
     */
    private String toLowerFristCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        /**
         * 之所以+32，是因为大小写字母ASCII码相差32
         * 而且大写字母的ASCII码要小于小写字母的ASCII码，在Java中，对char做运算，实际上就是对ASCII码做算学运算
         */
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doLoadConfig(String contextConfigLocation) {
        //直接从类路径下找到Spring主配置文件所在的路径,并且将其读取出来放到Properties对象中
        //相对于sacnPackage = org.example.demo从文件中保存到了内存中
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 思考一下，Spring源码中是如何处理扫描类的，这里是不是可以延申到Spring的三级缓存处理，拓展：SpringBoot又是如何通过注解进行装配的
     *
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        //scanPackage = org.example.demo 存储的是包路径
        //classpath 下不仅有.class文件 .xml文件 .properties文件
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String clazzName = (scanPackage + "." + file.getName().replace(".class", ""));
                classNames.add(clazzName);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            /**
             * 委派模式
             */
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exeption,Detail：" + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * 动态委派并反射实现调用
     *
     * @param req
     * @param resp
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InvocationTargetException, IllegalAccessException {
        HandlerMapping handlerMapping = getHandler(req);
        if(handlerMapping == null){
            resp.getWriter().write("404 Not Found！！！");
            return;
        }
        //获得方法的形参列表
        Class<?>[] paramTypes = handlerMapping.getParamTypes();
        Object[] paramValues = new Object[paramTypes.length];
        Map<String, String[]> params = req.getParameterMap();

        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if (!handlerMapping.getParamIndexMapping().containsKey(parm.getKey())) {
                continue;
            }

            int index = handlerMapping.getParamIndexMapping().get(parm.getKey());
            paramValues[index] = convert(paramTypes[index], value);
        }
        if (handlerMapping.getParamIndexMapping().containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = handlerMapping.getParamIndexMapping().get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (handlerMapping.getParamIndexMapping().containsKey(HttpServletResponse.class.getName())) {
            int respIndex = handlerMapping.getParamIndexMapping().get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }


        Object returnValue = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (returnValue == null || returnValue instanceof Void) {
            return;
        }
        resp.getWriter().write(returnValue.toString());

    }

    //url传过来的参数都是String类型的，HTTP是基于字符串协议
    //只需要把String转换为任意类型就好
    private Object convert(Class<?> type, String value) {
        //如果是int
        if (Integer.class == type) {
            return Integer.valueOf(value);
        } else if (Double.class == type) {
            return Double.valueOf(value);
        }
        //如果还有double或者其他类型，继续加if
        //这时候，我们应该想到策略模式了
        //在这里暂时不实现，希望小伙伴自己来实现
        return value;
    }


    private HandlerMapping getHandler(HttpServletRequest req) {
        if (handlerMapping.isEmpty()) {
            return null;
        }
        //绝对路径
        String url = req.getRequestURI();
        //处理成相对路径
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        /**
         * 大型系统会存在很庞大的接口，这样循环去取是否效率上会存在问题
         */
        for (HandlerMapping handlerMapping : this.handlerMapping) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

}
