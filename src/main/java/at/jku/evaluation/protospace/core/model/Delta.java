package at.jku.evaluation.protospace.core.model;

public class Delta {
    String val;
    long timestamp;

    public Delta(String val, long timestamp)
    {
        this.val = val;
        this.timestamp = timestamp;
    }

    public String getValue() {
        return val;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
}
