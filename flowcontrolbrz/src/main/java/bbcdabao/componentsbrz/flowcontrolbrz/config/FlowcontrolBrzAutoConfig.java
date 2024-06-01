package bbcdabao.componentsbrz.flowcontrolbrz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import bbcdabao.componentsbrz.flowcontrolbrz.service.ITokencyctl;
import bbcdabao.componentsbrz.flowcontrolbrz.service.ITokencyctl.TokenCfg;
import bbcdabao.componentsbrz.flowcontrolbrz.service.impl.TokencyctlImpl;

/**
 * -≈‰÷√≤ø∑÷
 * @author bao
 *
 */
@Configuration
public class FlowcontrolBrzAutoConfig implements WebMvcConfigurer {

	private TokencyctlImpl tokencyctlImpl = new TokencyctlImpl();

	@Value("${flowcontrol-brz.tps:1}")
	private int tps = 1;
	@Value("${flowcontrol-brz.max-token-bucket:1}")
	private int maxTokenBucket = 1;

	@Autowired
	private Environment env;

	@Bean(name="tokencyctl")
	public ITokencyctl getTokencyctl() {
		return tokencyctlImpl;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String path = env.getProperty("flowcontrol-brz.path", "/**");
		String[] paths = path.split(",");
		TokenCfg tokenCfg = new TokenCfg();
		tokenCfg.addToken = tps;
		tokenCfg.curToken = maxTokenBucket;
		tokenCfg.maxToken = maxTokenBucket;
		tokencyctlImpl.setTokenCfg(tokenCfg);
		registry.addInterceptor(tokencyctlImpl).addPathPatterns(paths);
	}
}
