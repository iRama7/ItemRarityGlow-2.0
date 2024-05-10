package rama.spigot.itemrarityglow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import rama.spigot.itemrarityglow.util.Color;
import rama.spigot.itemrarityglow.util.NBTMain;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ItemRarityGlow extends JavaPlugin {

    private Boolean NBTHook = false;
    private NBTMain nbtMain;

    private File colorsFile;
    private FileConfiguration colorsConfig;
    private GlowManager glowManager;
    private Scoreboard scoreboard;

    @Override
    public void onEnable() {

        //Checking for nbtapi before starting
        initNBTAPI();
        if(!NBTHook){
            log("&4&lERROR &cITEM-NBT-API not found!", true, null);
            log("&4&lERROR &cDisabling plugin.", true, null);
            Bukkit.getPluginManager().disablePlugin(this);
        }

        nbtMain = new NBTMain();
        loadConfig();
        loadColorsConfiguration();

        scoreboard = getServer().getScoreboardManager().getNewScoreboard();

        glowManager = new GlowManager(this, scoreboard);

        registerEvents();

        //Load colors
        log("&aLoaded &f" + initializeColors(colorsConfig) + " &acolors.", true, null);
        initializeItems(this.getConfig());

        glowManager.buildRainbowHashMap();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void initNBTAPI(){
        NBTHook = Bukkit.getPluginManager().getPlugin("NBTAPI") != null;
    }

    public void log(String s, boolean debug, Player p){

        String prefix = colorized("&c[&3Item Rarity Glow&c]");

        if(debug){
            Bukkit.getConsoleSender().sendMessage(prefix + " " + colorized(s));
        }else{
            p.sendMessage(colorized(s));
        }
    }

    public String colorized(String s){
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String hexCode = s.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }

            s = s.replace(hexCode, builder.toString());
            matcher = pattern.matcher(s);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public NBTMain getNbtMain() {
        return nbtMain;
    }


    //Loads all colors from config and add them to GlowManager.
    private int initializeColors(FileConfiguration file){
        int count = 0;
        for(String i : file.getConfigurationSection("Colors").getKeys(false)){
            /*
              1:
                identifier: White
                weight: 1
                color: "WHITE"
                type: STATIC #FLASHY - STATIC - RAINBOW
             */

            String identifier = file.getString("Colors." + i + ".identifier");
            int weight = file.getInt("Colors." + i + ".weight");
            String colorString = file.getString("Colors." + i + ".color");
            String type = file.getString("Colors." + i + ".type");

            try{
                ChatColor.valueOf(colorString);
            }catch (IllegalArgumentException | NullPointerException e){
                log("&4&lERROR &eInvalid color (&f" + colorString + "&e) in Colors file number &f" + i + "&e.", true, null);
                continue;
            }

            Color color = new Color(this, identifier, weight, type, colorString, scoreboard);
            glowManager.addColor(color);
            count++;
        }
        return count;
    }

    //Add all items from config to each color stored in GlowManager.
    private void initializeItems(FileConfiguration file){
        for(String identifier : file.getConfigurationSection("Items").getKeys(false)){

            Color color = glowManager.getColor(identifier);

            if(color != null){

                for(String materialString : file.getStringList("Items." + identifier + ".list")){

                    if(materialString.equals("DEFAULT")){
                        for(Material material : Material.values()){
                            color.addMaterial(material);
                        }
                        continue;
                    }
                    Material material;

                    try{
                        material = Material.valueOf(materialString);
                    }catch(IllegalArgumentException | NullPointerException e){
                        log("&4&lERROR &eInvalid Material (&f" + materialString + "&e) in Config file identifier &f" + identifier + "&e.", true, null);
                        continue;
                    }

                    color.addMaterial(material);

                }

            }

        }
    }

    //Loads colors file configuration.
    private void loadColorsConfiguration() {
        colorsFile = new File(getDataFolder(), "colors.yml");
        if (!colorsFile.exists()) {
            colorsFile.getParentFile().mkdirs();
            saveResource("colors.yml", false);
        }
        colorsConfig = new YamlConfiguration();
        try {
            colorsConfig.load(colorsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig(){
        this.saveDefaultConfig();
    }

    private void registerEvents(){
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(glowManager, this);
    }

    public boolean debug(){
        return getConfig().getBoolean("Debug");
    }


}
