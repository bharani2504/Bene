package com.example.demo.service;


import com.example.demo.model.Bene;
import com.example.demo.repo.benerepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;

@Service
public class beneService {

    @Autowired
    private benerepo benerepo;

   public void insret(Bene bene) throws SQLException {
       benerepo.insert(bene);
   }

    public Bene find (String beneNicknName) throws SQLException {
         Bene bene =new Bene();
             bene=  benerepo.findone(beneNicknName);
       return bene;
    }
}
