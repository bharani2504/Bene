package com.example.bene.service;

import com.example.bene.entity.UserSession;
import com.example.bene.repo.CorpRepo;
import com.example.bene.util.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorpCrnService {

    @Autowired
    private CorpRepo corpRepo;

     public void save(UserSession userSession) {
         String password= Hashing.hashPassword(userSession.getPassword());
         userSession.setPassword(password);
         corpRepo.save(userSession);
    }


}
