package bbcdabao.componentsbrz.websocketbrz.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * -¹¤³Ì×¢½â
 * @author bao
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
@Component
@Scope("singleton")
public @interface SessionFactoryBrz {
	String value() default "wsfactory";
}
