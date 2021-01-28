package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class SqlRuParse implements Parse {
    public static void main(String[] args) throws Exception {
        for (int p = 1; p <= 5; p++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + p).get();
            Elements topics = doc.select(".postslisttopic");
            Elements dates = doc.select(".altCol");
            for (int i = 0, j = 1; i < topics.size(); i++, j += 2) {
                Element href = topics.get(i).child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element date = dates.get(j);
                System.out.println(date.text());
            }
        }
    }

    @Override
    public List<Post> list(String link) {
        return null;
    }

    @Override
    public Post detail(String link) {
        return null;
    }
}
