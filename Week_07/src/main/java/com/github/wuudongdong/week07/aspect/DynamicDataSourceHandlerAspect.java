package com.github.wuudongdong.week07.aspect;

import com.github.wuudongdong.week07.annotation.ReadOnly;
import com.github.wuudongdong.week07.configuration.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Order(1)
@Aspect
@Component
public class DynamicDataSourceHandlerAspect {

    @Pointcut("@annotation(com.github.wuudongdong.week07.annotation.ReadOnly)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ReadOnly annotationClass = method.getAnnotation(ReadOnly.class);
        if (annotationClass == null) {
            annotationClass = joinPoint.getTarget().getClass().getAnnotation(ReadOnly.class);
            if (annotationClass == null){
                return;
            }
        }

        // 获取注解上的数据源的值的信息（这里最好还判断一下dataSourceId是否是有效的  若是无效的就用warn提醒  此处我就不处理了）
        String dataSourceId = annotationClass.dataSourceId();

        // 此处切换数据源
        DynamicDataSource.LOOKUP_KEY_HOLDER.set(dataSourceId);
        log.info("AOP动态切换数据源，className" + joinPoint.getTarget().getClass().getName() + "methodName" + method.getName() + ";dataSourceId:" + dataSourceId == "" ? "默认数据源" : dataSourceId);
    }


    @After("pointcut()")
    public void after(JoinPoint point) {
    }
}
