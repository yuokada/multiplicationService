package io.github.yuokada.guice;

import com.google.common.collect.ImmutableSet;
import io.airlift.drift.codec.ThriftCodecManager;
import io.airlift.drift.server.DriftServer;
import io.airlift.drift.server.DriftService;
import io.airlift.drift.server.stats.NullMethodInvocationStatsFactory;
import io.airlift.drift.transport.netty.server.DriftNettyServerConfig;
import io.airlift.drift.transport.netty.server.DriftNettyServerTransportFactory;

public class MultiplicationDriftServer
{
    private static DriftServer server = new DriftServer(
            new DriftNettyServerTransportFactory(new DriftNettyServerConfig()),
            new ThriftCodecManager(),
            new NullMethodInvocationStatsFactory(),
            ImmutableSet.of(new DriftService(new MultiplicationServiceImpl())),
            ImmutableSet.of()
    );

    public static void main(String[] args)
    {
        System.out.println("Start DriftServer");
        server.getServerTransport();
        server.start();
        System.out.println("hey");

        server.shutdown();
    }
}
