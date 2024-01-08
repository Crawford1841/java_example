package org.example.template;

import com.mongodb.client.result.UpdateResult;
import java.util.List;
import org.example.App;
import org.example.entity.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class)
/**
 * 1、检查SpringBoot是否正常启动
 * 2、编写测试用例，注意方法不能为private
 */
public class MongodbSpringBootTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    //@Test
    //public void add(){
    //    Employee employee = Employee.builder().id("22").firstName("huang").lastName("wei").empId(2).salary(20000).build();
    //    mongoTemplate.save(employee);
    //}

    @Test
    protected void findAll() {
        List<Employee> employeeList = mongoTemplate.findAll(Employee.class);
        employeeList.forEach(System.out::println);
    }

    @Test
    public void findById() {
        Employee employee = Employee.builder().id("22").build();
        Query query = new Query(where("id").is(employee.getId()));
        List<Employee> employees = mongoTemplate.find(query, Employee.class);
        employees.forEach(System.out::println);

    }

    @Test
    public void findByName() {
        Employee employee = Employee.builder().lastName("wei").build();
        Query query = new Query(where("lastName").is(employee.getLastName()));
        List<Employee> employees = mongoTemplate.find(query, Employee.class);
        employees.forEach(System.out::println);
    }

    @Test
    public void update() {
        Employee employee = Employee.builder().id("22").build();
        //使用更新的文档更新所有与查询文档条件匹配的对象
        Query query = new Query(where("id").is(employee.getId()));
        UpdateDefinition updateDefinition = new Update().set("lastName", "hello world");
        UpdateResult updateResult = mongoTemplate.updateMulti(query,updateDefinition, Employee.class);
        System.out.println("update id:{}" + updateResult.getUpsertedId());
    }

    @Test
    public void del() {
        Employee employee = Employee.builder().lastName("hello world").build();
        Query query = new Query(where("lastName").is(employee.getLastName()));
        mongoTemplate.remove(query,Employee.class);
    }

}
