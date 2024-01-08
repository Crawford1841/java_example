package org.example.demo.action;


import org.example.demo.service.IQueryService;
import org.example.spring.framework.annotation.Autowired;
import org.example.spring.framework.annotation.Controller;
import org.example.spring.framework.annotation.RequestMapping;
import org.example.spring.framework.annotation.RequestParam;
import org.example.spring.framework.webmvc.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@Controller
@RequestMapping("/")
public class PageAction {

    @Autowired
    IQueryService queryService;

    @RequestMapping("/first.html")
    public ModelAndView query(@RequestParam("name") String name,@RequestParam("skill")String skill){
        String result = queryService.query(name);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("name", name);
        model.put("skill", skill);
        model.put("data", result);
        model.put("token", "123456");
        return new ModelAndView("first.html",model);
    }

}
