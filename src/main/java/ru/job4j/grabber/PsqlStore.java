package ru.job4j.grabber;

import ru.job4j.quartz.AlertRabbit;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private Connection cn;

    public PsqlStore(Properties cfg) throws SQLException {
        final String url      = cfg.getProperty("url");
        final String username = cfg.getProperty("username");
        final String password = cfg.getProperty("password");
        final String driverClassName = cfg.getProperty("driver-class-name");

        if (url == null
            || username == null
            || password == null
            || driverClassName == null) {
            System.out.print("Missing parameters in config file");
            return;
        }

        try {
            Class.forName(driverClassName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        this.cn = DriverManager.getConnection(url, username, password);
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cn.prepareStatement("insert into post(name, text, link, created) values(?, ?, ?, ?)")) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getText());
            ps.setString(3, post.getLink());
            ps.setDate(4, new Date(post.getCreated().getTimeInMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            //System.out.println("Exception while saving data into DB");
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (Statement stmt = cn.createStatement()) {
            ResultSet res = stmt.executeQuery("select * from post;");
            while (res.next()) {
                Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(new java.util.Date(res.getDate("created").getTime()));
                Post post = new Post(res.getString("name"),
                        res.getString("text"),
                        res.getString("link"),
                        calendar);
                list.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Exception while retrieving ALL posts from DB");
        }
        return list;
    }

    @Override
    public Post findById(String id) {
        try (PreparedStatement stmt = cn.prepareStatement("select * from post where id = ?;")) {
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(new java.util.Date(res.getDate("created").getTime()));
                return new Post(res.getString("name"),
                        res.getString("text"),
                        res.getString("link"),
                        calendar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Exception while retrieving ONE post from DB");
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    public static void main(String[] args) {
        final String propFileName = "app.properties";
        Properties cfg = new Properties();
        try (InputStream is = PsqlStore.class.getClassLoader().getResourceAsStream(propFileName)) {
            if (is != null) {
                cfg.load(is);
            } else {
                System.out.print("property file " + propFileName + " is not found");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (Store s = new PsqlStore(cfg)) {
            Calendar c = Calendar.getInstance();
            s.save(new Post("Name1", "Text1", "Link1", c));
            s.save(new Post("Name2", "Text2", "Link2", c));
            s.save(new Post("Name3", "Text3", "Link1", c));
            System.out.println(s.getAll());
            System.out.println(s.findById("2"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
