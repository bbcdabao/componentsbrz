package baobao.componentsbrz.flowcontrolbrz.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * -����ע��
 * @author bao
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
@Inherited
public @interface Flowcontrol {
	int value() default 1;
}
