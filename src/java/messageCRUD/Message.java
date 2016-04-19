package messageCRUD;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.faces.bean.ApplicationScoped;

/**
 * @author Kihoon, Lee
 */

@ApplicationScoped
public class Message {
    
    private int id;
    private String title;
    private String contents;
    private String author;
    private String senttime;

    public Message(){
    }

    public Message(int id, String title, String contents, String author, String senttime) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.author = author;
        this.senttime=senttime;
    }
 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String autor) {
        this.author = autor;
    }
    
    public String getSenttime() {
        return senttime;
    }

    public void setSenttime(String senttime) {
        this.senttime = senttime;
    }
    
    @Override
    public String toString(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}