package com.tiendita.pos.model;

/**
 * Representa un proveedor o distribuidor de mercancías.
 */
public class Provider {
    private Integer id;
    private String name;
    private String contactName;
    private String phone;
    private String email;

    public Provider() {}

    public Provider(Integer id, String name, String contactName, String phone, String email) {
        this.id = id;
        this.name = name;
        this.contactName = contactName;
        this.phone = phone;
        this.email = email;
    }

    public Provider(String name, String contactName, String phone, String email) {
        this.name = name;
        this.contactName = contactName;
        this.phone = phone;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return name;
    }
}
