package org.example.demo.mvc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.example.demo.service.IDemoService;
import org.example.mvcframework.annotation.Autowired;
import org.example.mvcframework.annotation.Controller;
import org.example.mvcframework.annotation.RequestMapping;
import org.example.mvcframework.annotation.RequestParam;

@Controller
@RequestMapping("/demo")
public class DemoAction {
    @Autowired
    private IDemoService iDemoService;

    @RequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,@RequestParam("name")String name,@RequestParam("id")String id){
        String result = "My name is "+name + ",id = "+id;
        try {
            response.getWriter().write(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/add")
    public void add(HttpServletRequest request, HttpServletResponse response,@RequestParam("a")Integer a,@RequestParam("b")Integer b){
        try {
            response.getWriter().write(a+"+"+b+"="+(a+b));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/remove")
    public String  remove(@RequestParam("id") Integer id){
        return "" + id;
    }
}
