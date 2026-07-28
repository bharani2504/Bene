
--liquibase formatted sql
--changeset beneficiary:Bene_Lock-2026072800

  create table corp_crn_details(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   corp_crn VARCHAR(100),
   user_crn VARCHAR(150),
   password  VARCHAR (200),
   role VARCHAR(150)
 );