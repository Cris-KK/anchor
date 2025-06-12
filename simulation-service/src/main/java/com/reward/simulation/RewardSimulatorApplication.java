package com.reward.simulation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.*;

@SpringBootApplication
public class RewardSimulatorApplication implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    // 打赏接口地址
    private static final String REQUEST_URL = "http://localhost:8091/audience/reward";
    // 每秒请求数
    private static final int TARGET_RPS = 50;
    // 模拟总时长（秒）
    private static final int SIMULATION_DURATION_SECONDS = 60;

    // 存储观众ID和主播ID
    private final List<String> audienceIds = new ArrayList<>();
    private final List<String> anchorIds = new ArrayList<>();
    private final Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(RewardSimulatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. 查询观众和主播ID
        loadIdsFromDb();

        if (audienceIds.isEmpty() || anchorIds.isEmpty()) {
            System.err.println("数据库中没有观众或主播数据，无法模拟打赏！");
            return;
        }

        // 创建线程池和定时调度器
        ExecutorService executor = Executors.newFixedThreadPool(TARGET_RPS);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 单次请求任务
        Runnable requestTask = () -> {
            try {
                sendRewardRequest();
            } catch (Exception e) {
                System.err.println("发送打赏请求时出错: " + e.getMessage());
            }
        };

        // 每秒批量提交 TARGET_RPS 个请求
        Runnable submitBatch = () -> {
            for (int i = 0; i < TARGET_RPS; i++) {
                executor.submit(requestTask);
            }
        };

        // 每秒执行一次任务提交
        ScheduledFuture<?> schedulerHandle = scheduler.scheduleAtFixedRate(
                submitBatch, 0, 1, TimeUnit.SECONDS
        );

        // 模拟运行指定时长后结束
        Thread.sleep(SIMULATION_DURATION_SECONDS * 1000L);

        // 关闭调度器和线程池
        schedulerHandle.cancel(true);
        scheduler.shutdown();
        executor.shutdown();

        System.out.println("模拟结束。");
        System.exit(0); // 让Spring Boot应用退出
    }

    /**
     * 查询数据库获取观众和主播ID
     */
    private void loadIdsFromDb() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            // 查询观众ID
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT audience_id FROM audience")) {
                while (rs.next()) {
                    audienceIds.add(rs.getString("audience_id"));
                }
            }
            // 查询主播ID
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT anchor_id FROM anchor")) {
                while (rs.next()) {
                    anchorIds.add(rs.getString("anchor_id"));
                }
            }
        }
    }

    /**
     * 随机生成一次打赏请求
     */
    private void sendRewardRequest() throws Exception {
        // 随机选取观众和主播
        String audienceId = audienceIds.get(random.nextInt(audienceIds.size()));
        String anchorId = anchorIds.get(random.nextInt(anchorIds.size()));
        double amount = 1 + random.nextInt(100); // 随机金额1~100

        URL url = new URL(REQUEST_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 构造请求体
        String jsonInputString = String.format(
                "{\"audienceId\": \"%s\", \"anchorId\": \"%s\", \"amount\": %.2f}",
                audienceId, anchorId, amount);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        connection.disconnect();
    }
}