package bbcdabao.componentsbrz.terminalhub.terminalagents.kubernetespodagent;

import java.io.InterruptedIOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import bbcdabao.componentsbrz.websocketbrz.api.AbstractSessionServer;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecWatch;

/**
 * -K8S涓璄XEC鎺ュ彛鍛戒护琛屼細璇�
 * 
 * @author 10080262
 *
 */
public class Session  extends AbstractSessionServer {

	private static final String CLUSTERNAME = "clustername";
    
    @Autowired
    private K8sConnectorSelector k8sConnectorManager;

    /**
     * -鑾峰彇KubernetesClient
     * @return
     * @throws Exception
     */
    public  KubernetesClient getClient(Map<String, String> queryMap) throws Exception {
        return k8sConnectorManager
                .selectorForCluster(queryMap.get(CLUSTERNAME))
                .getActiveClient();
    }
    
    private static final int INT_ZERO = 0;

    private final Logger logger = LoggerFactory.getLogger(K8sExecSession.class);
    
    /**
     * -鍐呴儴浼氳瘽绾跨▼璋冨害浣跨敤
     * -闇�瑕佸仠姝㈡椂涓柇
     * 
     * @author 10080262
     *
     */
    private final class ExecThread extends Thread {
        
        public ExecThread() {
            start();
        }
        
        @Override
        public void run() {
            runExec();
        }
    }

    /**
     * -鏋勯�犲嚱鏁颁紶鍏�
     * -clientGter鑾峰彇k8s瀹㈡埛绔帴鍙�
     * -bufCapacity浼犺緭buffer鍗曚釜澶у皬榛樿1024
     * -bufRecycles浼犺緭鍥炴敹澶у皬瀹炵幇鍙屽惊鐜彁楂樻晥鐜�
     */
    IK8sClientGter clientGter;
    private int bufCapacity;
    private int bufRecycles;
    
    /**
     * -姣忎釜session鍒涘缓鏃秓nAfterConnectionEstablished鍑芥暟鍐呰缃�
     * -nameContainer鍏佽涓虹┖锛屽鏋滄槸绌洪粯璁ょ櫥褰曠涓�涓鍣�
     */
    private String nameSpace = "";
    private String namePod = "";
    private String nameContainer = "";
    
    private Map<String, String> queryMap = null;
    
    /**
     * -浼氳瘽浠嶦XEC璇诲彇缂撳啿鍥炴敹闃熷垪
     * -涓轰簡閬垮厤棰戠箒NEW
     */
    private ConcurrentLinkedQueue<BinaryMessage> msgFree = new ConcurrentLinkedQueue<>();
    
    /**
     * -骞跺彂璁℃暟鍣�
     */
    private final AtomicInteger idxRecycles = new AtomicInteger(INT_ZERO);
    
    private BinaryMessage getBinaryMessage() {
        
        BinaryMessage msg = msgFree.poll();
        if (null != msg) {
            idxRecycles.decrementAndGet();
        }
        else {
            msg = new BinaryMessage(ByteBuffer.allocate(bufCapacity));
            logger.info("session hashcode:{} new buffer : {}", hashCode(), bufCapacity);
        }
        
        return msg;
    }
    
    private void addBinaryMessage(BinaryMessage msg) {
        
        if (idxRecycles.getAndIncrement() < bufRecycles) {
            msgFree.add((BinaryMessage) msg);
        }
        else {
            idxRecycles.decrementAndGet();
        }
    }
    
    ISessionSenderGeter senderGeter = null;
    private ExecThread execThread = null;
    
    /**
     * -缁欐帴鏀跺悜閲岄潰鐏屽叆涓や釜绾跨▼闇�瑕乿olatile
     */
    private volatile PipedOutputStream cmdToExec = null;
    
