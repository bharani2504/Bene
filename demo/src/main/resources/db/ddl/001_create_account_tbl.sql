--liquibase formatted sql

--changeset beneficiary:Account_table-2026070200
ALTER TABLE account ADD COLUMN branch VARCHAR(255);

--changeset beneficiary:Account_table-2026070600
ALTER table bene add column referenceId VARCHAR(255);

--changeset beneficiary:Bene_table-2026070700
ALTER table bene add column delFlag VARCHAR(10);

--changeset beneficiary:Bene_table-2026070701
ALTER table account add column delAccFlag VARCHAR(10);

--changeset beneficiary:Bene_table-2026070900
ALTER table account add column remarks VARCHAR(255);

--changeset beneficiary:Bene_table-2026070901
ALTER table bene add column remarks VARCHAR(255);

--changeset beneficiary:Bene_table-2026071200
ALTER table bene add column lastupdated TIMESTAMP(6);

--changeset beneficiary:Bene_table-2026071201
ALTER table account add column lastupdated TIMESTAMP(6);

--changeset beneficiary:Bene_table-2026071202
ALTER table account add column accountType VARCHAR(50);

--changeset beneficiary:Bene_table-2026071203
ALTER table bene add column createdDate TIMESTAMP(6);

--changeset beneficiary:Bene_table-2026071600
ALTER table bene add column status VARCHAR(50);
