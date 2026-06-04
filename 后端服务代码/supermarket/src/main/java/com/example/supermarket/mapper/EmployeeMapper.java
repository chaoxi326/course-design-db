package com.example.supermarket.mapper;

import com.example.supermarket.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EmployeeMapper {
    // 1. 批量录入员工数据（单条员工数据也走这里）
    int insertEmployeeBatch(@Param("employeeList") List<Employee> employeeList);

    // 2. 查询并列出所有员工信息
    List<Employee> selectAllEmployees();

    // 3. 修改指定员工信息
    int updateEmployee(Employee employee);

    // 4. 删除指定员工数据
    int deleteEmployeeById(String eId);
}
