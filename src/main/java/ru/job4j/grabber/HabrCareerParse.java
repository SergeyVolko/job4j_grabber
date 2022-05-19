package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HarbCareerDateTimeParser;
import ru.job4j.model.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    public static final int PAGES = 5;

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK =
            String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) {
        String result = "";
        try {
            result = getDocument(link).select(".style-ugc").text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Document getDocument(String link) throws IOException {
        return Jsoup.connect(link).get();
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        Document document;
        try {
            for (int i = 1; i <= PAGES; i++) {
                document = getDocument(link + i);
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> posts.add(getPost(row)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public Post getPost(Element element) {
        Element titleElement = element.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element dateElement = element.select(".vacancy-card__date").first().child(0);
        String vacancyName = titleElement.text();
        LocalDateTime vacancyDate = dateTimeParser.parse(dateElement.attr("datetime"));
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String description = retrieveDescription(link);
        return new Post(vacancyName, link, description, vacancyDate);
    }

    public void showFirstFiveVacancies() throws IOException {
        for (int i = 1; i <= PAGES; i++) {
            Document document = getDocument(PAGE_LINK + i);
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Post post = getPost(row);
                System.out.println("-".repeat(100));
                System.out.printf("%s %s %s %n%n %s%n",
                        post.getTitle(), post.getLink(), post.getCreated(), post.getDescription());
            });
        }
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse parser = new HabrCareerParse(new HarbCareerDateTimeParser());
        List<Post> posts = parser.list(PAGE_LINK);
        posts.forEach(System.out::println);
    }
}
