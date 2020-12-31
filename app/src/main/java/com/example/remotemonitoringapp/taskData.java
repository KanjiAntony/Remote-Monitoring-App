package com.example.remotemonitoringapp;

public class taskData {
    String task;
    String deadline;
    String employee;
    String task_id;

    taskData(String task, String deadline, String employee,String task_id)
    {
        this.task = task;
        this.deadline = deadline;
        this.employee = employee;
        this.task_id = task_id;

    }
}