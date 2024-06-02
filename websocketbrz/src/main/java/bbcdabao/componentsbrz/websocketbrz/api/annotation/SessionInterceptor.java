package bbcdabao.componentsbrz.websocketbrz.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * -会话接入拦截器用于需要实现鉴权或者类似功能
 * -参数是拦截器优先级
 * -注意：0最高，1，2，3，4 ...依次降低
 * @author bao
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
@Component
@Scope("singleton")
public @interface SessionInterceptor {
	String priority() default "0";
}
