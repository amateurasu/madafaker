package com.viettel.ems.grpc.service;

import com.viettel.ems.VesEventCollectorGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import static com.viettel.ems.ProtobufGateway.VesEventReply;
import static org.onap.ves.DCAE.VesEvent;

@Slf4j
public class VesEventCollectorImpl extends VesEventCollectorGrpc.VesEventCollectorImplBase {

    @Override
    public void collect(VesEvent request, StreamObserver<VesEventReply> responseObserver) {
        super.collect(request, responseObserver);
        log.info("request {}", request);
    }
}
