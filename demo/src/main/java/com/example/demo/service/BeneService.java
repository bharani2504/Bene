package com.example.demo.service;


import com.example.demo.model.*;
import com.example.demo.repo.BeneRepo;
import com.example.demo.util.EmailUtil;
import com.example.demo.validator.BeneValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class BeneService {

    @Autowired
    private BeneRepo benerepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BeneValidation beneValidation;

    private Bene bene;
    private EmailUtil emailUtil;
    private static final Logger log = LoggerFactory.getLogger(BeneService.class);

   public String insret(Bene bene) throws SQLException, IOException {

       log.info("Beneficiary validation");
       beneValidation.submitRequestValidation(bene);

        log.info("Submit validation success");
        String Status =benerepo.insert(bene);
        if(Status.equals("Success")){
            if (bene.getEmail() != null &&
                    bene.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                String subject = "Beneficiary Created Successfully";
                String body = emailUtil.CreatedTemplate(bene);
                try {
                    log.info("mail service started");
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

    public void delete(DeleteRequest request) throws SQLException {
       if(request.getBeneNickName()!=null) {
           beneValidation.deleteValidator(request);
           benerepo.Delete(request);
       }
    }

    public ListResponse list(ListRequest request) throws SQLException {
        List response =benerepo.list(request);
        ListResponse re = new ListResponse();
        re.setData(response);
        int total = response.size();

        log.info("list size=>",total);
        if(!response.isEmpty()){
           re.setStatus("success");
           re.setTotal(total);

        }
       return re;
    }


    public String referenceId() {
        UUID uuid = UUID.randomUUID();
        String number = new BigInteger(uuid.toString().replace("-", ""), 16).toString();
        return "REF" + number.substring(0, 15);
    }

    public void amend(Amend request) throws SQLException {
        if(request.getBeneNicknName()!=null){
            beneValidation.amend(request);
            log.info("amend validation success");
            benerepo.amend(request);
        }
      }
}
