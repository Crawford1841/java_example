package org.example.demo.dao;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.core.common.Page;
import javax.sql.DataSource;
import org.example.demo.entity.Member;
import org.example.framework.BaseDaoSupport;
import org.example.framework.QueryRule;
import org.springframework.stereotype.Repository;

/**
 * Created by Tom.
 */
@Repository
public class MemberDao extends BaseDaoSupport<Member,Long> {

    @Override
    protected String getPKColumn() {
        return "id";
    }


    @Resource(name="dataSource")
    public void setDataSource(DataSource dataSource){
        super.setDataSourceReadOnly(dataSource);
        super.setDataSourceWrite(dataSource);
    }


    public List<Member> selectAll() throws  Exception{
        QueryRule queryRule = QueryRule.getInstance();
//        queryRule.andLike("name","Mic%");
        return super.select(queryRule);
    }


    public Page<Member> selectForPage(int pageNo,int pageSize) throws Exception{
        QueryRule queryRule = QueryRule.getInstance();
        queryRule.andLike("name","Tom%");
        Page<Member> page = super.select(queryRule,pageNo,pageSize);
        return page;
    }

    public void select() throws Exception{
        String sql = "";
        List<Map<String,Object>> result = super.selectBySql(sql);
//        System.out.println(JSON.parseObject(JSON.toJSONString(result)),Member.class);
    }

    public boolean insert(Member entity) throws Exception{
//        super.setTableName("t_mmmmm");
        return super.insert(entity);
    }
}
