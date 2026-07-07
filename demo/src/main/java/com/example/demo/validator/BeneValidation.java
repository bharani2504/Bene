package com.example.demo.validator;

import com.example.demo.exception.BeneficiaryException;
import com.example.demo.model.Account;
import com.example.demo.model.Bene;
import com.example.demo.model.ServiceRequest;
import com.example.demo.model.ServiceResponse;
import com.example.demo.repo.benerepo;
import com.example.demo.service.CommonService;
import com.example.demo.service.beneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class BeneValidation {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonService commonService;

    @Autowired
    private benerepo benerepo ;


    public void submitRequestValidation (Bene bene) throws SQLException {

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
}