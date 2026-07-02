package userClasses.dto;

public class CountryScoreDto {

    private String country;
    private double score;

    public CountryScoreDto(String country, double score) {
        this.country = country;
        this.score = score;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void addToScore(double value) {
        this.score += value;
    }
    
}
