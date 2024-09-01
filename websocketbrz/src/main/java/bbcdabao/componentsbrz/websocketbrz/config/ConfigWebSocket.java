package bbcdabao.componentsbrz.websocketbrz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import bbcdabao.componentsbrz.websocketbrz.impl.BrzHandshakeInterceptor;
import bbcdabao.componentsbrz.websocketbrz.impl.BrzWebSocketServer;

@Configuration
@EnableWebSocket
public class ConfigWebSocket implements WebSocketConfigurer {

	@Value("${wscfg.paths:bbcdabao}")
	private String paths;

	@Value("${wscfg.allowedOrigins:*}")
	private String allowedOrigins;

	@Autowired
	private BrzWebSocketServer brzWebSocketServer;

	@Autowired
	private BrzHandshakeInterceptor brzHandshakeInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry
		.addHandler(brzWebSocketServer, paths.split(","))
		.addInterceptors(brzHandshakeInterceptor)
		.setAllowedOrigins(allowedOrigins.split(","));
	}
}
