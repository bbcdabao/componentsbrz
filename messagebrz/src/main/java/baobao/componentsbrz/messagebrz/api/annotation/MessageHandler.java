package baobao.componentsbrz.messagebrz.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * -用于类上，标注处理模块
 * @author bao
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
@Inherited
@Component
@Scope("singleton")
public @interface MessageHandler {
	String value() default "defMessageHandler";
}
