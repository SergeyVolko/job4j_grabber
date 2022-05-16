package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK =
            String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

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

    private static void showVacancies() throws IOException {
        for (int i = 1; i <= 5; i++) {
            Document document = getDocument(PAGE_LINK + i);
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                String vacancyName = titleElement.text();
                String vacancyDate = dateElement.attr("datetime");
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.println("-".repeat(100));
                System.out.printf("%s %s %s %n%n %s%n",
                        vacancyName, link, vacancyDate, retrieveDescription(link));
            });
        }
    }

    public static void main(String[] args) throws IOException {
       showVacancies();
    }
}
