package com.proofpoint.anomalytics.rtstats;

import com.google.common.base.Function;

import javax.annotation.Nullable;

public class Pair<U, V>
{
    private final U first;
    private final V second;

    public Pair(U first, V second)
    {
        this.first = first;
        this.second = second;
    }

    public U getFirst()
    {
        return first;
    }

    public V getSecond()
    {
        return second;
    }
    
    public static <U, V> Function<Pair<U, V>, U> first()
    {
        return new Function<Pair<U, V>, U>()
        {
            public U apply(Pair<U, V> input)
            {
                return input.getFirst();
            }
        };
    }
}
