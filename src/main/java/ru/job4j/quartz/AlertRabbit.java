package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit implements AutoCloseable {

    private Connection connection;
    private Properties properties;

    public void init(String fileName) {
        properties = new Properties();
        ClassLoader loader = AlertRabbit.class.getClassLoader();
        try (InputStream in = loader.getResourceAsStream(fileName)) {
            properties.load(in);
            Class.forName(properties.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password")
            );
        } catch (Exception e) {
           throw new IllegalStateException();
        }
    }

    public  int getTime(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public static void main(String[] args) {
        AlertRabbit alertRabbit = new AlertRabbit();
        alertRabbit.init("rabbit.properties");
        try (Connection connection = alertRabbit.connection) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(alertRabbit.getTime("rabbit.interval"))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context
                    .getJobDetail()
                    .getJobDataMap()
                    .get("connection");
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into rabbit(created_date) "
                                 + "values (?)")) {
                statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
