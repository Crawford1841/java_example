package org.example.template;

import java.util.List;
import org.example.App;
import org.example.entity.Employee;
import org.example.respository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class)
public class MongodbSpringBootRepository {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void add(){
        Employee employee = Employee.builder().id("11").firstName("huang").lastName("wei").salary(1231234).build();
        employeeRepository.save(employee);
    }

    @Test
    public void finAll(){
        List<Employee> all = employeeRepository.findAll();
        all.forEach(System.out::println);
    }


}
