package bbcdabao.componentsbrz.terminalhub.terminalagents.kubernetespodagent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.zte.itp.websocketspbrz.annotation.SessionFactorySpBrz;
import com.zte.itp.websocketspbrz.api.AbstractSessionServer;
import com.zte.itp.websocketspbrz.api.ISessionFactory;
import com.zte.itp.k8ssessionbrz.config.K8sexecConfig;
import com.zte.itp.k8ssessionbrz.k8sexec.IK8sClientGter;

/**
 * -k8sexec工厂
 * @author 10080262
 *
 */
@SessionFactorySpBrz("k8sexecfactory")
public class K8sExecSessionFactory implements ISessionFactory {
    
    @Autowired
    private K8sexecConfig k8sexecConfig;
    
    @Autowired
    @Qualifier("k8sClientGter")
    private IK8sClientGter clientGter;

    @Override
    public AbstractSessionServer getSession() throws Exception {
        return new K8sExecSession(clientGter, k8sexecConfig.getBufCapacity(), k8sexecConfig.getBufRecycles());
    }
}

