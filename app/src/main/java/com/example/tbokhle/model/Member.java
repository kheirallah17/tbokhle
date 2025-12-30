
package com.example.tbokhle.model;

public class Member {
    public String name;
    public String email;
    public boolean isAdmin;
    public int imageResId;

    public Member(String name, String email, boolean isAdmin, int imageResId) {
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
        this.imageResId = imageResId;
    }
}

