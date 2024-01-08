package org.example.spring.framework.webmvc.servlet;

import org.example.spring.framework.annotation.Controller;
import org.example.spring.framework.annotation.RequestMapping;
import org.example.spring.framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/6 23:37
 * 解决循环依赖的问题
 */
public class DispatcherServlet extends HttpServlet {

    //保存Controller中URL和Method的对应关系
    private List<HandlerMapping> handlerMappings = new CopyOnWriteArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new ConcurrentHashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    //IOC容器的访问上下文
    private ApplicationContext applicationContext = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.applicationContext = new ApplicationContext(config.getInitParameter("contextConfigLocation"));
        //==========MVC功能=======
        //5、初始化HandlerMapping
        initStrategies(applicationContext);
        System.out.println("SpringMVC framework is init.");
    }

    private void initStrategies(ApplicationContext context) {
        //handlerMapping
        doInitHandlerMapping(context);
        //初始化参数适配器
        doInitHandlerAdapters(context);
        //初始化视图转换器
        doInitViewResolvers(context);
    }

    private void doInitHandlerMapping(ApplicationContext context) {
        if (this.applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }
        for (String beanName : this.applicationContext.getBeanDefinitionNames()) {
            Object instance = applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();

            }

            //只迭代public方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("\\*", ".*")
                        .replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(pattern, instance, method));
                System.out.println("Mapped " + regex + "-->" + method);
            }
        }
    }

    private void doInitHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new HandlerAdapter());
        }
    }

    private void doInitViewResolvers(ApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new ViewResolver(templateRoot));
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
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
//            resp.getWriter().write("500 Exception,Detail: " + Arrays.toString(e.getStackTrace()));
            Map<String,Object> model = new HashMap<String, Object>();
            model.put("detail","500 Exception,Detail: ");
            model.put("stackTrace",Arrays.toString(e.getStackTrace()));
            try {
                processDispatchResult(req,resp,new ModelAndView("500",model));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HandlerMapping handlerMapping = getHandler(req);
        if (handlerMapping == null) {
            processDispatchResult(req,resp,new ModelAndView("404"));
            return;
        }
        //2、根据HandlerMapping拿到HandlerAdapter
        HandlerAdapter ha = getHandlerAdapter(handlerMapping);

        //3、根据HandlerAdapter拿到对应的ModelAndView
        ModelAndView mv = ha.handler(req,resp,handlerMapping);

        //4、根据ViewResolver找到对应View对象
        //通过View对象渲染页面，并返回
        processDispatchResult(req,resp,mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView mv) throws Exception {
        if(null == mv){return;}
        if(this.viewResolvers.isEmpty()){return;}

        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(mv.getViewName());
            view.render(mv.getModel(),req,resp);
            return;
        }
    }
    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){ return null;}
        HandlerAdapter ha = this.handlerAdapters.get(handler);
        return ha;
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        //绝对路径
        String url = req.getRequestURI();
        //处理成相对路径
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        /**
         * 大型系统会存在很庞大的接口，这样循环去取是否效率上会存在问题
         */
        for (HandlerMapping handlerMapping : this.handlerMappings) {
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
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;     //利用了ASCII码，大写字母和小写相差32这个规律
        return String.valueOf(chars);
    }
}
