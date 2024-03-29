package anant.example.regular;

public class Events {
    private String label,heading,startEventTime,endEventTime;
    Boolean star;

    public Events(){

    }
    public Events(String label, String heading, String startEventTime, String endEventTime,Boolean star) {
        this.label=label;
        this.heading = heading;
        this.startEventTime = startEventTime;
        this.endEventTime = endEventTime;
        this.star=star;
    }

    public Boolean getStar() {
        return star;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getStartEventTime() {
        return startEventTime;
    }

    public void setStartEventTime(String startEventTime) {
        this.startEventTime = startEventTime;
    }

    public String getEndEventTime() {
        return endEventTime;
    }

    public void setEndEventTime(String endEventTime) {
        this.endEventTime = endEventTime;
    }
}
