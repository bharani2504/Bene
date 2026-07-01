package com.example.demo.validator;

import com.example.demo.model.Account;
import com.example.demo.model.Bene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class BeneValidation {

    @Autowired
    private RestTemplate restTemplate;

    private final Properties prop = new Properties();

    public BeneValidation() throws IOException {
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("application.properties")) {
            prop.load(input);
        }
    }

    public void ifscValidation(Bene bene) {
        if (bene.getAccount() != null) {
            for (Account act : bene.getAccount()) {
                if (act.getIFSC() != null) {
                    String url = prop.getProperty("ifsc.url");
                    Map<String, Object> response = restTemplate.postForObject(url, act.getIFSC(), Map.class);

                    String ifsc = String.valueOf(response.getOrDefault("ifsc_code", ""));
                    if (!ifsc.equalsIgnoreCase(act.getIFSC())) {
                        throw new RuntimeException("INVALID IFSC CODE");
                    }
                    String bank = String.valueOf(response.getOrDefault("bank_name", ""));
                }
            }
        }
    }
}