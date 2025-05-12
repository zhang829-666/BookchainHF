package com.bookchain.config;

import org.hyperledger.fabric.gateway.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 区块链配置类，初始化Hyperledger Fabric网关连接
 */
@Configuration
public class BlockchainConfig {

    @Value("${fabric.gateway.organizations}")
    private String organization;

    @Value("${fabric.gateway.endpoint}")
    private String endpoint;

    @Value("${fabric.gateway.credential.path}")
    private String credentialPath;

    @Value("${fabric.gateway.private.key.path}")
    private String privateKeyPath;

    @Value("${fabric.gateway.channel.name}")
    private String channelName;

    @Bean
    public Gateway gateway() throws IOException {
        // 加载客户端证书和私钥
        byte[] certificate = Files.readAllBytes(Paths.get(credentialPath));
        byte[] privateKey = Files.readAllBytes(Paths.get(privateKeyPath));

        // 配置连接选项
        Gateway.Options options = Gateway.Options.builder()
                .identity(new X509Identity(organization, new String(certificate)))
                .privateKey(new PrivateKey(new String(privateKey)))
                .endpoint(endpoint)
                .discovery(true) // 启用通道发现
                .build();

        // 创建网关连接
        return Gateway.create(options);
    }

    @Bean
    public Network network(Gateway gateway) {
        return gateway.getNetwork(channelName);
    }
}
