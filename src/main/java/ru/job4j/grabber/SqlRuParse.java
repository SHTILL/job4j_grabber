package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    @Override
    public List<Post> list(String adsLink) {
        int page = 1;
        List<Post> ads = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(adsLink).get();
            Elements topics = doc.select(".postslisttopic");
            for (int i = 3; i < topics.size(); i++) {
                Element href = topics.get(i).child(0);
                ads.add(new Post(0, href.text(), null, href.attr("href"), null));
            }
        } catch (IOException e) {
            System.out.printf("Error while connecting to the link %s" + System.lineSeparator(), adsLink + page);
            return null;
        }
        return ads;
    }

    @Override
    public Post detail(String link) {
        try {
            Document postDoc = Jsoup.connect(link).get();
            Elements descriptionElements = postDoc.getElementsByClass("msgBody");
            String description = descriptionElements.get(1).text();
            Elements footerElements = postDoc.getElementsByClass("msgFooter");
            Element createdElement = footerElements.get(0);
            String created = createdElement.ownText().replaceAll(" \\[] \\|$", "");
            return new Post(0, null, description, null, DateParser.parseDateString(created));
        } catch (IOException e) {
            System.out.printf("Error while connecting to the link %s" + System.lineSeparator(), link);
        }
        return null;
    }

    public static void main(String[] args) {
        Parse parse = new SqlRuParse();
        List<Post> allPosts = new ArrayList<>();

        int page;
        final String adsLink = "https://www.sql.ru/forum/job-offers/";
        for (page = 1; page <= 5; page++) {
            List<Post> ads = parse.list(adsLink + page);
            if (ads == null) {
                return;
            }
            for (Post ad: ads) {
                Post details = parse.detail(ad.getLink());
                if (details == null) {
                    return;
                }
                ad.setCreated(details.getCreated());
                ad.setDescription(details.getDescription());
            }
            allPosts.addAll(ads);
        }
        allPosts.forEach(System.out::println);
        System.out.print(allPosts.size());
    }
}
