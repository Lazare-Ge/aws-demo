package com.lazare.awsdemowebsite.config;

import com.zaxxer.hikari.HikariDataSource;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;

import java.net.URI;

public class RDSIAMDataSource extends HikariDataSource {
    @Override
    public String getPassword() {
        // Alternatively, you can create a Bean declaration of RdsClient. For demo purposes, I have decided to instantiate it here.
        RdsClient rdsClient = RdsClient.builder()
                .region(new DefaultAwsRegionProviderChain().getRegion())
                .build();


        RdsUtilities rdsUtilities = rdsClient.utilities();

        URI jdbcUri = parseJdbcURL(getJdbcUrl());


        GenerateAuthenticationTokenRequest request = GenerateAuthenticationTokenRequest.builder()
                .username(getUsername())
                .hostname(jdbcUri.getHost())
                .port(jdbcUri.getPort())
                .build();

        String token = rdsUtilities.generateAuthenticationToken(request);
        return token;
    }

    private URI parseJdbcURL(String jdbcUrl) {
        String uri = jdbcUrl.substring(5);
        return URI.create(uri);
    }



}
