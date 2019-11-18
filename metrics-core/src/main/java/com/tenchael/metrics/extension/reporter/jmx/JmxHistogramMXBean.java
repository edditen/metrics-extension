package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.Snapshot;

import javax.management.ObjectName;
import java.util.concurrent.TimeUnit;

public interface JmxHistogramMXBean extends MBean {

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

    class JmxHistogram extends BaseMBean implements JmxHistogramMXBean {
        private final Histogram metrics;

        private volatile Snapshot snapshot;
        private long latestSnapshot;

        public static final long DEFAULT_REFRESH_INTERVAL = 1;

        /**
         * 快照刷新间隔（单位：毫秒）
         */
        private long refreshIntervalMills;

        public JmxHistogram(ObjectName oname, Histogram metrics) {
            this(oname, metrics, DEFAULT_REFRESH_INTERVAL);
        }

        public JmxHistogram(ObjectName oname, Histogram metrics, long refreshSeconds) {
            super(oname);
            this.metrics = metrics;
            this.refreshIntervalMills = TimeUnit.MILLISECONDS
                    .convert(refreshSeconds, TimeUnit.SECONDS);
        }

        @Override
        public long getCount() {
            return metrics.getCount();
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

        private <T> T execute(SnapshotListener listener) {
            long now = nowTime();
            if (Math.abs(now - latestSnapshot) > this.refreshIntervalMills) {
                this.snapshot = this.metrics.getSnapshot();
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


}
