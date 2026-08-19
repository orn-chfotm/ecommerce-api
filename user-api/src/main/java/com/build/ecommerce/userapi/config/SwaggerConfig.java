package com.build.ecommerce.userapi.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI eCommerceAppOpenApi() {
        return new OpenAPI()
            .info(new Info()
                    .title("E-commerce Toy API 문서")
                    .description("E-commerce Toy API document")
                    .version("v1")
            )
            .externalDocs(new ExternalDocumentation()
                    .description("프로젝트 README")
                    .url("")
            );
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("1. User API")
                .pathsToMatch("/v1/user/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.info(new Info()
                                .title("User API")
                                .description("API documents related to User")
                                .version("1.0")
                        )
                )
            .build();
    }

    @Bean
    public GroupedOpenApi AddressApi() {
        return GroupedOpenApi.builder()
                .group("2. Address API")
                .pathsToMatch("/v1/address/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.info(new Info()
                                .title("Address API")
                                .description("API documents related to Address")
                                .version("1.0")
                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("3. Product API")
                .pathsToMatch("/v1/product/**")
                .addOpenApiCustomizer(openApi ->
                    openApi.info(new Info()
                        .title("Product API")
                        .description("API documents related to Prodcut")
                        .version("1.0")
                    )
                )
            .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("4. Order API")
                .pathsToMatch("/v1/order/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.info(new Info()
                                .title("Order API")
                                .description("API documents related to Order")
                                .version("1.0")
                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi cartApi() {
        return GroupedOpenApi.builder()
                .group("5. Cart API")
                .pathsToMatch("/v1/cart/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.info(new Info()
                                .title("Cart API")
                                .description("API documents related to Cart")
                                .version("1.0")
                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi wishApi() {
        return GroupedOpenApi.builder()
                .group("6. Wish API")
                .pathsToMatch("/v1/wish/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.info(new Info()
                                .title("Wish API")
                                .description("API documents related to Product Wish")
                                .version("1.0")
                        )
                )
                .build();
    }
}
