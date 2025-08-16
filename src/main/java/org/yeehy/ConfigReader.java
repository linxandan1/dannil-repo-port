package org.yeehy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    public static void main(String[] args) {
            try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
                properties.load(fis);
                System.out.println("ye");
            } catch (IOException e) {
                throw new RuntimeException("Не удалось загрузить config.properties", e);
            }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
