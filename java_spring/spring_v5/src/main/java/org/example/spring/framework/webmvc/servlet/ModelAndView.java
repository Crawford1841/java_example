package org.example.spring.framework.webmvc.servlet;

import java.util.Map;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/7 22:47
 */
public class ModelAndView {
    private String viewName;
    private Map<String,?> model;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
