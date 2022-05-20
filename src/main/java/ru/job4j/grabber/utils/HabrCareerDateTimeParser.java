package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        return OffsetDateTime.parse(parse).toLocalDateTime();
    }

    public static void main(String[] args) {
        HabrCareerDateTimeParser habr = new HabrCareerDateTimeParser();
        System.out.println(habr.parse("2022-05-08T13:03:54+03:00"));
    }
}
