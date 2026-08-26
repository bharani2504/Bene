package com.example.bene.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

@Configuration
@ConditionalOnProperty(name = "spring.wiremock.enabled", havingValue = "true")
public class WireMockConfig {

    private static final Logger LOG = LoggerFactory.getLogger(WireMockConfig.class);
    private static final int WIREMOCK_PORT = 8085;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() throws IOException {

        Path baseDir = Paths.get(System.getProperty("java.io.tmpdir"), "bene-wiremock-temp");

        try {
            Files.createDirectories(baseDir, PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString("rwx------")
            ));
        } catch (UnsupportedOperationException e) {
            // Non-POSIX filesystem (Windows) — permissions handled by OS
            Files.createDirectories(baseDir);
        }

        // Temp dir where classpath stub files will be extracted
        Path tempDir = Files.createTempDirectory(baseDir, "wiremock-");

        // Copy resources from classpath:/wiremock/** → temp dir, preserving structure
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/wiremock/**");

        for (Resource resource : resources) {
            if (resource.isReadable() && resource.getFilename() != null) {
                String urlStr = resource.getURL().toString();
                String pathInJar;

                if (urlStr.contains("!/wiremock/")) {
                    // Running from packaged fat jar (Docker/prod)
                    pathInJar = urlStr.split("!/wiremock/")[1];
                } else {
                    // Running from IDE / exploded classes
                    pathInJar = urlStr.substring(urlStr.indexOf("/wiremock/") + "/wiremock/".length());
                }

                Path destFile = tempDir.resolve(pathInJar);

                if (urlStr.endsWith("/")) {
                    Files.createDirectories(destFile);
                } else {
                    Files.createDirectories(destFile.getParent());
                    try (InputStream in = resource.getInputStream()) {
                        Files.copy(in, destFile);
                    }
                }
            }
        }

        LOG.info("WireMock stub files copied to {}", tempDir);
        LOG.info("Starting WireMock on port {}", WIREMOCK_PORT);

        return new WireMockServer(
                WireMockConfiguration.options()
                        .port(WIREMOCK_PORT)
                        .usingFilesUnderDirectory(tempDir.toFile().getAbsolutePath())
        );
    }
}