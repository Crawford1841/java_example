package org.example.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.service.IModifyService;
import org.example.demo.service.IQueryService;
import org.example.spring.framework.annotation.Autowired;
import org.example.spring.framework.annotation.Service;

/**
 * 查询业务
 * @author Tom
 *
 */
@Service
@Slf4j
public class QueryService implements IQueryService {
	/**
	 * 循环依赖的问题复现
	 */
	@Autowired
	private IModifyService modifyService;

	/**
	 * 查询
	 */
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		log.info("这是在业务方法中打印的：" + json);
		return json;
	}

	@Override
	public String appear() {
		log.info("========调用循环依赖=========");
		return modifyService.appear();
		//return "request success!";
	}

}
