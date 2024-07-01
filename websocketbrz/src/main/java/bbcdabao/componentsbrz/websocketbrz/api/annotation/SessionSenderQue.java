package bbcdabao.componentsbrz.websocketbrz.api.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used on like:
 *  
 * ...
 * @SessionSenderQue
 * BlockingQueue<WebSocketMessage<?>> msgList
 * ... 
 * 
 * And msgList can add WebSocketMessage then will be sent
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface SessionSenderQue {
}
