--liquibase formatted sql

--changeset beneficiary:Account_table-2026070200
ALTER TABLE account ADD COLUMN branch VARCHAR(255);

--changeset beneficiary:Account_table-2026070600
ALTER table bene add column referenceId VARCHAR(255);

--changeset beneficiary:Bene_table-2026070700
ALTER table bene add column delFlag VARCHAR(10);

--changeset beneficiary:Bene_table-2026070701
ALTER table account add column delAccFlag VARCHAR(10);