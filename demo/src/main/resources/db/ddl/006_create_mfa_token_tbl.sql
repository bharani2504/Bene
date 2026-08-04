--liquibase formatted sql
--changeset beneficiary:Bene_MFA_TOKEN-2026080400


create table mfa_Token (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   token VARCHAR(100),
   otp VARCHAR(150),
   status  VARCHAR (200),
   expiredat TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   attempt Integer,
   isLocked boolean
);