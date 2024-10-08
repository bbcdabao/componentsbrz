# Flowcontrollbrz
This is a springboot component that a simple flow control component.<br>
By adopting the token bucket algorithm, you can achieve traffic control for Controller layer interface calls through simple configuration and annotations.<br>

  - __Instructions for use__
    - Instructions for config
    ```yml
    # Here is an example YAML configuration
    flowcontrol-brz:
      # Number of tokens generated per second, which is TPS (Transactions Per Second).
      tps: 10
      # Token bucket capacity
      max-token-bucket: 50
    ```
    - Instructions for coding
    Only one annotation is used on the methods of the Controller layer:<br>
    ```java
    /*
     * Used on api for flowcontrol
     */
    @Retention(RUNTIME)
    @Target(METHOD)
    @Inherited
    public @interface Flowcontrol {
	    int value() default 1;
    }
    ```
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
