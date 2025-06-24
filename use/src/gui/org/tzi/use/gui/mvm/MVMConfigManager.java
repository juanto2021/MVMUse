package org.tzi.use.gui.mvm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MVMConfigManager {

    private Properties properties;

    public MVMConfigManager(String filename) {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filename)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    // Método main de prueba
    public static void main(String[] args) {
        MVMConfigManager config = new MVMConfigManager("config.properties");

        String apiKey = config.get("api_key");
        String endpoint = config.get("endpoint");

        System.out.println("API Key: " + apiKey);
        System.out.println("Endpoint: " + endpoint);
    }
}
