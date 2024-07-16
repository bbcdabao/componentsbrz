package bbcdabao.componentsbrz.terminalhub.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bbcdabao.componentsbrz.terminalhub.model.LoginDto;
import bbcdabao.componentsbrz.terminalhub.model.LoginResponseDto;

/**
 * Controller Interface like login
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto login) throws Exception {
    	LoginResponseDto loginResponseDto = new LoginResponseDto();
    	loginResponseDto.setToken("success");
    	return loginResponseDto;
	}
}
