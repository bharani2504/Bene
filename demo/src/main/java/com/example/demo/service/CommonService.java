package com.example.demo.service;

import com.example.demo.annotation.AuditLog;
import com.example.demo.dto.ServiceRequest;
import com.example.demo.dto.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Service
public class CommonService {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(CommonService.class);
    private final Properties prop = new Properties();
    public CommonService() throws IOException {
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("application.properties")) {
            prop.load(input);
        }
    }


    @AuditLog(serviceName ="ifsc_detail",operation = "get-Ifsc")
    public ServiceResponse getIfsc(ServiceRequest request){
        ServiceResponse response =new ServiceResponse();
        if(request!=null){
            String url = prop.getProperty("ifsc.url");
            Map<String, Object> resp = restTemplate.postForObject(url,request, Map.class);
            response.setData(resp);
            response.setStatus("SUCCESS");
            response.setResponseString(resp.toString());
            log.info("service response =>{}",response);

        }

        return response;
    }

}
