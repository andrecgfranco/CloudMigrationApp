package AndreFranco.LocationService.dto;

public class TopEntryDto {

    private int rank;
    private String name;
    private double value;

    public TopEntryDto() {}

    public TopEntryDto(int rank, String name, double value) {
        this.rank  = rank;
        this.name  = name;
        this.value = value;
    }

    public int getRank()             { return rank; }
    public String getName()          { return name; }
    public double getValue()         { return value; }

    public void setRank(int rank)        { this.rank = rank; }
    public void setName(String name)     { this.name = name; }
    public void setValue(double value)   { this.value = value; }
}