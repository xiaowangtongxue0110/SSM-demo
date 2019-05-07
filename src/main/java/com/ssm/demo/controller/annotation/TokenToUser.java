


package com.ssm.demo.controller.annotation;

import java.lang.annotation.*;
//Target注解决定TokenTuUser注解可以加在哪些成分上，如加在类身上，或者属性身上，或者方法身上等成分  这里是加在属性身上
@Target({ElementType.PARAMETER})
//Retention注解决定TokenTuUser注解的生命周期  这里指的是生命周期从程序执行开始一直存在
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenToUser {
    /**
     * 当前用户在request中的名字
     *
     * @return
     */
    String value() default "user";
}
