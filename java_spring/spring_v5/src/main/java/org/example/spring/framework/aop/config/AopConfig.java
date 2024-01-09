package org.example.spring.framework.aop.config;

import lombok.Data;

@Data
public class AopConfig {
    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
