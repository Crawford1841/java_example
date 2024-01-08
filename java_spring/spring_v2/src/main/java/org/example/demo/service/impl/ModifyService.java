package org.example.demo.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.example.demo.service.IModifyService;
import org.example.spring.framework.annotation.Autowired;
import org.example.spring.framework.annotation.Service;

/**
 * 增删改业务
 * @author Tom
 *
 */
@Service
@Slf4j
public class ModifyService implements IModifyService {
	/**
	 * 循环依赖的问题复现
	 */
	@Autowired
	private QueryService queryService;

	/**
	 * 增加
	 */
	public String add(String name,String addr) {
		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}

	@Override
	public String appear() {
		log.info("========调用循环依赖=========");
		return queryService.appear();
		//return "request success!";
	}

}
