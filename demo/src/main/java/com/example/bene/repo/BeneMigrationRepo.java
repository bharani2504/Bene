package com.example.bene.repo;

import com.example.bene.entity.Migration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneMigrationRepo extends JpaRepository<Migration,Long> {


}
