# Flowcontrollbrz
This is a springboot component that a simple flow control component.<br>
By adopting the token bucket algorithm, you can achieve traffic control for Controller layer interface calls through simple configuration and annotations.<br>

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
