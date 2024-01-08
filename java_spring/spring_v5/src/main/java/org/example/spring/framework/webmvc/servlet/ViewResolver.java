package org.example.spring.framework.webmvc.servlet;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/7 23:22
 */

import java.io.File;

public class ViewResolver {
    //.vm   .ftl  .jsp  .gp  .tom
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    public ViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public View resolveViewName(String viewName) {
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName)
                .replaceAll("/+","/"));

        return new View(templateFile);
    }
}
