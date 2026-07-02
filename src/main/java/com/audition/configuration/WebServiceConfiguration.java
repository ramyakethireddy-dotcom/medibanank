package com.audition.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures web-layer beans, outbound HTTP support, and MVC interceptors.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebServiceConfiguration implements WebMvcConfigurer {

    private final PostsQueryParamValidationInterceptor postsQueryParamValidationInterceptor;
    private final ResponseHeaderInjector responseHeaderInjector;

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
    private static final int TIMEOUT_MS = (int) java.time.Duration.ofSeconds(10).toMillis();

    /**
     * Creates the shared Jackson object mapper used across the application.
     *
     * @return the configured object mapper
     */
    @Bean
    public ObjectMapper objectMapper() {
            return com.fasterxml.jackson.databind.json.JsonMapper.builder()
                .defaultDateFormat(new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.ROOT))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .serializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .addModule(new JavaTimeModule())
                .build();

    }

    /**
     * Creates the shared {@link RestTemplate} used for outbound HTTP requests.
     *
     * @param objectMapper the object mapper to use for JSON conversion
     * @return the configured rest template
     */
    @Bean
    public RestTemplate restTemplate(final ObjectMapper objectMapper) {
        final RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(createClientFactory()));

        // Add logging interceptor
        restTemplate.getInterceptors().add(createLoggingInterceptor());

        // Use object mapper for HttpMessageConverter
        restTemplate.getMessageConverters().forEach(converter -> {
            if (converter instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) {
                ((org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) converter).setObjectMapper(objectMapper);
            }
        });

        return restTemplate;
    }

    /**
     * Creates the base request factory used by the shared rest template.
     *
     * @return the configured request factory
     */
    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        requestFactory.setConnectTimeout(TIMEOUT_MS); // 10 seconds
        requestFactory.setReadTimeout(TIMEOUT_MS);    // 10 seconds
        return requestFactory;
    }

    /**
     * Creates an interceptor that logs outbound HTTP requests and errors.
     *
     * @return the configured client request interceptor
     */
    private ClientHttpRequestInterceptor createLoggingInterceptor() {
        return (request, body, execution) -> {
            if (log.isDebugEnabled()) {
                log.debug("Outgoing request: {} {}", request.getMethod(), request.getURI());
            }
            try {
                return execution.execute(request, body);
            } catch (IOException e) {
                log.error("Error executing HTTP request to {}", request.getURI(), e);
                throw e;
            }
        };
    }

    /**
     * Registers the inbound MVC interceptors used by the API.
     *
     * @param registry the interceptor registry to configure
     */
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(responseHeaderInjector);
        registry.addInterceptor(postsQueryParamValidationInterceptor).addPathPatterns("/posts");
    }
}
