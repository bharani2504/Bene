package com.example.bene.service;

import com.example.bene.entity.UserSession;
import com.example.bene.repo.CorpRepo;
import com.example.bene.security.Jwt;
import com.example.bene.util.Hashing;
import com.example.bene.validator.BeneValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private CorpRepo corpRepo;

    @Autowired
    private BeneValidation validation;

    @Autowired
    private Jwt jwt;

     public void login(UserSession session){

         UserSession sess=corpRepo.findbyUsercrn(session.getUserCRN());
         String hashpassword=sess.getPassword();

         if(Hashing.verifyPassword(session.getPassword(),hashpassword)){
            String accessTokem=jwt.generateAccessToken(sess);
            String refreshToken= jwt.generateRefreshToken(sess);
         }else{
             validation.applyError("Give the correct Password");
         }

     }


}
