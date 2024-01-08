package org.example.spring.framework.webmvc.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.example.spring.framework.annotation.Controller;
import org.example.spring.framework.annotation.RequestMapping;
import org.example.spring.framework.context.ApplicationContext;

import javax.servlet.http.HttpServlet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/6 23:37
 */
public class DispatcherServlet extends HttpServlet {

    //保存Controller中URL和Method的对应关系
    private List<HandlerMapping> handlerMapping = new CopyOnWriteArrayList<>();

    //IOC容器的访问上下文
    private ApplicationContext applicationContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.applicationContext = new ApplicationContext(config.getInitParameter("contextConfigLocation"));
        //==========MVC功能=======
        //5、初始化HandlerMapping
        doInitHandlerMapping();
        System.out.println("SpringMVC framework is init.");
    }

    private void doInitHandlerMapping() {
        if(this.applicationContext.getBeanDefinitionCount()==0){
            return;
        }
        for(String beanName:this.applicationContext.getBeanDefinitionNames()){
            Object instance = applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();
            if(!clazz.isAnnotationPresent(Controller.class)){
                continue;
            }
            String baseUrl = "";
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();

            }

            //只迭代public方法
            for(Method method:clazz.getMethods()){
                if(!method.isAnnotationPresent(RequestMapping.class)){
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");;
                Pattern pattern = Pattern.compile(regex);
                this.handlerMapping.add(new HandlerMapping(pattern,method));
                System.out.println("Mapped " + pattern + "," + method);

            }


        }


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //6、根据URL委派给具体的调用方法
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp)throws Exception {
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

        String beanName = toLowerFirstCase(handlerMapping.getMethod().getDeclaringClass().getSimpleName());
        /**
         *TODO
         *  会存在循环依赖的问题
         *  这种情况是怎么造成的呢? 我们分析原因之后发现，因为，loC 容器对 Bean 的初始化是根据 BeanDefinition 循环迭代，有一定的顺序。
         *  这样，在执行依赖注入时，需要自动赋值的属性对应的对象有可能还没初始化，没有初始化也就没有对应的实例可以注入于是，就出行我们看到的情况
         *  演示错误：http://localhost:8080/spring_v2/web/appear
         */
        handlerMapping.getMethod().invoke(applicationContext.getBean(beanName), paramValues);
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

    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;     //利用了ASCII码，大写字母和小写相差32这个规律
        return String.valueOf(chars);
    }
}
