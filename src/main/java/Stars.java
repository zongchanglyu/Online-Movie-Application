package main.java;

public class Stars {
    private String name;
    private int birthYear;
    private String id;

    public Stars() {
        birthYear = 0;
    }

    public Stars(String name, int birthYear, String id) {
        this.name = name;
        this.birthYear = birthYear;
        this.id = id;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Stars Details - ");
        sb.append("starName:" + getName());
        sb.append(", ");
        sb.append("id:" + getId());
        sb.append(", ");
        sb.append("birthYear:" + getBirthYear());
        sb.append(".");

        return sb.toString();
    }
}
