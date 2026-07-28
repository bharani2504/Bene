package com.example.bene.repo;

import com.example.bene.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface CorpRepo extends JpaRepository<UserSession,Long>{

    UserSession findbyUsercrn(String userCRN);

}
