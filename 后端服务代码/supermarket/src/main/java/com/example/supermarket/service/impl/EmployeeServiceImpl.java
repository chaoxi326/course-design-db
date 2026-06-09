package com.example.supermarket.service.impl;

import com.example.supermarket.entity.Employee;
import com.example.supermarket.mapper.EmployeeMapper;
import com.example.supermarket.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean saveEmployeeBatch(List<Employee> employeeList) {
        if (employeeList == null || employeeList.isEmpty()) return false;
        for (Employee emp : employeeList) {
            if (emp.getEPassword() != null && !emp.getEPassword().isEmpty()) {
                emp.setEPassword(passwordEncoder.encode(emp.getEPassword()));
            }
        }
        return employeeMapper.insertEmployeeBatch(employeeList) > 0;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeMapper.selectAllEmployees();
    }

    @Override
    public boolean modifyEmployee(Employee employee) {
        String pwd = employee.getEPassword();
        if (pwd != null && !pwd.isEmpty() && !pwd.startsWith("$2")) {
            employee.setEPassword(passwordEncoder.encode(pwd));
        }
        return employeeMapper.updateEmployee(employee) > 0;
    }

    @Override
    public boolean removeEmployee(String eId) {
        return employeeMapper.deleteEmployeeById(eId) > 0;
    }

    @Override
    public Employee login(String eId, String password) {
        Employee emp = employeeMapper.selectEmployeeById(eId);
        if (emp == null) return null;
        if (passwordEncoder.matches(password, emp.getEPassword())) {
            emp.setEPassword(null);
            return emp;
        }
        return null;
    }
}
