0. Техническое задание. Агрегатор Java Вакансий. [#260357]

Описание.

Система запускается по расписанию. Период запуска указывается в настройках - app.properties.
Первый сайт будет career.habr.com. В нем есть раздел https://career.habr.com/vacancies/java_developer. 
С ним будет идти работа. Программа должна считывать все вакансии относящиеся к Java и записывать их в базу.
Доступ к интерфейсу будет через REST API.

Расширение.

1. В проект можно добавить новые сайты без изменения кода.
2. В проекте можно сделать параллельный парсинг сайтов.