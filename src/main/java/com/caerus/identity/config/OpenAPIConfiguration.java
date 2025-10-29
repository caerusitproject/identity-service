package com.caerus.identity.config;

import com.caerus.identity.handlers.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            contact = @Contact(name = "Caerus", email = "kaushik@cearusitconsulting.com"),
            title = "Identity-authentication App",
            description = "Identity-authentication",
            version = "0.0.1-SNAPSHOT"),
    servers = {@Server(description = "Development", url = "http://localhost:8086")})
public class OpenAPIConfiguration {
  @Bean
  public OpenApiCustomizer schemaCustomizer() {
    ResolvedSchema resolvedSchema =
        ModelConverters.getInstance()
            .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class));
    return openApi -> openApi.schema(resolvedSchema.schema.getName(), resolvedSchema.schema);
  }
}
