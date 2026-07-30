package com.example.bene.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name ="corp_crn_details")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "corp_crn")
    private String corpCRN;

    @Column(name = "user_crn")
    private String userCrn;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

}
