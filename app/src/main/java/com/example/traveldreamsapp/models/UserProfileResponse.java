package com.example.traveldreamsapp.models;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {

    private int id;

    private String image;

    private String address;

    private String location;

    @SerializedName("mail")
    private String mail;

    @SerializedName("telephone")
    private String telephone;

    private String dni;

    private int user;

    private String email;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    // Constructor vacío
    public UserProfileResponse() {}

    // Getters y setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public int getUser() { return user; }
    public void setUser(int user) { this.user = user; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
