/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
