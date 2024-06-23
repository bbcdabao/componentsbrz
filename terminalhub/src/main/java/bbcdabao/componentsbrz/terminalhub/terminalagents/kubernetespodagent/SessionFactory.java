package bbcdabao.componentsbrz.terminalhub.terminalagents.kubernetespodagent;

import java.util.HashMap;
import java.util.Map;

import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;
import bbcdabao.componentsbrz.websocketbrz.api.ISessionFactory;
import bbcdabao.componentsbrz.websocketbrz.api.annotation.SessionFactoryBrz;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;


/**
 * -K8S绘画工厂，里面会保存所有的KubernetesClient链接
 * @author 10080262
 *
 */
@SessionFactoryBrz("k8spodexecfactory")
public class SessionFactory extends Thread implements ISessionFactory {

	private Map<String, KubernetesClient> kubernetesClientMap = new HashMap<>(50);
	
    Config config = new ConfigBuilder()
            .withMasterUrl("https://your-k8s-api-server:6443")
            .withOauthToken("your-oauth-token")
            .withNamespace("default")
            .withTrustCerts(true)
            .build();

    @Override
    public AbstractSessionServer getSession(Map<String, String> queryMap) throws Exception {
    	return null;
        //return new K8sExecSession(clientGter, k8sexecConfig.getBufCapacity(), k8sexecConfig.getBufRecycles());
    }
}

