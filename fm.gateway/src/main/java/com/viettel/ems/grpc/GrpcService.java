package com.viettel.ems.grpc;

import com.viettel.ems.grpc.service.VesEventCollectorImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class GrpcService implements CommandLineRunner {

    @Value("${fm.grpc.enable}")
    private boolean enable;

    @Value("${fm.grpc.port}")
    private int grpcPort;

    @Override
    public void run(String... args) throws Exception {
        log.info("gRPC is {}enabled at port {}", enable ? "" : "not ", grpcPort);
        if (enable) {
            Server server = ServerBuilder.forPort(grpcPort).addService(new VesEventCollectorImpl()).build();
            server.start();
            server.awaitTermination();
        }
    }
}
