# Messagebrz
This is a springboot component that for sending messages between threads.<br>
It supports delayed message execution.<br>
The core algorithm uses a time-sorted queue to retrieve messages that are due for execution from the front of the queue, and then schedules them for processing.<br>

- __Component Description__
  - Instructions for use
    - Java example code 
    ```java
    /**
     * Define your own message type.
     */
    public class MyMsg extends Message {
        public MyMsg(String dest) {
            super(dest);
        }
    }

    
    /**
     * Define your message handling module and use the @MessageHandler annotation to mark the type.
     * Note that its parameter 'mymodule' is the address to which messages are sent to this module.
     */
    @
    MessageHandler("mymodule")
    public class MyHandler {
        public void procMyMsg() throws Exception {...}
    }

    /**
     * Send messages using the code above.
     */
    void testFunction() {
        MyMsg msg = new MyMsg("mymodule");
        // You can set message handling delay like.
        msg.setDelayTime(10000);
        // And you can send it to 'procMyMsg'
        msg.post();
    }
    ```
