package org.example.demo.action;


import org.example.demo.service.IModifyService;
import org.example.demo.service.IQueryService;
import org.example.spring.framework.annotation.Autowired;
import org.example.spring.framework.annotation.Controller;
import org.example.spring.framework.annotation.RequestMapping;
import org.example.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口url
 * @author Tom
 *
 */
@Controller
@RequestMapping("/web")
public class MyAction {

	@Autowired
	IQueryService queryService;
	@Autowired
	IModifyService modifyService;

	@RequestMapping("/query.json")
	public void query(HttpServletRequest request, HttpServletResponse response,
								@RequestParam("name") String name){
		String result = queryService.query(name);
		out(response,result);
	}
	
	@RequestMapping("/add*.json")
	public void add(HttpServletRequest request, HttpServletResponse response,
					@RequestParam("name") String name, @RequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@RequestMapping("/remove.json")
	public void remove(HttpServletRequest request,HttpServletResponse response,
		   @RequestParam("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@RequestMapping("/edit.json")
	public void edit(HttpServletRequest request,HttpServletResponse response,
			@RequestParam("id") Integer id,
			@RequestParam("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}

	@RequestMapping("/appear")
	public void appear(HttpServletRequest request,HttpServletResponse response){
		String result = queryService.appear();
		out(response,result);
	}
	
	
	
	private void out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
