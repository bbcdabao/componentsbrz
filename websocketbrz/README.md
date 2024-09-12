# Websocketbrz
This is a springboot component that makes websockets easier to use. <br>
Provides rich annotations to support development, and provides rich support for websocket sending buffer queues. Websocket business development can be completed with only some configuration and annotations.<br><br>
__Here are two more interesting examples, you can see "terminalhub" and "chatroom" in this project.__<br><br>
- __Component Description__
  - Schematic diagram
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
    __The component interface you can use is at page:__<br>
    **bbcdabao.componentsbrz.websocketbrz.api**<br>
    It includes 4 interfaces and 3 annotations:<br>
      - 1. AbstractSessionServer:
It is an abstract class used to initialize access to websocket sessions and receive messages. When there is a reply access, its subclass will be created. The ISessionFactory factory subclass is responsible for creating.
      - 2. IGetMsgForSend:
Its subclass forms a websocket sending module. There will be independent thread scheduling to obtain websocket messages and then send them. You can implement its interface, return a message, and then the component will schedule and send asynchronously. This is just one of the ways to send websocket messages.
      - 3. IRegGetMsgForSend:
It calls back regGetMsgForSend in the "public void onAfterConnectionEstablished(WebSocketSession session, IRegGetMsgForSend regGetMsgForSend)" interface and is used to register the "IGetMsgForSend" sending module.
      - 4. ISessionFactory:
It is an interface for creating websocket session factories.
<br>
      - 5. annotation "SessionFactoryBrz":
      - 6. annotation "SessionInterceptor":
      - 7. annotation "SessionInterceptor":
  
    ```java

    ```
