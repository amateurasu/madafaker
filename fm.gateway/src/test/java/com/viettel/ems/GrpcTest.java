package com.viettel.ems;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.onap.ves.DCAE;

public class GrpcTest {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        var stub = VesEventCollectorGrpc.newBlockingStub(channel);

        var response = stub.collect(DCAE.VesEvent.newBuilder().build());
        System.out.println(response);
        channel.shutdown();
    }
}
