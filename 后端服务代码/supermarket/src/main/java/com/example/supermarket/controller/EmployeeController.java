package com.example.supermarket.controller;

import com.example.supermarket.entity.Employee;
import com.example.supermarket.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/batch")
    public ResponseEntity<String> addEmployeeBatch(@RequestBody List<Employee> employeeList) {
        boolean success = employeeService.saveEmployeeBatch(employeeList);
        return success ? ResponseEntity.ok("员工录入成功") : ResponseEntity.badRequest().body("员工录入失败");
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PutMapping
    public ResponseEntity<String> updateEmployee(@RequestBody Employee employee) {
        return employeeService.modifyEmployee(employee) ? ResponseEntity.ok("修改成功") : ResponseEntity.badRequest().body("修改失败");
    }

    @DeleteMapping("/{eId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String eId) {
        return employeeService.removeEmployee(eId) ? ResponseEntity.ok("删除成功") : ResponseEntity.badRequest().body("删除失败");
    }
}