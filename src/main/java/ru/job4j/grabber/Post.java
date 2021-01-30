package ru.job4j.grabber;

import java.text.DateFormat;
import java.util.Calendar;

public class Post {
    private int id;
    private String   name;
    private String   description;
    private String   link;
    private Calendar created;

    public Post(int id, String name, String description, String link, Calendar created) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.created = created;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Post{"
                + "created=" + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(created.getTime())  + '\''
                + ", name='" + name + '\''
                + ", text='" + description + '\''
                + ", link='" + link + '\''
                + ", id='"   + id
                + '}';
    }
}