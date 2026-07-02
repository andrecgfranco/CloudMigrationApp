package userClasses.dto;

import userClasses.entities.Location;

public class LocationDto {
        private long id;
        private String name;

        public LocationDto(Location location) {
            this.id = location.getId();
            this.name = location.getName();
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }