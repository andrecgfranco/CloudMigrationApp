package userClasses.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_metrics")
public class UserMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "metric_id")
    private Metric metric;

    @Column(name = "metric_value")
    private Short metricValue;

    public UserMetric() {
    }

    public UserMetric(User user, Metric metric, Short metricValue) {
        this.user = user;
        this.metric = metric;
        this.metricValue = metricValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public Short getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Short metricValue) {
        this.metricValue = metricValue;
    }
}
