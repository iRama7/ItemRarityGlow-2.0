package rama.spigot.itemrarityglow;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import rama.spigot.itemrarityglow.util.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commands implements TabExecutor {

    private ItemRarityGlow main;

    public Commands(ItemRarityGlow main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            main.log("&3[&eItemRarityGlow&3]", false, (Player) sender);
            main.log("&3 - &e/irg reload&3: &bReload config.yml&3.", false, (Player) sender);
            main.log("&3 - &e/irg add <Color>&3: &bAdd your holding item to a color&3.", false, (Player) sender);
            main.log("&3 - &e/irg remove <Material> <Color>&3: &bRemove item from a color&3.", false, (Player) sender);
            main.log("&3[&eItemRarityGlow&3]", false, (Player) sender);
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                main.reloadConfig();
                main.initializeItems(main.getConfig());
                main.log("&eReloaded items config.", false, (Player) sender);
            }
        }

        if(args.length == 2){
            if(args[0].equalsIgnoreCase("add")){
                Player p = (Player) sender;
                ItemStack holding = p.getInventory().getItemInMainHand();
                if(holding.getType() != Material.AIR){
                    String color = args[1];
                    Color c = main.getGlowManager().getColor(color);
                    if(c != null){
                        if(c.containsMaterial(holding.getType())){
                            main.log("&eMaterial &f" + holding.getType() + " &ealready exists in &f" + c.getIdentifier().toUpperCase(), false, p);
                        }else {
                            c.addMaterial(holding.getType());
                            List<String> materials = main.getConfig().getStringList("Items." + c.getIdentifier() + ".list");
                            materials.add(holding.getType().toString());
                            main.getConfig().set("Items." + c.getIdentifier() + ".list", materials);
                            main.saveConfig();
                            main.reloadConfig();
                            main.log("&aAdded &f" + holding.getType() + " &eto color &f" + c.getIdentifier().toUpperCase(), false, p);
                        }
                    }else{
                        main.log("&eColor is null.", false, p);
                    }
                }else{
                    main.log("&eYou're holding AIR!", false, p);
                }
            }
        }

        if(args.length == 3){
            if(args[0].equalsIgnoreCase("remove")){
                Player p = (Player) sender;
                Material material = Material.getMaterial(args[1]);
                if(material != null && material != Material.AIR){
                    String color = args[2];
                    Color c = main.getGlowManager().getColor(color);
                    if(c != null){
                        if(!c.containsMaterial(material)){
                            main.log("&eMaterial &f" + material + " &edoesn't exist in &f" + c.getIdentifier().toUpperCase(), false, p);
                        }else {
                            c.removeMaterial(material);
                            List<String> materials = main.getConfig().getStringList("Items." + c.getIdentifier() + ".list");
                            materials.remove(material.toString());
                            main.getConfig().set("Items." + c.getIdentifier() + ".list", materials);
                            main.saveConfig();
                            main.reloadConfig();
                            main.log("&aRemoved &f" + material + " &afrom color &f" + c.getIdentifier().toUpperCase(), false, p);
                        }
                    }else{
                        main.log("&eColor is null.", false, p);
                    }
                }else{
                    main.log("&eMaterial null or AIR!", false, p);
                }

            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if(args.length == 1) {
            if (sender.hasPermission("irg.admin")) {
                commands.add("reload");
                commands.add("add");
                commands.add("remove");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }
            Collections.sort(completions);
            return completions;

        }else if(args.length == 2 && args[0].equals("add")) {
            for(Color c : main.getGlowManager().getColors()) {
                commands.add(c.getIdentifier());
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
            Collections.sort(completions);
            return completions;
        }else if(args.length == 2 && args[0].equals("remove")){
            for(Material m : Material.values()){
                commands.add(m.toString());
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
            Collections.sort(completions);
            return completions;
        }else if(args.length == 3 && args[0].equals("remove")){
            for(Color c : main.getGlowManager().getColors()) {
                commands.add(c.getIdentifier());
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
            Collections.sort(completions);
            return completions;
        }


        return completions;

    }
}
