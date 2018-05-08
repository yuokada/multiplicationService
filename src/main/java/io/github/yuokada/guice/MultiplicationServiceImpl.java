package io.github.yuokada.guice;

import io.airlift.drift.annotations.ThriftField;

public class MultiplicationServiceImpl implements MultiplicationService
{
    @Override
    public int multiply(
        @ThriftField(value = 1, name = "a") int a,
        @ThriftField(value = 2, name = "b") int b)
    {
        System.out.println("DEBUG: called multiply.");
        return a * b;
    }
}
