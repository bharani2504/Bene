
--liquibase formatted sql
--changeset beneficiary:Bene_Migration-2026072000

CREATE TABLE migration_schedule (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
file_name VARCHAR(255) ,
excel_file LONGBLOB NOT NULL,
cron_expression VARCHAR(100) NOT NULL,
status VARCHAR(20) DEFAULT 'PENDING',
uploaded_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
completed_time TIMESTAMP NULL,
total_records INT DEFAULT 0,
success_records INT DEFAULT 0,
failed_records INT DEFAULT 0
);

--changeset beneficiary:Bene_Migration-2026072100
ALTER TABLE migration_schedule MODIFY COLUMN excel_file LONGBLOB NULL;
ALTER TABLE migration_schedule MODIFY COLUMN cron_expression VARCHAR(100) NULL;
ALTER TABLE migration_schedule MODIFY COLUMN uploaded_time TIMESTAMP NULL;