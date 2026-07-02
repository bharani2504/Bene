--liquibase formatted sql

--changeset beneficiary:Account_table-2026070200
ALTER TABLE account ADD COLUMN branch VARCHAR(255);