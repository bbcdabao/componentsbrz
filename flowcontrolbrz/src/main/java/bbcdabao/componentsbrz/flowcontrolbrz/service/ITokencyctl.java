package bbcdabao.componentsbrz.flowcontrolbrz.service;

/**
 * -�������������ƽӿڣ�����ʵ������ͮ��ʽ��������
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
	 * -���ò���
	 * @param tokenCfg
	 */
	void setTokenCfg(TokenCfg tokenCfg);

	/**
	 * -��ȡ����
	 * @return
	 */
	TokenCfg getTokenCfg();
}
