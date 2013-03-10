/*package com.beike.biz.service.trx.aspect;    

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;



*//**   
 * @Title: LoggerAspect.java
 * @Package com.beike.biz.service.trx.aspect
 * @Description: 日志切面
 * @date Jun 29, 2011 6:49:43 PM
 * @author wh.cheng
 * @version v1.0   
 *//*
public class LoggerAspect {
	

    private static Log logger = LogFactory.getLog(LoggerAspect.class);
    
    @Autowired
    private SystemThreadLocalMap threadLocalMap;

    @Autowired
    private ArgsLogger argsLogger;

    @Pointcut("execution (* com.sinosoft.core.log.aspect.TestService.*(..))")
    public void servicePointCut() {
    }

    @Around("servicePointCut()")
    public void serviceAdvice(ProceedingJoinPoint pjp) throws Throwable {
        String operationMethodName = pjp.getSignature().getName();
        argsLogger.debug(pjp.getArgs(),operationMethodName);
        
        recordServiceMetaInfo(pjp);
        try {
            pjp.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("Exception Caught!"); //$NON-NLS-1$
                //异常的时候打印参数，以方便调试
                argsLogger.error(pjp.getArgs(), operationMethodName);
            }
            //抛出原日志，让上层（展现层）处理
            throw e;
        }

        if (logger.isInfoEnabled()) {
            logger.info(getOperationLog()); //$NON-NLS-1$
        }
        
    }

private String getOperationLog() {
        String serviceDesc = (String) threadLocalMap.get(SystemThreadLocalMap.SERVICE_DESCRIPTION);
        String operationDesc = (String) threadLocalMap.get(SystemThreadLocalMap.OPERATION_DESCRIPTION);
        String userID = (String) threadLocalMap.get(SystemThreadLocalMap.USER_ID);
        String userName = (String) threadLocalMap.get(SystemThreadLocalMap.USER_NAME);
        return 
                SystemThreadLocalMap.USER_ID+"=" + userID +
                ","+SystemThreadLocalMap.USER_NAME+"=" + userName +
                ","+SystemThreadLocalMap.SERVICE_DESCRIPTION+"=" + serviceDesc +
                ","+SystemThreadLocalMap.OPERATION_DESCRIPTION+"=" + operationDesc;
	//获取日志

    }

    private void recordServiceMetaInfo(ProceedingJoinPoint pjp) {
        try {
            String serviceDesc = this.getClassAnnotation(pjp);
            String operationDesc = this.getMethodAnnotation(pjp);
            threadLocalMap.put(SystemThreadLocalMap.SERVICE_DESCRIPTION, serviceDesc);
            threadLocalMap.put(SystemThreadLocalMap.OPERATION_DESCRIPTION, operationDesc);
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    *//**
     * To get the defined method's annotation of the target class
     * @param pjp
     * @return
     * @throws ClassNotFoundException 
     *//*
    private String getMethodAnnotation(ProceedingJoinPoint pjp) throws ClassNotFoundException{
        String pjpStr = "";
        String localStr = "";
        String rtStr = "";
        Method[] m = this.getTargetMethods(pjp);

        // To get the method name and arguments information of the target method
        pjpStr += pjp.getSignature().getName();
        int argLen = pjp.getArgs().length;
        for (int i = 0; i < argLen; i++) {
            pjpStr += pjp.getArgs()[i].getClass().getName();
        }

        // check to get the annotation
        for (int i = 0; i < m.length; i++) {
            if (pjp.getArgs().length == m[i].getGenericParameterTypes().length) {
                if (pjp.getSignature().getName().equals(m[i].getName())) {
                    if (pjp.getArgs().length > 0) {
                        localStr = "";
                        localStr += m[i].getName();
                        for (int j = 0; j < m[i].getGenericParameterTypes().length; j++) {
                            localStr += m[i].getGenericParameterTypes()[j].toString().substring(6);
                        }
                        if (pjpStr.equals(localStr)) {
                            Annotation annotation1 = m[i]
                                    .getAnnotation(OperationDescription.class);
                            OperationDescription des = (OperationDescription) annotation1;
                            rtStr = des.funtion();
                            localStr = "";
                        }
                    } else {
                        Annotation annotation1 = m[i]
                                .getAnnotation(OperationDescription.class);
                        OperationDescription des = (OperationDescription) annotation1;
                        rtStr = des.funtion();
                    }
                }
            }
        }

        return rtStr;
    }
    
    *//**
     * To get the annotation of the target class
     * @param pjp
     * @return
     * @throws ClassNotFoundException 
     *//*
    private String getClassAnnotation(ProceedingJoinPoint pjp) throws ClassNotFoundException{
        Class cls = this.getTargetClass(pjp);
        Annotation annotation = cls.getAnnotation(ServiceDescription.class);
        ServiceDescription d = (ServiceDescription) annotation;
        return d.value();
    }
    
    *//**
     * To get the target class 
     * @param pjp
     * @return
     * @throws ClassNotFoundException 
     *//*
    private Class getTargetClass(ProceedingJoinPoint pjp) throws ClassNotFoundException{
        String classname = pjp.getTarget().getClass().toString().substring(6);
        Class cls = Class.forName(classname);
        return cls;
    }
    
    *//**
     * To get the methods of the target class
     * @param pjp
     * @return
     * @throws ClassNotFoundException 
     *//*
    private Method[] getTargetMethods(ProceedingJoinPoint pjp) throws ClassNotFoundException{
        Class cls = this.getTargetClass(pjp);
        Method[] methods = cls.getMethods();
        return methods;
    }
//该类定义了切入点和对切面的处理，在其中你看到了OperationDescription个注释和ServiceDescription注释：这两个是自定义的注释:
@Target(ElementType.METHOD)   
@Retention(RetentionPolicy.RUNTIME) 

public @interface OperationDescription {
    String funtion();
}

@Target(ElementType.TYPE)   
@Retention(RetentionPolicy.RUNTIME) 

public @interface ServiceDescription {
    String value();
}



//下面这个ArgsLogger 类是记录日志的输出
public class ArgsLogger {
    *//**
     * Logger for this class
     *//*
    private static final Log logger = LogFactory.getLog(ArgsLogger.class);

    
    public void debug(Object[] args, String operationMethodName) {
        
        if (logger.isDebugEnabled()) {
            logger.debug(getArgsInfo(args, operationMethodName)); 
        }
    }
    
    public void error(Object[] args, String operationMethodName) {
        if (logger.isErrorEnabled()) {
            logger.error(getArgsInfo(args, operationMethodName)); 
        }
        
    }

    private StringBuffer getArgsInfo(Object[] args, String operationMethodName) {
        StringBuffer argsBuffer = new StringBuffer();
        argsBuffer.append("\nprinting the Args of " +operationMethodName+
                "(..):\n");
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            argsBuffer.append("args["+i+"]='").append(arg).append("'\n");
        }
        return argsBuffer;
    }



}
 */