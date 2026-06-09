package com.example.supermarket.service;

import com.example.supermarket.entity.Employee;
import java.util.List;

public interface EmployeeService {
    boolean saveEmployeeBatch(List<Employee> employeeList);
    List<Employee> getAllEmployees();
    boolean modifyEmployee(Employee employee);
    boolean removeEmployee(String eId);
    Employee login(String eId, String password);
}