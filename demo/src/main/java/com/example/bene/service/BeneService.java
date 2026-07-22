package com.example.bene.service;


import com.example.bene.dto.*;
import com.example.bene.repo.BeneRepo;
import com.example.bene.util.EmailUtil;
import com.example.bene.validator.BeneValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

   public BeneSubmitResponse insret(Bene bene) throws SQLException, IOException {
       BeneSubmitResponse response = new BeneSubmitResponse();
       log.info("Beneficiary validation");
       beneValidation.submitRequestValidation(bene);

        log.info("Submit validation success");
        String status =benerepo.insert(bene);
        if(status.equals("Success")){
            if (bene.getEmail() != null &&
                    bene.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                String subject = "Beneficiary Created Successfully";
                String body = emailUtil.CreatedTemplate(bene);
                try {
                    log.info("mail service started");
                    CompletableFuture.runAsync(() -> {
                        emailService.sendMail(bene.getEmail(), subject, body);
                    });
                } catch (Exception e) {
                    System.out.println("Mail sending failed: " + e.getMessage());
                }
            }
        }
        response.setStatus(status);
        response.setBeneNickName(bene.getBeneNicknName());
        response.setCreatedDate(new Date(System.currentTimeMillis()));
       return response;
   }

    public Bene find (String beneNicknName) throws SQLException {
             bene=  benerepo.findone(beneNicknName);
             return bene;
    }

    public  BeneDeletedResponse delete(DeleteRequest request) throws SQLException {
       BeneDeletedResponse response = new BeneDeletedResponse();
       String status="";
       if(request.getBeneNickName()!=null) {
           beneValidation.deleteValidator(request);
           status= benerepo.Delete(request);
       }
       response.setStatus(status);
       response.setBeneNickName(request.getBeneNickName());
       response.setDeletedAt(new Date(System.currentTimeMillis()));

       return response;
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

    public AmendBeneResponse amend(Amend request) throws SQLException {
       AmendBeneResponse response = new AmendBeneResponse();
       String status="";
       if(request.getBeneNicknName()!=null){
            beneValidation.amend(request);
            log.info("amend validation success");
            status= benerepo.amend(request);
        }
       response.setStatus(status);
       response.setBeneNickName(request.getBeneNicknName());
       response.setLastUpdatedat(new Date(System.currentTimeMillis()));
       return response;
      }
}
