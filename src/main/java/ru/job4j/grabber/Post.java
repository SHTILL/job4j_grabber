package ru.job4j.grabber;

import java.text.DateFormat;
import java.util.Calendar;

public class Post {
    private String   name;
    private String   text;
    private String   link;
    private Calendar created;

    public Post(String text, Calendar created) {
        this.text = text;
        this.created = created;
    }

    public String getLink() {
        return link;
    }

    public Post(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Post{"
                + "created=" + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(created.getTime())  + '\''
                + ", name='" + name + '\''
                + ", text='" + text + '\''
                + ", link='" + link
                + '}';
    }
}