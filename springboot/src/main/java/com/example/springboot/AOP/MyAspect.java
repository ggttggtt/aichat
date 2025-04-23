package com.example.springboot.AOP;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @ClassName: MyAspect
 * @Description: TODO
 * @Author: 代刘斌
 * @Date: 2023/10/9 - 10 - 09 - 16:04
 * @version: 1.0
 **/

@Component("myAspect")
@Aspect//标注该类为切面类
public class MyAspect {

    //切点
    @Pointcut("execution( * com.example.springboot.service.UserDao.*(..))")
    public void pointcut(){}

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){ //使用JoinPoint接口实例作为参数获得目标对象的类名和方法名
        System.out.println("前置增强....");
        System.out.println("目标类：" + joinPoint.getTarget());
        System.out.println("被织入增强处理的目标方法为：" + joinPoint.getSignature());

        Object[] args = joinPoint.getArgs();
        // 判断是否允许访问
        if (!args[0].toString().equals("true")) {
            System.out.println("访问被拒绝");
            // 这里可以抛出异常或者做其他处理
        }else {
            System.out.println("访问允许");
        }
    }

    @AfterReturning(pointcut = "pointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result){
        System.out.println("后置增强....");
        System.out.println("被织入增强处理的目标方法为：" + joinPoint.getSignature());
        System.out.println("方法返回值为：" + result);

        //在这里添加日志记录的逻辑，比如可以将日志写入文件或数据库
        System.out.println("记录日志：方法：" + joinPoint.getSignature() + "执行成功，返回值为：" + result);
    }

    //ProceedingJoinPoint：正在执行的连接点 ==> 切点
    /**
     * 环绕通知
     * ProceedingJoinPoint是JoinPoint子接口，表示可以执行目标方法
     * 1.必须是Object类型的返回值
     * 2.必须接收一个参数，类型为ProceedingJoinPoint
     * 3.必须throws Throwable
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        System.out.println("环绕前增强....");
        //调用目标方法
        Object proceed = point.proceed();//切点方法
        System.out.println("环绕后增强....");
        return proceed;
    }


    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("异常抛出增强....");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("最终增强....");
    }
}
