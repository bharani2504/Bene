package com.example.demo.validator;

import com.example.demo.exception.BeneficiaryException;
import com.example.demo.model.Account;
import com.example.demo.model.Bene;
import com.example.demo.model.ServiceRequest;
import com.example.demo.model.ServiceResponse;
import com.example.demo.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Component
public class BeneValidation {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonService commonService;

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

                       ServiceResponse response=commonService.getIfsc(serviceRequest);
                       Map<String,Object>data=response.getData();
                       String ifsc = String.valueOf(data.getOrDefault("ifsc_code", ""));
                       if (!ifsc.equalsIgnoreCase(act.getIFSC())) {
                           throw new BeneficiaryException("INVALID IFSC CODE");
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
}