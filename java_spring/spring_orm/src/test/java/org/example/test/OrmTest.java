package org.example.test;

import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.core.common.Page;
import org.example.demo.dao.MemberDao;
import org.example.demo.dao.OrderDao;
import org.example.demo.entity.Member;
import org.example.demo.entity.Order;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 */
@ContextConfiguration(locations = {"classpath:application-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class OrmTest {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmdd");

    @Autowired private MemberDao memberDao;

    @Autowired private OrderDao orderDao;

    @Test
    //@Ignore
    public void testSelectForPage(){
        try {
           Page page = memberDao.selectForPage(2, 3);
            System.out.println("总条数： " + page.getTotal());
            System.out.println("当前第几页：" + page.getPageNo());
            System.out.println("每页多少条：" + page.getPageSize());
            System.out.println("本页的数据：" + JSON.toJSONString(page.getRows(),true));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Test
    //@Ignore
    public void testSelectAllForMember(){
        try {
            List<Member> result = memberDao.selectAll();
            System.out.println(JSON.toJSONString(result,true));
//            System.out.println(Arrays.toString(result.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //@Ignore
    public void testInsertMember(){
        try {
            String [] members = new String[]{
                    "Tom,Hunan Changsha,18", "Tomcat,Beijing,16",
                    "Mic,Hunan Changsha,27", "James,Hunan Changsha,26",
                    "Bob,Shanghai,29", "Tony,Guangdong Shenzhen,32",
                    "Jerry,Jiangsu Suzhou,30"
            };

            for (String value : members) {
                String[] member = value.split(",");
                memberDao.insert(new Member(member[0],member[1],Integer.valueOf(member[2])));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Test
//	@Ignore
    public void testInsertOrder(){
        try {
            Order order = new Order();
            order.setMemberId(1L);
            order.setDetail("历史订单");
            Date date = sdf.parse("20210421123456");
            order.setCreateTime(date.getTime());
            orderDao.insertOne(order);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
