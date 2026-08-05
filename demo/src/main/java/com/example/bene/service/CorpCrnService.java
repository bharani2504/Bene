package com.example.bene.service;

import com.example.bene.entity.UserSession;
import com.example.bene.repo.CorpRepo;
import com.example.bene.util.Hashing;
import com.example.bene.validator.BeneValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorpCrnService {

    @Autowired
    private CorpRepo corpRepo;

     public void save(UserSession userSession) {
         String password= Hashing.hashPassword(userSession.getPassword());
         userSession.setPassword(password);
         if(userSession!=null){
             UserSession sess=corpRepo.findByUserCrn(userSession.getUserCrn());
              if(sess!=null){
                 BeneValidation.applyError("userCrn already exsists");
             }
         }
         corpRepo.save(userSession);
    }


}
