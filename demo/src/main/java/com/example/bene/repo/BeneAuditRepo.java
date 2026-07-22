package com.example.bene.repo;

import com.example.bene.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneAuditRepo extends JpaRepository<Audit,Long> {

}
