package com.nextcar.carrental.dto;

public class MeResponseDTO {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;

    public MeResponseDTO(Integer id, String email, String firstName, String lastName, String phone, String role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
    }

    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
}
