package com.vert.tut.db_proj;

public class User {
  private String name;
  private int age;
  private String country;

  // Constructors
  public User() {}

  public User(String name, int age, String country) {
    this.name = name;
    this.age = age;
    this.country = country;
  }

  // Getters and Setters
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public int getAge() { return age; }
  public void setAge(int age) { this.age = age; }

  @Override
  public String toString() {
    return "User{" +
      "name='" + name + '\'' +
      ", age=" + age +
      ", country='" + country + '\'' +
      '}';
  }

  public String getCountry() { return country; }
  public void setCountry(String country) { this.country = country; }
}