    /**
     * -璇诲彇鍛戒护琛岃緭鍑烘祦閫氳繃websocket鍙戦��
     * @param reader
     * @throws Exception
     */
    private void doSendProc(PipedInputStream reader, ISessionSender sender) throws Exception {

        BinaryMessage msg = getBinaryMessage();
        ByteBuffer byteBuffer = msg.getPayload();
        byteBuffer.clear();
        byte[] readBuffer = byteBuffer.array();
        int readSize = reader.read(readBuffer, INT_ZERO, readBuffer.length);
        if (INT_ZERO >= readSize) {
            throw new SessionException("doSendProc read lower 0");
        }
        byteBuffer.limit(readSize);
        // info: 鎺ㄥ悜鍙戦�侀槦鍒�
        sender.postMsg(msg);
    }
    
    private void runExecWithClient(KubernetesClient client) {
        
        try (PipedOutputStream cmdToExec = new PipedOutputStream();
                PipedInputStream cmdExecTo = new PipedInputStream();
                ExecWatch execwt = client.pods().inNamespace(nameSpace).withName(namePod).inContainer(nameContainer)
                        .writingInput(cmdToExec).readingOutput(cmdExecTo).withTTY().exec();
                ISessionSender sender = senderGeter.getSessionSender(new ISessionSender.IComplete() {
                    @Override
                    public void onComplete(WebSocketMessage<?> msg, boolean ok, Throwable exception) throws Exception {
                        addBinaryMessage((BinaryMessage) msg);
                    }
                })) {
            
            this.cmdToExec = cmdToExec;
            while (sender.isOpen()) {
                doSendProc(cmdExecTo, sender);
            }
        }
        catch (InterruptedIOException e) {
            logger.info("session hashcode:{} is interrupted", hashCode());
        }
        catch (Exception e) {
            logger.error("session hashcode:{} runExecWithClient Exception:", hashCode(), e);
        }
    }
    
    private void runExec() {
        
        logger.info("session hashcode:{} run...", hashCode());
        
        try (KubernetesClient client = clientGter.getClient(queryMap)) {
            if (ObjectUtils.isEmpty(nameContainer)) {
                List<Container> containers = client.pods().inNamespace(nameSpace).withName(namePod).get().getSpec().getContainers();
                nameContainer = containers.get(INT_ZERO).getName();
            }

            runExecWithClient(client);
        }
        catch (Exception e) {
            logger.error("session hashcode:{} runExec Exception:", hashCode(), e);
        }
        
        logger.info("session hashcode:{} end over", hashCode());
    }
    
    public K8sExecSession(IK8sClientGter clientGter, int bufCapacity, int bufRecycles) {
        this.clientGter = clientGter;
        this.bufCapacity = bufCapacity;
        this.bufRecycles = bufRecycles;
    }
    
    @Override
    public void onTextMessage(TextMessage message) throws Exception {
        if (null == cmdToExec) {
            return;
        }
        cmdToExec.write(message.getPayload().getBytes());
    }

    @Override
    public void onAfterConnectionEstablished(ISessionSenderGeter sessionSenderGeter, Map<String, String> queryMap)
            throws Exception {
        this.queryMap = queryMap;
        nameSpace = queryMap.get("space");
        if (StringUtils.isEmpty(nameSpace)) {
            throw new SessionException("nameSpace not had !");
        }
        namePod = queryMap.get("pod");
        if (StringUtils.isEmpty(namePod)) {
            throw new SessionException("namePod not had !");
        }
        nameContainer = queryMap.get("container");
        senderGeter = sessionSenderGeter;        
        execThread = new ExecThread();
    }

    @Override
    public void onHandleTransportError(Throwable exception) throws Exception {
        logger.info("session hashcode:{} onHandleTransportError", hashCode());
    }

    @Override
    public void onAfterConnectionClosed(CloseStatus closeStatus) throws Exception {
        logger.info("session hashcode:{} onAfterConnectionClosed", hashCode());
        if (null != execThread) {
            execThread.interrupt();
        }
    }
}

