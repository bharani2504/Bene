package com.example.bene.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
@Data
@Entity
@Table(name = "migration_schedule")
public class Migration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Lob
    @Column(name = "excel_file")
    private byte[] excelFile;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name="status")
    private String status;

    @Column(name = "uploaded_time")
    private Date uploadedTime;

    @Column(name = "completed_time")
    private Date completedTime;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "success_records")
    private Integer successRecords ;

    @Column(name = "failed_records")
    private Integer failedRecords ;
}