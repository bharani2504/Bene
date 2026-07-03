--liquibase formatted sql
--changeset beneficiary:Bene_Auidt-2026070300

  create table BENE_INTEGRATION_AUDIT
  (
      audit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
      serviceName VARCHAR(100),
      operationName varchar(255),
      beneNickName Varchar(255),
      referenceId Varchar(255),
      context varchar(255),
      status VARCHAR(100),
      responseString VARCHAR(500),
      data VARCHAR(500)
);