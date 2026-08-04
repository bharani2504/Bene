package com.example.bene.entity;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "mfa_Token")
public class MfaRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="token")
    private String token;

    @Column(name="otp")
    private String otp;

    @Column(name="status")
    private String status;

    @Column(name = "expiredAt")
    private Timestamp expiredat;

    @Column(name = "attempt")
    private int attempt;

    @Column(name = "isLocked")
    private boolean isLocked = false;

}
