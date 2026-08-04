package com.example.bene.repo;

import com.example.bene.entity.MfaRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MfaTokenRepo extends JpaRepository<MfaRequest,Long> {
    MfaRequest getMfaByToken(String mfaToken);
}
