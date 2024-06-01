package bbcdabao.componentsbrz.flowcontrolbrz.service;

/**
 * -第三方并发控制接口，用于实现令牌彤方式限流控制
 * @author bao
 *
 */
public interface ITokencyctl {

	public static class TokenCfg {
		public int addToken;
		public int curToken;
		public int maxToken;
	}

	/**
	 * -设置参数
	 * @param tokenCfg
	 */
	void setTokenCfg(TokenCfg tokenCfg);

	/**
	 * -获取参数
	 * @return
	 */
	TokenCfg getTokenCfg();
}
