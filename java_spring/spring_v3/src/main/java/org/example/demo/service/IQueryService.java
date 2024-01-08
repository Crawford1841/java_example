package org.example.demo.service;

/**
 * 查询业务
 * @author Tom
 *
 */
public interface IQueryService {
	
	/**
	 * 查询
	 */
	public String query(String name);

	/**
	 * 循环依赖复现
	 */
	public String appear();

}
