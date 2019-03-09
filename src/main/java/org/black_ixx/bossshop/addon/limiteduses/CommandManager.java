package org.black_ixx.bossshop.addon.limiteduses;


import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShop;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {

    private LimitedUses plugin;


    public CommandManager(LimitedUses plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (plugin.getLimitedUsesManager() != null) {
            if (sender.hasPermission("LimitedUses.Modify")) {

                if (args.length >= 1) {
                    String arg = args[0];

                    //Reset all
                    if (arg.equalsIgnoreCase("resetall")) {
                        plugin.getLimitedUsesManager().resetAll();
                        sender.sendMessage(ChatColor.GRAY + "The whole LimitedUses storage file has been reset (A backup has been created in the plugin folder).");
                        return true;
                    }

                    if (args.length >= 2) {
                        String target = args[1];
                        Player t = Bukkit.getPlayer(target);

                        if (t == null) {
                            sender.sendMessage(ChatColor.RED + "Target " + target + " not found!");
                            return false;
                        }

                        //Reset player
                        if (arg.equalsIgnoreCase("reset")) {
                            if (args.length == 2) {
                                plugin.getLimitedUsesManager().resetPlayer(t);
                                sender.sendMessage(ChatColor.GRAY + "The shopitem consume count and cooldowns of player " + ChatColor.GOLD + t.getName() + ChatColor.GRAY + " have been reset.");
                                return true;
                            }
                        }


                        if (args.length >= 4) {
                            BSShop shop = ClassManager.manager.getShops().getShop(args[2]);
                            if (shop == null) {
                                sender.sendMessage(ChatColor.RED + "Shop '" + args[2] + "' not found!");
                                return false;
                            }

                            BSBuy shopitem = shop.getItem(args[3]);
                            if (shopitem == null) {
                                sender.sendMessage(ChatColor.RED + "Shopitem '" + args[3] + "' not found!");
                                return false;
                            }


                            //Reset player shopitem
                            if (arg.equalsIgnoreCase("reset")) {
                                plugin.getLimitedUsesManager().resetShopItem(t, shop, shopitem);
                                sender.sendMessage(ChatColor.GRAY + "Player uses and cooldowns reset. Player: " + ChatColor.GOLD + t.getName() + ChatColor.GRAY + ". Shopitem: " + ChatColor.GOLD + shopitem.getName() + " (" + shop.getShopName() + ")" + ChatColor.GRAY + ".");
                                return true;
                            }


                            if (args.length >= 5) {
                                long count = InputReader.getInt(args[4], -1);

                                if (count == -1) {
                                    sender.sendMessage(ChatColor.RED + "Invalid count: '" + args[4] + "'!");
                                    return false;
                                }


                                //Set player shopitem uses
                                if (arg.equalsIgnoreCase("set")) {
                                    plugin.getLimitedUsesManager().setUses(t, shop, shopitem, count);
                                    sender.sendMessage(ChatColor.GRAY + "Player uses modified. Player: " + ChatColor.GOLD + t.getName() + ChatColor.GRAY + ". Shopitem: " + ChatColor.GOLD + shopitem.getName() + " (" + shop.getShopName() + ")" + ChatColor.GRAY + ". Set to " + ChatColor.GOLD + count + ChatColor.GRAY + ".");
                                    return true;
                                }

                                //Add player shopitem uses
                                if (arg.equalsIgnoreCase("add")) {
                                    plugin.getLimitedUsesManager().setUses(t, shop, shopitem, plugin.getLimitedUsesManager().detectUsedAmount(t, shop, shopitem) + count);
                                    sender.sendMessage(ChatColor.GRAY + "Player uses modified. Player: " + ChatColor.GOLD + t.getName() + ChatColor.GRAY + ". Shopitem: " + ChatColor.GOLD + shopitem.getName() + " (" + shop.getShopName() + ")" + ChatColor.GRAY + ". Added " + ChatColor.GOLD + count + ChatColor.GRAY + ".");
                                    return true;
                                }

                                //Remove player shopitem uses
                                if (arg.equalsIgnoreCase("remove")) {
                                    plugin.getLimitedUsesManager().setUses(t, shop, shopitem, plugin.getLimitedUsesManager().detectUsedAmount(t, shop, shopitem) + count);
                                    sender.sendMessage(ChatColor.GRAY + "Player uses modified. Player: " + ChatColor.GOLD + t.getName() + ChatColor.GRAY + ". Shopitem: " + ChatColor.GOLD + shopitem.getName() + " (" + shop.getShopName() + ")" + ChatColor.GRAY + ". Removed " + ChatColor.GOLD + count + ChatColor.GRAY + ".");
                                    return true;
                                }
                            }

                        }

                    }

                }

                sender.sendMessage(ChatColor.GOLD + "[LimitedUses]");
                sender.sendMessage(ChatColor.GRAY + "- lius set <player> <shop> <shopitem> <count> - Set uses");
                sender.sendMessage(ChatColor.GRAY + "- lius add <player> <shop> <shopitem> <count> - Add uses");
                sender.sendMessage(ChatColor.GRAY + "- lius remove <player> <shop> <shopitem> <count> - Remove uses");
                sender.sendMessage(ChatColor.RED + "- lius reset <player> <shop> <shopitem> - Reset cooldown and uses");
                sender.sendMessage(ChatColor.RED + "- lius reset <player> - Reset cooldown and uses of all items");
                sender.sendMessage(ChatColor.RED + "- lius resetall - Reset all data of all players");

                return false;
            } else {
                ClassManager.manager.getMessageHandler().sendMessage("Main.NoPermission", sender);
                return false;
            }
        }


        return false;
    }


}
