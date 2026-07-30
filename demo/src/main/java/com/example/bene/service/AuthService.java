package com.example.bene.service;

import com.example.bene.entity.UserSession;
import com.example.bene.repo.CorpRepo;
import com.example.bene.security.Jwt;
import com.example.bene.util.Hashing;
import com.example.bene.validator.BeneValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private CorpRepo corpRepo;

    @Autowired
    private BeneValidation validation;

    @Autowired
    private Jwt jwt;

     public Map<String,Object> login(UserSession session){

         Map<String,Object>mp=new HashMap<>();
         UserSession sess=corpRepo.findByUserCrn(session.getUserCrn());
         String hashpassword=sess.getPassword();
         String accessTokem="";
         String refreshToken="";
         if(Hashing.verifyPassword(session.getPassword(),hashpassword)){
             accessTokem=jwt.generateAccessToken(sess);
             refreshToken= jwt.generateRefreshToken(sess);
         }else{
             validation.applyError("Give the correct Password");
         }
         mp.put("accessTokem",accessTokem);
         mp.put("refreshToken",refreshToken);

         return mp;
     }


}
