/**
 * Copyright 2014-2020  the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.certapp.common.config;

import com.webank.wbbc.dappsdk.WBBCClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class BeanConfig {

  @Value("${dappsdk.appid}")
  private String appid;

  @Value("${dappsdk.appKey}")
  private String appKey;

  @Bean
  public WBBCClient getClient() {
    log.info("now init WBBCClient for appid appKey:{}|{}", appid, appKey);
    WBBCClient client = new WBBCClient(appid, appKey);
    client.init();
    return client;
  }
}
