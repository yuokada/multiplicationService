package io.github.yuokada.guice;

import io.airlift.drift.annotations.ThriftMethod;
import io.airlift.drift.annotations.ThriftService;

@ThriftService
public interface MultiplicationService
{
    @ThriftMethod
    int multiply(int a, int b);
}
