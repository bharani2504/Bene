package com.example.demo.service;


import com.example.demo.model.Bene;
import com.example.demo.model.ListResponse;
import com.example.demo.repo.benerepo;
import com.example.demo.util.EmailUtil;
import com.example.demo.validator.BeneValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class beneService {

    @Autowired
    private benerepo benerepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BeneValidation beneValidation;

    private Bene bene;
    private EmailUtil emailUtil;

   public String insret(Bene bene) throws SQLException, IOException {
       beneValidation.ifscValidation(bene);
        String Status =benerepo.insert(bene);
        if(Status.equals("Success")){
            if (bene.getEmail() != null &&
                    bene.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                String subject = "Beneficiary Created Successfully";
                String body = emailUtil.CreatedTemplate(bene);
                try {
                    emailService.sendMail(bene.getEmail(), subject, body);
                } catch (Exception e) {
                    System.out.println("Mail sending failed: " + e.getMessage());
                }
            }
        }
       return Status;
   }

    public Bene find (String beneNicknName) throws SQLException {
             bene=  benerepo.findone(beneNicknName);
       return bene;
    }

    public void delete(String beneNicknName) throws SQLException {
        benerepo.delete(beneNicknName);
    }

    public ListResponse list(boolean fetchChild) throws SQLException {
       List response =benerepo.list(fetchChild);
        ListResponse re = new ListResponse();
        re.setData(response);
        int total = response.size();
        if(!response.isEmpty()){
           re.setStatus("success");
           re.setTotal(total);

        }
       return re;
    }
}
