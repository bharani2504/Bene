package com.example.bene.util;

import com.example.bene.security.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ServiceUtil {

    @Autowired
    private static Jwt jwt;

    public static HttpServletRequest getServletRequest() {
        HttpServletRequest request = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes attributes) {
            request = attributes.getRequest();
        }
        return request;
    }

    public static  String getUserCrn(Object req){

        if(req instanceof HttpServletRequest request){
            String header =request.getHeader("Authorization");
            String token = header.substring(7);
            String userCrn = jwt.extractUserCrn(token);
            return userCrn;
        }

        return null;
    }

}
