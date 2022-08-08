package net.maed.biomestemperature.config;

import com.mojang.datafixers.util.Pair;
import net.maed.biomestemperature.BiomesTemperature;

public class Configs {
    public static SimpleConfig CONFIG;
    private static ConfigProvider configs;

    public static int TEMPERATURE_SCALE;

    public static void registerConfigs() {
        configs = new ConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(BiomesTemperature.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("temperature.scale", 0), "0 = Celsius, 1 = Fahrenheit, 2 = Kelvin");
    }

    private static void assignConfigs() {
        TEMPERATURE_SCALE = CONFIG.getOrDefault("temperature.scale", 0);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}
