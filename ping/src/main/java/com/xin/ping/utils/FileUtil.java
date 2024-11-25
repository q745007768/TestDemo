package com.xin.ping.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    public static final String LOCK_FILE_PATH = "rate_limit.txt";
    private static final int MAX_REQUESTS_PER_SECOND = 2;

    public static boolean tryAcquire() {
        FileChannel channel = null;
        FileLock lock = null;
        try {
            Path filePath = Paths.get(LOCK_FILE_PATH);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            channel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
            lock = channel.lock(); // 获取文件锁
//            log.info("拿到文件锁.");

            // 读取文件内容
            ByteBuffer buffer = ByteBuffer.allocate(16); // 存储8字节的秒数 + 4字节的计数器
            channel.read(buffer);
            buffer.flip();

            long fileTimestamp = buffer.hasRemaining() ? buffer.getLong() : 0L;
            int requestCount = buffer.hasRemaining() ? buffer.getInt() : 0;

            long currentTimestamp = Instant.now().getEpochSecond();

            if (fileTimestamp == currentTimestamp) {
                if (requestCount < MAX_REQUESTS_PER_SECOND) {
                    requestCount++;
                    writeToFile(channel, currentTimestamp, requestCount);
//                    log.info("当前秒写入次数: {}", requestCount);
                    return true;
                } else {
//                    log.info("当前秒写入次数超出2次.");
                    return false;
                }
            } else {
                // 重置计数器并写入当前时间戳
                writeToFile(channel, currentTimestamp, 1);
//                log.info("重新计数!");
                return true;
            }

        } catch (IOException e) {
            log.error("Error occurred while acquiring file lock or processing file.", e);
            return false;
        } finally {
            // 确保锁和文件通道都能正确关闭
            try {
                if (lock != null) {
                    lock.release();
//                    log.info("File lock released.");
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                log.error("Error occurred while releasing file lock or closing file channel.", e);
            }
        }
    }

    private static void writeToFile(FileChannel channel, long timestamp, int count) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(timestamp);
        buffer.putInt(count);
        buffer.flip();
        channel.position(0); // 重置写入位置
        channel.write(buffer);
        channel.truncate(buffer.limit()); // 确保文件没有旧数据
        channel.force(true); // 确保数据写入磁盘
    }
}
