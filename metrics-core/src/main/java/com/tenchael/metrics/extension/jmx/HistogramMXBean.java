package com.tenchael.metrics.extension.jmx;

public interface HistogramMXBean extends MBean {

    long getCount();

    long getMin();

    long getMax();

    double getMean();

    double getMedian();

    double getStddev();

    double getP75();

    double getP95();

    double getP98();

    double getP99();

    double getP999();

    void update(long value);

}
