package com.example.supermarket.service.impl;

import com.example.supermarket.entity.Employee;
import com.example.supermarket.mapper.EmployeeMapper;
import com.example.supermarket.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    @Override
    public boolean saveEmployeeBatch(List<Employee> employeeList) {
        if (employeeList == null || employeeList.isEmpty()) return false;
        return employeeMapper.insertEmployeeBatch(employeeList) > 0;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeMapper.selectAllEmployees();
    }

    @Override
    public boolean modifyEmployee(Employee employee) {
        return employeeMapper.updateEmployee(employee) > 0;
    }

    @Override
    public boolean removeEmployee(String eId) {
        return employeeMapper.deleteEmployeeById(eId) > 0;
    }
}