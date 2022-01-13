package com.jeff_media.stackresize;

import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Enums;
import com.jeff_media.stackresize.commands.MainCommand;
import com.jeff_media.stackresize.config.Config;
import com.jeff_media.stackresize.listeners.*;
import de.jeff_media.configupdater.ConfigUpdater;
import de.jeff_media.jefflib.JeffLib;
import de.jeff_media.jefflib.MaterialUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StackResize extends JavaPlugin {

    @Getter private static StackResize instance;
    @Getter private final Set<Material> unstackableTools = loadRegexList("unstackable-tools");
    @Getter private final Set<Material> unstackableWearables = loadRegexList("unstackable-wearables");
    @Getter private final Map<Material,Integer> defaultStackSizes = new HashMap<>();

    private final File stackFile = new File(getDataFolder(),"max-stack-sizes.yml");
    private final YamlConfiguration stackYaml = new YamlConfiguration();

    {
        instance = this;
        JeffLib.init(this);
    }

    @Override
    public void onEnable() {
        if(isUnsupportedVersion()) return;
        registerCommand();
        saveDefaultStackSizes();
        loadConfigAndSetStackSizes();
        JeffLib.registerArmorEquipEvent();
        getServer().getPluginManager().registerEvents(new DisappearingItemListener(), this);
        getServer().getPluginManager().registerEvents(new ToolListener(), this);
        getServer().getPluginManager().registerEvents(new FurnaceListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryUpdateListener(), this);
        getServer().getPluginManager().registerEvents(new ArmorEquipListener(), this);
    }

    @Override
    public void onDisable() {
        resetStackSizesToDefault();
    }

    public boolean isChanged(Material material) {
        return material.getMaxStackSize() != getDefaultStackSizes().get(material);
    }

    private void resetStackSizesToDefault() {
        Arrays.stream(Material.values()).forEach(mat -> {
            if(!mat.isItem()) return;
            int defaultStackSize = getDefaultStackSizes().getOrDefault(mat,mat.getMaxStackSize());
            if(mat.getMaxStackSize() == defaultStackSize) return;
            MaterialUtils.setMaxStackSize(mat, defaultStackSize);
        });
    }

    private void saveDefaultStackSizes() {
        Arrays.stream(Material.values()).forEach(mat -> defaultStackSizes.put(mat, mat.getMaxStackSize()));
    }

    private boolean isUnsupportedVersion() {
        if(JeffLib.getNMSHandler() == null) {
            getLogger().severe("Your version of Minecraft is currently not supported.");
            getLogger().severe("Supported versions are: 1.16.1 and above.");
            getLogger().severe("Do not request support for older versions. It won't happen.");
            Bukkit.getPluginManager().disablePlugin(this);
            return true;
        }
        return false;
    }

    public void loadConfigAndSetStackSizes() {
        saveDefaultConfig();
        updateConfigFiles();
        reloadConfig();
        Config.init(getConfig());
        setupStackYaml();
        setStackSizes();

    }

    private void updateConfigFiles() {
        try {
            new ConfigUpdater(this, "config.yml","config-update.yml").update();
        } catch (IOException e) {
            getLogger().warning("Could not update config.yml file:");
            e.printStackTrace();
        }
    }

    private void setupStackYaml() {
        if(!stackFile.exists()) {
            saveResource("max-stack-sizes.yml",true);
        }
        try {
            stackYaml.load(stackFile);
            stackYaml.options().pathSeparator('°');
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Could not load max-stack-sizes.yml! Check your YAML syntax!");
            e.printStackTrace();
        }
        stackYaml.options().header(getConfigHeader());
        stackYaml.options().copyHeader(true);
    }

    private String getConfigHeader() {
        try (InputStream inputStream = getResource("max-stack-sizes.header");
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))
        ) {
            return reader.lines()
                    .map(line -> line.replaceFirst("^# ",""))
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException ignored) {

        }
        return "";
    }

    public void changeStackSize(Material material, int amount) {
        stackYaml.set("static°" + material.name(),amount);
        saveAsync();
        setStackSizes();
    }

    private void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                stackYaml.options().pathSeparator('°');
                stackYaml.save(stackFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isForcefullyUnstackable(Material mat) {
        return unstackableTools.contains(mat) || unstackableWearables.contains(mat);
    }

    private void setStackSizes() {
        Map<Material,Integer> map = new HashMap<>();
        stackYaml.options().pathSeparator('°');

        // Insert static entries
        if(stackYaml.isConfigurationSection("static")) {
            stackYaml.getConfigurationSection("static").getKeys(false).forEach(key -> {
                Material mat = Enums.getIfPresent(Material.class, key.toUpperCase(Locale.ROOT)).orNull();
                if (mat == null) {
                    getLogger().warning("Invalid material defined in static list: " + key);
                    return;
                }
                if(isForcefullyUnstackable(mat)) {
                    getLogger().warning("Sorry, cannot make " + mat.name() + " stackable.");
                    return;
                }
                map.put(mat, stackYaml.getInt("static°" + key));
            });
        }

        // Load regex entries
        Map<Pattern,Integer> regexMap = new HashMap<>();
        if(stackYaml.isConfigurationSection("regex")) {
            stackYaml.getConfigurationSection("regex").getKeys(true).forEach(regex -> regexMap.put(Pattern.compile(regex), stackYaml.getInt("regex°" + regex)));
        }

        // Resolve regex entries
        Arrays.stream(Material.values()).forEachOrdered((mat) -> {
            if(map.containsKey(mat)) return;
            regexMap.forEach((key, value) -> {
                if (key.matcher(mat.name()).matches()) {
                    if(isForcefullyUnstackable(mat)) {
                        getLogger().warning("Sorry, cannot make " + mat.name() + " stackable (matched by regex \"" + key + "\").");
                        return;
                    }
                    map.put(mat, value);
                }
            });
        });

        /*
        Finally apply values. Also loop over the default values so stack sizes will be reset to default
        when you reload the config after removing an item. Falls back to default value when an invalid
        stack size is specified.
         */
        Arrays.stream(Material.values()).forEach(mat -> {
            if(!mat.isItem()) return; // Cannot change unobtainable items
            int defaultValue = defaultStackSizes.get(mat);
            //System.out.println(mat + ": " + defaultValue);
            if(defaultValue == 0) return;
            int value = map.getOrDefault(mat,defaultValue);
            if (value < 1) {
                getLogger().warning("Cannot set max stack size of " + mat.name() + " to a value below 1!");
                value = defaultValue;
            } else if (value > 64) {
                getLogger().warning("Cannot set max stack size of " + mat.name() + " to a value above 64!");
                value = defaultValue;
            }
            //getLogger().info("Stack size for " + mat.name() + " set to " + value);
            MaterialUtils.setMaxStackSize(mat, value);
        });
    }

    public Set<Material> loadRegexList(String name) {
        Set<Material> set = new HashSet<>();
        try(InputStream input = getResource("regex/" + name + ".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            List<String> linesInFile = reader.lines().collect(Collectors.toList());
            reader.lines().forEach(regex -> {
                Pattern pattern = Pattern.compile(regex);
                Arrays.stream(Material.values()).forEach(mat -> {
                    if(pattern.matcher(mat.name()).matches()) {
                        set.add(mat);
                    }
                });
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return set;
    }

    private void registerCommand() {
        PaperCommandManager acf = new PaperCommandManager(this);
        acf.registerCommand(new MainCommand());
        acf.getCommandCompletions().registerStaticCompletion("materials", Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(Material::name)
                .collect(Collectors.toList()));
    }
}
