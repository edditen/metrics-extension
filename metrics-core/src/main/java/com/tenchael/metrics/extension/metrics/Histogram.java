package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.jmx.HistogramMXBean;
import com.tenchael.metrics.extension.jmx.MBean.BaseMBean;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;


public class Histogram extends BaseMBean implements HistogramMXBean {

    public static final long DEFAULT_REFRESH_INTERVAL = 5;
    private final Reservoir reservoir;
    private final LongAdder count;

    private volatile Snapshot snapshot;
    private long latestSnapshot;

    /**
     * 快照刷新间隔（单位：秒）
     */
    private long refreshIntervalMills;

    public Histogram(String category, String name) {
        this(category, name, new UniformReservoir(), DEFAULT_REFRESH_INTERVAL);
    }

    public Histogram(String category, String name, Reservoir reservoir, long refreshSeconds) {
        super(category, name);
        this.reservoir = reservoir;
        this.count = new LongAdder();
        this.snapshot = this.reservoir.getSnapshot();
        this.latestSnapshot = System.currentTimeMillis();
        this.refreshIntervalMills = TimeUnit.MILLISECONDS
                .convert(refreshSeconds, TimeUnit.SECONDS);
    }

    @Override
    public long getCount() {
        return count.sum();
    }

    @Override
    public long getMin() {
        return execute(new SnapshotListener() {
            @Override
            public Long onRefresh(Snapshot ss) {
                return ss.getMin();
            }
        });
    }

    @Override
    public long getMax() {
        return execute(new SnapshotListener() {
            @Override
            public Long onRefresh(Snapshot ss) {
                return ss.getMax();
            }
        });
    }

    @Override
    public double getMean() {
        return execute(new SnapshotListener() {
            @Override
            public Double onRefresh(Snapshot ss) {
                return ss.getMean();
            }
        });
    }


    @Override
    public double getMedian() {
        return execute(new SnapshotListener() {
            @Override
            public Double onRefresh(Snapshot ss) {
                return ss.getMedian();
            }
        });
    }

    @Override
    public double getStddev() {
        return execute(new SnapshotListener() {
            @Override
            public Double onRefresh(Snapshot ss) {
                return ss.getStdDev();
            }
        });
    }

    @Override
    public double getP75() {
        return execute(new SnapshotListener() {
            @Override
            public Double onRefresh(Snapshot ss) {
                return ss.get75thPercentile();
            }
        });
    }

    @Override
    public double getP95() {
        return execute(new SnapshotListener() {
            @Override
            public Double onRefresh(Snapshot ss) {
                return ss.get95thPercentile();
            }
        });
    }

    @Override
    public double getP98() {
        return execute(new SnapshotListener() {
            @Override
            public Double onRefresh(Snapshot ss) {
                return ss.get98thPercentile();
            }
        });
    }

    @Override
    public double getP99() {
        return execute(new SnapshotListener() {
            @Override
            public Double onRefresh(Snapshot ss) {
                return ss.get99thPercentile();
            }
        });
    }

    @Override
    public double getP999() {
        return execute(new SnapshotListener() {
            @Override
            public Double onRefresh(Snapshot ss) {
                return ss.get999thPercentile();
            }
        });
    }

    @Override
    public void update(long value) {
        this.count.increment();
        this.reservoir.update(value);
    }

    private <T> T execute(SnapshotListener listener) {
        long now = nowTime();
        if (Math.abs(now - latestSnapshot) > this.refreshIntervalMills) {
            this.snapshot = this.reservoir.getSnapshot();
            this.latestSnapshot = now;
        }
        return listener.onRefresh(snapshot);
    }

    private long nowTime() {
        return System.currentTimeMillis();
    }

    interface SnapshotListener {
        <T> T onRefresh(Snapshot ss);
    }

}
