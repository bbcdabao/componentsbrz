# Websocketbrz
This is a springboot component that makes websockets easier to use. <br>
Provides rich annotations to support development, and provides rich support for websocket sending buffer queues. Websocket business development can be completed with only some configuration and annotations.<br><br>
__Here are two more interesting examples, you can see [terminalhub](../terminalhub) and [chatroom](../chatroom) in this project.__<br><br>
- __Component Description__
  - Instructions for use
    - Instructions for config
    ```yml
    # Here is an example YAML configuration
    wscfg:
    # Configure websocket path, Supports configuring multiple paths "," intervals.
    # like: ws://mypath ...
      paths: mypath
    # Origin config
      allowedOrigins: *
    # Partinal websocket message support
      isPartialMsg: true
    # The detection period for clearing useless WebSocketSession responses, in milliseconds
      timeCyc: 2000
    # This config for the session time out
    # Note that the unit is "timeCyc". If there is no data flow in a certain period
    # The supermarket will be considered
      stepOut: 30
    # The max web socekt sessions config
      maxSessions: 100
    # The period of actively sending websocket-ping to the client, in milliseconds
    # 0 means turning off and not sending ping
      pingCyc: 10000
    # The threadpool config for working
      threadPool:
        corePoolSize: 20
        maxPoolSize: 40
        queueCapacity: 50
        keepAliveSeconds: 10
        threadNamePrefix: wsc-sendthread
    ```
    - Instructions for coding<br>
    __The component interface you can use is at package:__<br>
    **bbcdabao.componentsbrz.websocketbrz.api./* **<br>
    It includes 4 interfaces and 3 annotations:<br>
      > 1. AbstractSessionServer:
      ```java
      /**
       * Session framework abstract base class
       * It is an abstract class used to initialize access to websocket sessions and receive messages. 
       * When there is a session come in, the subclass will be created. 
       * The ISessionFactory factory subclass is responsible for creating.
       */
      public abstract class AbstractSessionServer {
      }
      ```

    - Java websocket factory example code 
    ```java
	@SessionFactoryBrz("chatroom")
	public class SessionFactory implements ISessionFactory {@
	    Override
	    public AbstractSessionServer getSession(Map < String, String > queryMap) throws Exception {
	        String name = queryMap.get("name");
	        if (ObjectUtils.isEmpty(name)) {
	            throw new Exception("no name!!!");
	        }
	        return new Session(name);
	    }
	}
    ```
    - Java websocket session example code
    ```java
	public class Session extends AbstractSessionServer {
	    public Session(String name) throws Exception {
	        this.name = name;
	    }
	
	    private final String name;
	    /**
	     * Writing to "sendChanl" will send the websocket message
	     */
	    @SessionSenderQue
	    private BlockingQueue < TextMessage > sendChanl = new LinkedBlockingQueue < > ();
	
	    @Override
	    public void onTextMessage(TextMessage message) throws Exception {}
	
	    @Override
	    public void onAfterConnectionEstablished(WebSocketSession session, IRegGetMsgForSend regGetMsgForSend) throws Exception {}
	
	    @Override
	    public void onHandleTransportError(Throwable exception) throws Exception {}
	
	    /**
	     * Close the open resource init the onAfterConnectionEstablished function
	     */
	    @Override
	    public void onAfterConnectionClosed(CloseStatus closeStatus) throws Exception {}
	}
    ```
    - Please see the following two examples to complete the code
      - [chatroom](../chatroom)
      - [terminalhub](../terminalhub)
