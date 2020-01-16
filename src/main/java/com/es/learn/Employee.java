package com.es.learn;

public class Employee {
 private    String name;
 private    int age;
 private    String position;
 private    String country;
 private    String join_date;
 private    int salary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Employee() {
    }

    public Employee(String name, int age, String position, String country, String join_date, int salary) {
        this.name = name;
        this.age = age;
        this.position = position;
        this.country = country;
        this.join_date = join_date;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", position='" + position + '\'' +
                ", country='" + country + '\'' +
                ", join_date='" + join_date + '\'' +
                ", salary=" + salary +
                '}';
    }
}
