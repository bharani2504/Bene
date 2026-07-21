package com.example.demo.repo;

import com.example.demo.entity.Migration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneMigrationRepo extends JpaRepository<Migration,Long> {


}
