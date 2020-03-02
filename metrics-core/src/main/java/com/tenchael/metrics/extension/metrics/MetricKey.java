package com.tenchael.metrics.extension.metrics;

/**
 * Created by Tenchael on 2020/2/18.
 */
public class MetricKey {
	private MetricType metricType;
	private String category;
	private String name;

	public MetricKey() {
	}

	public MetricKey(MetricType metricType, String category, String name) {
		this.metricType = metricType;
		this.category = category;
		this.name = name;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public MetricType getMetricType() {
		return metricType;
	}

	public void setMetricType(MetricType metricType) {
		this.metricType = metricType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("%s:%s:%s", metricType, category, name);
	}

	@Override
	public int hashCode() {
		int t = (metricType != null) ? metricType.hashCode() : 0;
		int c = (category != null) ? category.hashCode() : 0;
		int n = (name != null) ? name.hashCode() : 0;
		return t * 97 + c * 31 + n;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MetricKey)) {
			return false;
		}
		MetricKey key = (MetricKey) obj;

		return equals(this.metricType, key.metricType)
				&& equals(this.category, key.category)
				&& equals(this.name, key.name);
	}

	public boolean equals(MetricType t1, MetricType t2) {
		if (t1 == null && t2 == null) {
			return true;
		}
		if (t1 == null || t2 == null) {
			return false;
		}
		return t1 == t2;
	}

	public boolean equals(String str1, String str2) {
		if (str1 == null && str2 == null) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		return str1.equals(str2);
	}

	public enum MetricType {
		counter, histogram
	}

	public static class Builder {
		private MetricType metricType;
		private String category;
		private String name;

		public Builder metricType(MetricType metricType) {
			this.metricType = metricType;
			return this;

		}

		public Builder category(String category) {
			this.category = category;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;

		}

		public MetricKey build() {
			return new MetricKey(metricType, category, name);
		}
	}

}
