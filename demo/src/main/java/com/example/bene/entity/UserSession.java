package com.example.bene.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name ="USER_DETAILS")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "corp_crn")
    private String corpCRN;

    @Column(name = "user_crn")
    private String userCRN;

    @Column(name = "password")
    private String password;

    @Column(name = "roles")
    private String role;

}
