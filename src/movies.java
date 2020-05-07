
public class movies {

    private String title;

    private String id;

    private int year;

    private String director;

    private String genres;

    public movies(){

    }

    public movies(String title, String id, int year, String director, String genres) {
        this.title = title;
        this.id  = id;
        this.year = year;
        this.director = director;
        this.genres = genres;
    }

//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movies Details - ");
        sb.append("title:" + getTitle());
        sb.append(", ");
        sb.append("id:" + getId());
        sb.append(", ");
        sb.append("year:" + getYear());
        sb.append(", ");
        sb.append("director:" + getDirector());
        sb.append(", ");
        sb.append("genres:" + getGenres());
        sb.append(".");

        return sb.toString();
    }
}
