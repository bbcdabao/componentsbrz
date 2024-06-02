package bbcdabao.componentsbrz.websocketbrz.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * -≈‰÷√¿‡
 * @author bao
 *
 */
@Configuration
@EnableWebSocket
public class Config implements WebSocketConfigurer {
	private final Logger logger = LoggerFactory.getLogger(Config.class);

	@Value("${wscfg.paths:bbadabao}")
	private String paths;

	@Value("${wscfg.allowedOrigins:*}")
	private String allowedOrigins;

	@Value("${wscfg.isPartialMsg:true}")	
	private String isPartialMsg;

	@Value("${wscfg.senderCapacity:128}")	
	private int senderCapacity;

	@Value("${wscfg.senderCapacity:2000}")
	private long timeCyc;

	@Value("${wscfg.stepOut:30}")
	private long stepOut;

	@Value("${wscfg.maxSessions:20}")
	private int maxSessions;

	@Value("${wscfg.taskSchedulerSize:10}")
	private int taskSchedulerSize;

	@Value("${wscfg.pingCyc:10000}")
	private long pingCyc;

	@Autowired
	//private 
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	}
}
