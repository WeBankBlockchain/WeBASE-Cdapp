/*
 * Copyright 2014-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.certapp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ListVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableOpenApi
@EnableWebMvc
public class SwaggerConfig {

    @Value("${swagger.enabled}")
    private Boolean enabled;

    /**
     * 构建swagger对象.
     * 
     * @return
     */
    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2).enable(enabled).apiInfo(apiInfo())
                .groupName("default").select()
                .apis(RequestHandlerSelectors.basePackage("com.certapp"))
                .paths(PathSelectors.any()).build();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ApiInfo apiInfo() {
        List<VendorExtension> extensions = new ArrayList<VendorExtension>();
        VendorExtension vendorExtension = new ListVendorExtension("base-url", null);
        extensions.add(vendorExtension);

        return new ApiInfoBuilder().title("DCSS API").termsOfServiceUrl(null).license(null)
                .version(null).extensions(extensions).build();
    }

    /**
     * swagger ui界面配置.
     * 
     * @return
     */
    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().deepLinking(true).displayOperationId(false)
                // 设置Models展示
                .defaultModelsExpandDepth(-1).defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE).displayRequestDuration(false)
                // 自动展开接口列表
                .docExpansion(DocExpansion.LIST).filter(false).maxDisplayedTags(null)
                // OperationsSorter.ALPHA – 按路径按字母顺序对API端点进行排序
                // OperationsSorter.METHOD – 按方法按字母顺序对API端点进行排序
                // .operationsSorter(OperationsSorter.METHOD)
                .showExtensions(false).tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null).build();
    }
}
