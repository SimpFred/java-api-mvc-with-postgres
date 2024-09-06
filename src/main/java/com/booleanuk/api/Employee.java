package com.booleanuk.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {
    private long id;
    private String name;
    private String jobName;
    private String salaryGrade;
    private String department;

    public Employee(long id, String name, String jobName, String salaryGrade, String department) {
        this.id = id;
        this.name = name;
        this.jobName = jobName;
        this.salaryGrade = salaryGrade;
        this.department = department;
    }

    public String toString() {
        String result = "";
        result += this.id + " - ";
        result += this.name + " - ";
        result += this.jobName + " - ";
        return result;
    }

}
