package com.proofpoint.anomalytics.rtstats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class Store
{
    private final Map<String, Object> previous = Maps.newHashMap();
    private final Map<String, Object> current = Maps.newHashMap();

    public Map<String, Object> getAll()
    {
        synchronized (this) {
            return ImmutableMap.copyOf(current);
        }
    }

    public Object getCurrent(String key)
    {
        synchronized (this) {
            return current.get(key);
        }
    }
    
    public Pair<Object, Object> get(String key)
    {
        synchronized (this) {
            Object previousValue = previous.get(key);
            Object currentValue = current.get(key);

            if (previousValue == null && currentValue == null) {
                return null;
            }

            return new Pair<Object, Object>(previousValue, currentValue);
        }
    }
    
    public Object put(String key, Object value)
    {
       synchronized (this) {
           Object previousValue = current.get(key);
           current.put(key, value);
           previous.put(key, previousValue);

           return previousValue;
       }
    }

    public Object remove(String key)
    {
        synchronized (this) {
            Object previousValue = current.get(key);
            current.put(key, null);
            previous.put(key, previousValue);

            return previousValue;
        }
    }
}
