package com.example.demo.audit;
import com.example.demo.annotation.AuditLog;
import com.example.demo.dto.Audit;
import com.example.demo.dto.ServiceRequest;
import com.example.demo.dto.ServiceResponse;
import com.example.demo.service.BeneAuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "spring.audit.enabled", havingValue = "true", matchIfMissing = true)
public class BeneAudit {


    public BeneAudit(BeneAuditService beneAuditService){
        this.beneAuditService=beneAuditService;
    }
    private static BeneAuditService beneAuditService;
    private static final Logger log = LoggerFactory.getLogger(BeneAudit.class);

    @Around("@annotation(auditLog)")
    public Object around (ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {

        Object response= null;
        String serviceName=auditLog.serviceName();
        String operation= auditLog.operation();

        log.info("Bene audit",serviceName,operation);
        Object[] request =joinPoint.getArgs();

        ServiceRequest serviceRequest=null;
        if(request!=null && request.length>0 && request[0] instanceof ServiceRequest){
            serviceRequest = (ServiceRequest) request[0];
        }

        response= joinPoint.proceed();
        try {
            if (serviceRequest != null) {

                if(response!=null && response instanceof ServiceResponse serviceResponse){
                     Audit audit=new Audit();
                     extracted(audit,serviceRequest,serviceResponse,serviceName,operation);
                     log.info("audit values has been set");
                     beneAuditService.createLog(audit);
                }
            }
        } catch (Throwable ex) {
            throw ex;
        }
     return response;
    }

    public static void extracted(Audit audit,ServiceRequest request,ServiceResponse response,String servicename,String operation){

        audit.setBeneNickName(request.getBeneNickname());
        audit.setOperationName(operation);
        audit.setServiceName(servicename);
        audit.setStatus(response.getStatus());
        audit.setContext(request.getContext().toString());
        audit.setData(response.getData().toString());
        audit.setResponseString(response.getResponseString());
    }
}
