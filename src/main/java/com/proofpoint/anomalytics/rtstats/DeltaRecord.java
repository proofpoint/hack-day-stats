package com.proofpoint.anomalytics.rtstats;

public class DeltaRecord
{
    private final int delta;
    private final String label;
    private final int count;

    public DeltaRecord(int delta, String label, int count)
    {
        this.delta = delta;
        this.label = label;
        this.count = count;
    }

    public int getDelta()
    {
        return delta;
    }

    public String getLabel()
    {
        return label;
    }

    public int getCount()
    {
        return count;
    }
}
