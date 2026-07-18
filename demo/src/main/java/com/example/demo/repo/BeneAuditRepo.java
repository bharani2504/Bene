package com.example.demo.repo;

import com.example.demo.dto.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneAuditRepo extends JpaRepository<Audit,Long> {

}
