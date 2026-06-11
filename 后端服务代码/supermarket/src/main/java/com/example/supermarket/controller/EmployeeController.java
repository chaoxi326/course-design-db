package com.example.supermarket.controller;

import com.example.supermarket.common.Result;
import com.example.supermarket.entity.Employee;
import com.example.supermarket.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/batch")
    public Result<Void> addEmployeeBatch(@RequestBody List<Employee> employeeList) {
        return employeeService.saveEmployeeBatch(employeeList)
                ? Result.success()
                : Result.fail("员工录入失败");
    }

    @GetMapping
    public Result<List<Employee>> getAllEmployees() {
        return Result.success(employeeService.getAllEmployees());
    }

    @PutMapping
    public Result<Void> updateEmployee(@RequestBody Employee employee) {
        return employeeService.modifyEmployee(employee)
                ? Result.success()
                : Result.fail("修改失败");
    }

    @DeleteMapping("/{eId}")
    public Result<Void> deleteEmployee(@PathVariable String eId) {
        return employeeService.removeEmployee(eId)
                ? Result.success()
                : Result.fail("删除失败");
    }

    @PostMapping("/login")
    public Result<Employee> login(@RequestBody Employee loginReq) {
        Employee emp = employeeService.login(loginReq.getEId(), loginReq.getEPassword());
        if (emp == null) {
            return Result.fail("工号或密码错误");
        }
        return Result.success(emp);
    }
}
