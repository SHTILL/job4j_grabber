package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    public Store store() throws SQLException {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {
        final String propFileName = "app.properties";
        try (InputStream is = PsqlStore.class.getClassLoader().getResourceAsStream(propFileName)) {
            if (is != null) {
                cfg.load(is);
            } else {
                System.out.print("property file " + propFileName + " is not found");
            }
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            List<Post> allPosts = new ArrayList<>();

            int page;
            final String adsLink = "https://www.sql.ru/forum/job-offers/";
            System.out.printf("Start parsing data from %s" + System.lineSeparator(), adsLink);
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
                    ad.setText(details.getText());
                }
                allPosts.addAll(ads);
            }
            System.out.printf("Finish parsing data from %s" + System.lineSeparator(), adsLink);
            System.out.println("Store data into DB");
            allPosts.forEach(store::save);
            System.out.println("Storing is completed");
        }
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(post.toString().getBytes());
                            out.write(System.lineSeparator().getBytes());
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
        grab.web(store);
    }
}
