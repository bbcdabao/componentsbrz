# Messagebrz
This is a springboot component that for sending messages between threads.<br>
It supports delayed message execution.<br>
The core algorithm uses a time-sorted queue to retrieve messages that are due for execution from the front of the queue, and then schedules them for processing.<br>

- __Component Description__
  - Instructions for use
    - Java example code 
    ```java
    /**
     * Controller Interface like login
     */
    @RestController
    @RequestMapping("/api")
    public class ApiController {
        @Flowcontrol
        @PostMapping("/login")
        public LoginResponseDto login(@RequestBody LoginDto login) throws Exception {
    	    ......
        }
    }
    ```
