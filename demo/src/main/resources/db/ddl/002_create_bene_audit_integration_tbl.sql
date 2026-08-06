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

--changeset beneficiary:Bene_Auidt-2026070600
ALTER table BENE_INTEGRATION_AUDIT add column service_url VARCHAR(255);

--changeset beneficiary:Bene_Auidt-2026080600
ALTER table BENE_INTEGRATION_AUDIT drop column beneNickName;

--changeset beneficiary:Bene_Auidt-2026080601
ALTER table BENE_INTEGRATION_AUDIT add column REQUEST_TIME TIMESTAMP(6);
ALTER table BENE_INTEGRATION_AUDIT add column RESPONSE_TIME TIMESTAMP(6);