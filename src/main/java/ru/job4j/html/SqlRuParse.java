package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements topics = doc.select(".postslisttopic");
        Elements dates = doc.select(".altCol");
        for (int i = 1, j = 0; i < dates.size(); i += 2, j++) {
            Element href = topics.get(j).child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            Element date = dates.get(i);
            System.out.println(date.text());
        }
    }
}
