package com.bo.msgpush.config;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @notes 日志切面类
 * 
 * @author wangboc
 * 
 * @version 2018年4月21日 下午1:30:43
 */
//@Aspect
//@Component
//@Order(1)
public class AspectLog {

	private Logger log = LoggerFactory.getLogger(AspectLog.class);

	/**
	 * 声明一个切点 里面是 execution表达式
	 */
	@Pointcut("execution(public * com.xinwei.*.service..*.*(..))||execution(public * com.xinwei.msgpush.controller.*.*(..))")
	// @Pointcut("execution(public * com.xinwei.*.controller..*.*(..))")
	public void mylogPoint() {
	}

	/**
	 * 请求前打印内容
	 * 
	 * @param joinPoint
	 */
	@Before(value = "mylogPoint()")
	public void methodBefore(JoinPoint joinPoint) {
		// 同名logger只会创建一次(内部单例)
		log = LoggerFactory.getLogger(joinPoint.getTarget().getClass().getName());
		log.info("Before " + joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + " , Args:" + Arrays.toString(joinPoint.getArgs()));
	}

	/**
	 * 在方法执行完结后打印返回内容
	 * 
	 * @param joinPoint
	 * @param resp
	 */
	@AfterReturning(returning = "resp", pointcut = "mylogPoint()")
	public void methodAfterReturing(JoinPoint joinPoint, Object resp) {
		log = LoggerFactory.getLogger(joinPoint.getTarget().getClass().getName());
		log.info("After " + joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + ", Response:" + resp);
	}

}