package io.github.yuokada.guice;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.airlift.configuration.ConfigurationFactory;
import io.airlift.configuration.ConfigurationModule;
import io.airlift.drift.client.DriftClientFactory;
import io.airlift.drift.client.ExceptionClassifier;
import io.airlift.drift.client.address.AddressSelector;
import io.airlift.drift.client.address.SimpleAddressSelector;
import io.airlift.drift.codec.ThriftCodecManager;
import io.airlift.drift.transport.netty.client.DriftNettyClientConfig;
import io.airlift.drift.transport.netty.client.DriftNettyClientModule;
import io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.airlift.drift.client.guice.DriftClientBinder.driftClientBinder;
import static io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory.createStaticDriftNettyMethodInvokerFactory;
import static java.lang.Runtime.getRuntime;

public class MultiplicationDriftClient
{

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(getRuntime().availableProcessors());

    @Inject
    public MultiplicationDriftClient(Service1 service1)
    {
        service1.multiply(1, 2);
    }

    public static void main(String[] args)
    {

        int port = 9090;
//        DriftClientConfig config = new DriftClientConfig()
//                .setMaxRetryTime(Duration.nanosSince(500))
//                .setMaxRetries(3);

        // expensive services that should only be created once
        ThriftCodecManager codecManager = new ThriftCodecManager();
        // server address
        List<HostAndPort> addresses = ImmutableList.of(HostAndPort.fromParts("localhost", 1234));
        AddressSelector addressSelector = new SimpleAddressSelector(addresses);
        DriftNettyClientConfig config2 = new DriftNettyClientConfig();

        // methodInvokerFactory must be closed
        DriftNettyMethodInvokerFactory<?> methodInvokerFactory = DriftNettyMethodInvokerFactory
                .createStaticDriftNettyMethodInvokerFactory(config2);
        // see io.airlift.bootstrap.Bootstrap for a simpler system to create Guice services with configuration
        Injector injector = Guice.createInjector(
                Stage.PRODUCTION,
                new ConfigurationModule(new ConfigurationFactory(ImmutableMap.of())),
                new DriftNettyClientModule(),
                binder -> driftClientBinder(binder).bindDriftClient(Service1.class)
                        .withAddressSelector(new SimpleAddressSelector(addresses))
        );

        DriftNettyClientConfig config = new DriftNettyClientConfig().setPoolEnabled(true);
        DriftNettyMethodInvokerFactory<?> invokerFactory = createStaticDriftNettyMethodInvokerFactory(config);
        DriftClientFactory factory = new DriftClientFactory(
                new ThriftCodecManager(),
                invokerFactory,
                new SimpleAddressSelector(ImmutableList.of(HostAndPort.fromParts("localhost", port))),
                ExceptionClassifier.NORMAL_RESULT);
        Service1 service1 = factory.createDriftClient(Service1.class).get();
//        int result = service1.multiply(100, 200);
        System.out.println(service1.multiply(100, 200));
        //             System.out.println(client.multiply(100, 200));
        // warm up
//        EXECUTOR_SERVICE.invokeAll(Collections.<Callable<Integer>>nCopies(1_000, () -> service1.method(1)));

    }
}
