package com.tenchael.metrics.extension.reporter.jmx;

import com.tenchael.metrics.extension.metrics.Histogram;
import com.tenchael.metrics.extension.metrics.Snapshot;

import javax.management.ObjectName;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

public interface JmxHistogramMXBean extends MBean {

    long getCount();

    long getU10();

    long getU50();

    long getU500();

    double getMin();

    double getMax();

    double getMean();

    double getMedian();

    double getStddev();

    double getP75();

    double getP95();

    double getP98();

    double getP99();

    double getP999();

    TimeUnit getDurationUnit();

    class JmxHistogram extends BaseMBean implements JmxHistogramMXBean {
        public static final long DEFAULT_REFRESH_INTERVAL = 1;
        private final Histogram metrics;
        private final TimeUnit durationUnit;
        private final double durationFactor;
        private volatile Snapshot snapshot;
        private long latestSnapshot;
        /**
         * 小数精度
         */
        private int precise = 4;
        /**
         * 快照刷新间隔（单位：毫秒）
         */
        private long refreshIntervalMills;

        public JmxHistogram(ObjectName oname, Histogram metrics) {
            this(oname, metrics, DEFAULT_REFRESH_INTERVAL);
        }

        public JmxHistogram(ObjectName oname, Histogram metrics, long refreshSeconds) {
            this(oname, metrics, refreshSeconds, TimeUnit.MILLISECONDS);
        }

        public JmxHistogram(ObjectName oname, Histogram metrics, long refreshSeconds, TimeUnit durationUnit) {
            super(oname);
            this.metrics = metrics;
            this.refreshIntervalMills = TimeUnit.MILLISECONDS
                    .convert(refreshSeconds, TimeUnit.SECONDS);
            this.durationUnit = durationUnit;
            this.durationFactor = 1.0 / durationUnit.toNanos(1);
        }

        @Override
        public long getCount() {
            return metrics.getCount();
        }

        @Override
        public long getU10() {
            return metrics.getU10();
        }

        @Override
        public long getU50() {
            return metrics.getU50();
        }

        @Override
        public long getU500() {
            return metrics.getU500();
        }

        @Override
        public double getMin() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.getMin());
                }
            });
        }

        @Override
        public double getMax() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.getMax());
                }
            });
        }

        @Override
        public double getMean() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.getMean());
                }
            });
        }


        @Override
        public double getMedian() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.getMedian());
                }
            });
        }

        @Override
        public double getStddev() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.getStdDev());
                }
            });
        }

        @Override
        public double getP75() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.get75thPercentile());
                }
            });
        }

        @Override
        public double getP95() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.get95thPercentile());
                }
            });
        }

        @Override
        public double getP98() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.get98thPercentile());
                }
            });
        }

        @Override
        public double getP99() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.get99thPercentile());
                }
            });
        }

        @Override
        public double getP999() {
            return execute(new SnapshotListener() {
                @Override
                public Double onRefresh(Snapshot ss) {
                    return convertDuration(ss.get999thPercentile());
                }
            });
        }

        @Override
        public TimeUnit getDurationUnit() {
            return this.durationUnit;
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

        protected double convertDuration(double duration) {
            return decimalPrecise(duration * durationFactor);
        }

        protected double decimalPrecise(double d) {
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                return 0.0d;
            }
            return BigDecimal.valueOf(d)
                    .setScale(precise, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        interface SnapshotListener {
            <T> T onRefresh(Snapshot ss);
        }


    }


}
