package com.jeff_media.stackresize.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    @Getter private static boolean updateInventories;

    public static void init(FileConfiguration config) {
        updateInventories = config.getBoolean("update-inventories", true);
    }
}
