package bbcdabao.componentsbrz.terminalhub.terminalagents.kubernetespodagent;

import java.io.InterruptedIOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import com.zte.itp.websocketspbrz.api.AbstractSessionServer;
import com.zte.itp.websocketspbrz.api.ISessionSender;
import com.zte.itp.websocketspbrz.api.ISessionSenderGeter;
import com.zte.itp.k8ssessionbrz.exception.SessionException;
import com.zte.itp.k8ssessionbrz.k8sexec.IK8sClientGter;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecWatch;

/**
 * -K8S中EXEC接口命令行会话
 * 
 * @author 10080262
 *
 */
public class K8sExecSession  extends AbstractSessionServer {
    
    private static final int INT_ZERO = 0;

    private final Logger logger = LoggerFactory.getLogger(K8sExecSession.class);
    
    /**
     * -内部会话线程调度使用
     * -需要停止时中断
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
     * -构造函数传入
     * -clientGter获取k8s客户端接口
     * -bufCapacity传输buffer单个大小默认1024
     * -bufRecycles传输回收大小实现双循环提高效率
     */
    IK8sClientGter clientGter;
    private int bufCapacity;
    private int bufRecycles;
    
    /**
     * -每个session创建时onAfterConnectionEstablished函数内设置
     * -nameContainer允许为空，如果是空默认登录第一个容器
     */
    private String nameSpace = "";
    private String namePod = "";
    private String nameContainer = "";
    
    private Map<String, String> queryMap = null;
    
    /**
     * -会话从EXEC读取缓冲回收队列
     * -为了避免频繁NEW
     */
    private ConcurrentLinkedQueue<BinaryMessage> msgFree = new ConcurrentLinkedQueue<>();
    
    /**
     * -并发计数器
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
     * -给接收向里面灌入两个线程需要volatile
     */
    private volatile PipedOutputStream cmdToExec = null;
    
    /**
     * -读取命令行输出流通过websocket发送
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
        // info: 推向发送队列
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
            
            // info: 如果没有容器名称默认取第一个容器
            if (StringUtils.isEmpty(nameContainer)) {
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

