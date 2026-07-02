package userClasses.dto;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import userClasses.entities.UserMetric;

public class UserMetricDto {
        private String name;
        private short value;


        public UserMetricDto(UserMetric metric) {
            this.name = metric.getMetric().getName();
            this.value = metric.getMetricValue();
        }

        public UserMetricDto() {
        }
            

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public short getValue() {
            return value;
        }

        public void setValue(short value) {
            this.value = value;
        }
    }