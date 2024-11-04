package com.angularexercise.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SpaceshipLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(SpaceshipLoggingAspect.class);

    @Before("execution(* com.angularexercise.service.SpaceshipService.getSpaceshipById(..)) && args(id)")
    public void logBeforeGetSpaceshipById(Long id) {
        if (id < 0) {
            logger.info("Retrieving spaceship with negative ID: {}", id);
        }
    }
}