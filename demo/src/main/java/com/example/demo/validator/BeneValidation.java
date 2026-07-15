package com.example.demo.validator;

import com.example.demo.exception.BeneficiaryException;
import com.example.demo.model.*;
import com.example.demo.repo.BeneRepo;
import com.example.demo.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BeneValidation {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonService commonService;

    @Autowired
    private BeneRepo benerepo ;

    private static final Logger log = LoggerFactory.getLogger(BeneValidation.class);
    public void submitRequestValidation (Bene bene) throws SQLException {

        log.info("request=>",bene);
        if(bene.getBeneNicknName()!=null){

            Bene bn=benerepo.findone(bene.getBeneNicknName());
            if(bene.getBeneNicknName()==null){
               applyError("bene nick name is mandatory");
            }

            if(bn!=null && bn.getBeneNicknName()!=null) {
                if (bn.getBeneNicknName().equals(bene.getBeneNicknName())) {
                   applyError("Beneficiary nick name is already exsists");
                }
            }

            if( !bene.getBeneNicknName().matches("^[A-Za-z0-9]+(?: [A-Za-z0-9]+)*$")){
                applyError("Beneficiary nick name should contains only Alphanumeric");
            }

            if(bene.getMobile()==null){
                applyError("bene mobile number is mandatory");
            }
            if(bene.getMobile().matches("^[6-9]\\d{9}$")){
                applyError("Invalid mobile number format");
            }

            if(bene.getEmail()!=null) {
                if (!bene.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                   applyError("Invalid email");
                }
            }

            log.info("Bene ifsc validation");
            bene.setCreatedDate(new Date(System.currentTimeMillis()));
            ifscValidation(bene);
            for(Account ac : bene.getAccount()){
                if(ac.getAccountNumber()==null){
                    applyError("account number is mandatory");
                }
                if(ac.getIFSC()==null){
                    applyError("Ifsc code is mandatory");
                }

                if(!ac.getIFSC().toUpperCase().matches("^[A-Z]{4}0[A-Z0-9]{6}$")){
                    applyError("Invalid Ifsc format");
                }
            }
        }
    }

    public void ifscValidation(Bene bene) {

       try{
           ServiceRequest serviceRequest = new ServiceRequest();
           if (bene.getAccount() != null) {
               for (Account act : bene.getAccount()) {
                   if (act.getIFSC() != null) {
                       serviceRequest.setBeneNickname(bene.getBeneName());
                       Map<String,Object> req=new HashMap<>();
                       req.put("ifsc", act.getIFSC());

                       serviceRequest.setContext(req);
                       serviceRequest.setRefenceID(bene.getReferenceId());
                       log.info("service request=>",serviceRequest);

                       ServiceResponse response=commonService.getIfsc(serviceRequest);
                       Map<String,Object>data=response.getData();
                       String ifsc = String.valueOf(data.getOrDefault("ifsc_code", ""));
                       if (!ifsc.equalsIgnoreCase(act.getIFSC())) {
                           applyError("INVALID IFSC CODE");
                       }
                       String bank = String.valueOf(data.getOrDefault("bank_name", ""));
                       String branch = String.valueOf(data.getOrDefault("branch",""));
                       act.setBranch(branch);
                       act.setBank(bank);
                   }
               }
           }
       } catch (RuntimeException e) {
           throw new RuntimeException(e);
       }
    }


    public BeneficiaryException applyError(String error){
        throw  new BeneficiaryException(error);
    }

    public void deleteValidator(DeleteRequest request) throws SQLException {

     Bene bene=benerepo.findone(request.getBeneNickName());
     if(bene.getDelFlag()!= null && bene.getDelFlag().equals("Y")){
         applyError("Beneficiary is already marked as deleted");
     }
     request.setDelFlag("Y");
    }

    public void amend(Amend request) throws SQLException {

        log.info("amend request request validation started");
        Bene bn=benerepo.findone(request.getBeneNicknName());

        if(bn.getDelFlag()!= null ){
          if( bn.getDelFlag().equals("Y")){
              applyError("Beneficiary is already deleted");
          }
        }
        if(bn==null){
            applyError("Beneficiary not found");
        }
        if(!bn.getBeneNicknName().equalsIgnoreCase(request.getBeneNicknName())){
            applyError("Beneficiary nick name is non amendable");
        }

          List<Account> ac = bn.getAccount();
          List<Account>amendAccount =request.getAccount();

         Map<String, Account> existingMap = ac.stream()
                .collect(Collectors.toMap(Account::getAccountNumber, a -> a));

        Date dt=new Date(System.currentTimeMillis());
        for(Account acc : amendAccount){
            Account accc = existingMap.get(acc.getAccountNumber());

            if(accc==null){
                applyError("account number is non amendable filed");
            }

            acc.setLastupdated(dt);
        }

        request.setLastupdated(dt);
        request.setAccount(amendAccount);

    }
}