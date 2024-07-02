package bbcdabao.componentsbrz.flowcontrolbrz.service;

/**
 * Tokencyctl
 */
public interface ITokencyctl {

	public static class TokenCfg {
		public int addToken;
		public int curToken;
		public int maxToken;
	}

	void setTokenCfg(TokenCfg tokenCfg);

	TokenCfg getTokenCfg();
}
