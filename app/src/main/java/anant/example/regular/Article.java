package anant.example.regular;

public class Article {
    private String Heading,Author,Genre,Source,Body;

    public Article() {
    }

    public Article(String heading, String author, String genre, String source, String body) {
        Heading = heading;
        Author = author;
        Genre = genre;
        Source = source;
        Body = body;
    }

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }
}
