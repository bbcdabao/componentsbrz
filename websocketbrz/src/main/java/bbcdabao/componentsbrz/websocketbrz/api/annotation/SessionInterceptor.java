package bbcdabao.componentsbrz.websocketbrz.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * -�Ự����������������Ҫʵ�ּ�Ȩ�������ƹ���
 * -���������������ȼ�
 * -ע�⣺0��ߣ�1��2��3��4 ...���ν���
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
