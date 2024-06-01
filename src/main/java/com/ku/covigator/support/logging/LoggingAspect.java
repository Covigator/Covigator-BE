package com.ku.covigator.support.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(public * com.ku.covigator..*(..)) && !execution(public * com.ku.covigator.support.logging..*(..))")
    private void allComponents() {}

    @Pointcut("execution(public * com.ku.covigator.config..*(..))")
    private void allConfig() {}

    @Before("allComponents() && !allConfig()")
    public void doLog(JoinPoint joinPoint) {
        log.info("[{}]", joinPoint.getSignature().toShortString());
    }

}
