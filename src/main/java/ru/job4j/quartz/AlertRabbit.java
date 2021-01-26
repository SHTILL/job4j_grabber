package ru.job4j.quartz;

import java.sql.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        final String propFileName = "rabbit.properties";

        Properties p = new Properties();
        try (InputStream is = AlertRabbit.class.getClassLoader().getResourceAsStream(propFileName)) {
            if (is != null) {
                p.load(is);
            } else {
                System.out.print("property file " + propFileName + " is not found");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        final String timeInterval = p.getProperty("rabbit.interval");
        final String url      = p.getProperty("url");
        final String username = p.getProperty("username");
        final String password = p.getProperty("password");
        final String driverClassName = p.getProperty("driver-class-name");

        if (timeInterval == null
            || url == null
            || username == null
            || password == null) {
            System.out.print("Missing parameters in config file");
            return;
        }

        final int time = Integer.parseInt(timeInterval);
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try (Connection cn = DriverManager.getConnection(url, username, password)) {
            JobDataMap data = new JobDataMap();
            data.put("db_connection", cn);

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).usingJobData(data).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(time)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println("Job hashCode:" + hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("db_connection");
            try {
                PreparedStatement ps = cn.prepareStatement("insert into rabbit(time) values(?)");
                ps.setLong(1, System.currentTimeMillis());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}