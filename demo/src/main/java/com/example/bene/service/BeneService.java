package com.example.bene.service;


import com.example.bene.dto.*;
import com.example.bene.entity.MfaRequest;
import com.example.bene.exception.BeneficiaryException;
import com.example.bene.lock.BeneLock;
import com.example.bene.repo.BeneRepo;
import com.example.bene.repo.MfaTokenRepo;
import com.example.bene.security.Jwt;
import com.example.bene.util.EmailUtil;
import com.example.bene.util.ServiceUtil;
import com.example.bene.validator.BeneValidation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;

@Service
public class BeneService {

    private static BeneRepo benerepo;
    private static EmailService emailService;
    private static BeneValidation beneValidation;
    private static BeneLock beneLock;
    private static MfaTokenRepo mfaTokenRepo;
    private static Jwt jwt;


    private Bene bene;
    private EmailUtil emailUtil;

    public BeneService(BeneRepo benerepo, EmailService emailService, BeneValidation beneValidation, BeneLock beneLock, MfaTokenRepo mfaTokenRepo,Jwt jw){
        this.benerepo=benerepo;
        this.emailService=emailService;
        this.beneValidation=beneValidation;
        this.beneLock=beneLock;
        this.mfaTokenRepo=mfaTokenRepo;
        this.jwt=jwt;
    }

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
        response.setBeneNickName(bene.getBeneNickName());
        response.setCreatedDate(new Date(System.currentTimeMillis()));
       return response;
   }

    public Bene find (String beneNicknName) throws SQLException {
             bene=  benerepo.findone(beneNicknName);
             return bene;
    }

    public  BeneDeletedResponse delete(DeleteRequest request,String server) throws SQLException {
       BeneDeletedResponse response = new BeneDeletedResponse();
       String status="";
       String key = server + " " + request.getBeneNickName();
       Lock lock=beneLock.obtainLock(key);
       if(request.getBeneNickName()!=null) {
           boolean acquired = false;
           try {
               acquired=beneLock.tryLock(lock);
               if(!acquired){
                   throw  new BeneficiaryException("Technical error");
               }
               beneValidation.deleteValidator(request);
               status = benerepo.Delete(request);
           } finally {
               if (acquired) {
                   beneLock.unlock(lock);
               }
           }
       }
       response.setStatus(status);
       response.setBeneNickName(request.getBeneNickName());
       response.setDeletedAt(new Date(System.currentTimeMillis()));

       return response;
    }

    public ListResponse list(ListRequest request) throws SQLException {

        HttpServletRequest req= ServiceUtil.getServletRequest();
        String userCrn=ServiceUtil.getUserCrn(req);
        request.getFilters().add(new Filter("userCrn",userCrn));
        List response =benerepo.list(request);
        ListResponse re = new ListResponse();
        re.setData(response);
        int total = response.size();

        log.info("list size=>{}",total);
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
       if(request.getBeneNickName()!=null){
            beneValidation.amend(request);
            log.info("amend validation success");
            status= benerepo.amend(request);
        }
       response.setStatus(status);
       response.setBeneNickName(request.getBeneNickName());
       response.setLastUpdatedat(new Date(System.currentTimeMillis()));
       return response;
      }

    public List<AnalyticalResponse> analytical(AnalyticalRequest request) {
       List<AnalyticalResponse> response = new ArrayList<>();
        AnalyticalResponse rsp=new AnalyticalResponse();

        return response;
    }

    public AuthorizeResponse authorize(AuthorizeRequest request) throws SQLException {

        AuthorizeResponse response = new AuthorizeResponse();

        if (request.getBeneNickName() == null || request.getBeneNickName().isEmpty()) {
            BeneValidation.applyError("BeneNickName is Mandatory");
        }

        Bene bene = benerepo.findone(request.getBeneNickName());
        if (bene == null) {
            BeneValidation.applyError("Beneficiary does not exist");
        }

        if(bene.getDelFlag().equals("Y") && bene.getStatus().equals("Deleted")){
            BeneValidation.applyError("Beneficiary is Deleted");
        }

        if (!"Pending".equals(bene.getStatus())) {
            BeneValidation.applyError("Beneficiary is not in Pending status");
        }

        if (request.getMfaToken() == null) {

            MfaRequest req = new MfaRequest();
            String token = "BENE" + UUID.randomUUID().toString();
            String otp = String.format("%06d", new SecureRandom().nextInt(1000000));

                req.setToken(token);
                req.setOtp(otp);
                req.setStatus("Pending");
                req.setExpiredat(java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(2)));
                req.setAttempt(3);       // attempts remaining, persisted on the entity
                req.setLocked(false);
                mfaTokenRepo.save(req);

            response.setOtp(otp);
            response.setMfaToken(token);
            return response;

        } else {

            MfaRequest req = mfaTokenRepo.getMfaByToken(request.getMfaToken());
            if (req == null) {
                BeneValidation.applyError("Invalid MFA token");

            }

            if (LocalDateTime.now().isAfter(req.getExpiredat().toLocalDateTime())) {
                req.setStatus("Expired");
                mfaTokenRepo.save(req);
                BeneValidation.applyError("OTP expired. Please retry");
            }

            if (req.getOtp() == null || !req.getOtp().equals(request.getOtp())) {
                req.setStatus("Failed");
                int remaining = req.getAttempt() - 1;
                req.setAttempt(remaining);
                if (remaining <= 0) {
                    req.setLocked(true);
                    mfaTokenRepo.save(req);
                    BeneValidation.applyError("Maximum limit reached please try again after some time");
                }
                mfaTokenRepo.save(req);
                BeneValidation.applyError("Invalid OTP");
            }

            req.setStatus("Success");
            mfaTokenRepo.save(req);

            if ("Approve".equals(request.getAction())) {
                if(bene.getDelFlag().equals("Y")){
                    bene.setStatus("Deleted");
                }else {
                    bene.setStatus("Approved");
                }
                bene.setLastupdated(new Date(System.currentTimeMillis()));
            } else if ("Reject".equals(request.getAction())) {
                if("Beneficiary Deletion".equalsIgnoreCase(bene.getRequestType())) {
                    if (bene.getDelFlag().equals("Y") && bene.getStatus().equals("Pending")) {
                        bene.setDelFlag("N");
                    }
                }
                bene.setStatus("Rejected");
                bene.setLastupdated(new Date(System.currentTimeMillis()));
                bene.setRemarks(request.getRejectReason());
            } else {
                BeneValidation.applyError("Invalid action. Must be Approve or Reject");
            }

            Amend amend = new Amend();
            BeanUtils.copyProperties(bene, amend);
            benerepo.amend(amend);

            response.setBeneNickName(bene.getBeneName());
            response.setStatus("Success");
            return response;
        }
    }
}
