package org.black_ixx.bossshop.addon.limiteduses;


import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.conditions.BSCondition;
import org.black_ixx.bossshop.core.conditions.BSConditionSet;
import org.black_ixx.bossshop.core.conditions.BSConditionType;
import org.black_ixx.bossshop.core.conditions.BSSingleCondition;
import org.black_ixx.bossshop.events.BSCheckStringForFeaturesEvent;
import org.black_ixx.bossshop.events.BSPlayerPurchasedEvent;
import org.black_ixx.bossshop.events.BSRegisterTypesEvent;
import org.black_ixx.bossshop.events.BSTransformStringEvent;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.black_ixx.bossshop.managers.misc.StringManipulationLib;
import org.black_ixx.bossshop.misc.TimeTools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BSListener implements Listener {

    private final LimitedUses plugin;
    private final LimitedUsesManager manager;

    public BSListener(LimitedUses plugin, LimitedUsesManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void enable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            manager.loadPlayer(p);
        }
    }

    public void disable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            manager.unloadPlayer(p, true, false);
        }
        manager.save();
        manager.unloadAll();
    }

    @EventHandler
    public void onRegisterTypes(BSRegisterTypesEvent e) {
        new BSConditionTypeUses(manager).register();
        new BSConditionTypeCooldown(manager).register();
    }

    @EventHandler
    public void onItemPurchased(BSPlayerPurchasedEvent e) {
        boolean b = false;
        if (hasConditionUses(e.getShopItem())) {
            manager.progressUse(e.getPlayer(), e.getShop(), e.getShopItem());
            b = true;
        }

        if (hasConditionCooldown(e.getShopItem())) {
            manager.progressCooldown(e.getPlayer(), e.getShop(), e.getShopItem());
            b = true;
        }

        if (b) {
            plugin.getBossShop().getAPI().updateInventory(e.getPlayer());
        }
    }

    @EventHandler
    public void transformString(BSTransformStringEvent event) {
        Player p = event.getTarget();
        if (p != null && event.getShop() != null && event.getShopItem() != null) {

            String text = event.getText();

            if (text.contains("%uses%")) {
                long uses = manager.detectUsedAmount(p, event.getShop(), event.getShopItem());
                text = text.replace("%uses%", String.valueOf(uses));
            }
            if (text.contains("%uses_")) {
                String variable = StringManipulationLib.figureOutVariable(text, "uses", 0);
                long uses = manager.detectUsedAmount(p, variable);
                text = text.replace("%uses_" + variable + "%", String.valueOf(uses));
            }

            if (text.contains("%cooldown_")) {
                String variable = StringManipulationLib.figureOutVariable(text, "cooldown", 0);
                BSBuy buy = manager.getShopItem(variable);
                if (buy != null) {
                    long time = manager.detectLastUseDelay(p, buy.getShop(), buy);
                    long time_to_wait = 0;
                    BSSingleCondition c = getCondition(buy.getCondition(), "cooldown");
                    if (c != null) {
                        if (c.getConditionType().equalsIgnoreCase(">") || c.getConditionType().equalsIgnoreCase("over")) {
                            time_to_wait = InputReader.getInt(c.getCondition(), 0) * 1000;
                        }
                    }
                    long time_left = time_to_wait - time;
                    text = text.replace("%cooldown_" + variable + "%", TimeTools.transform(Math.max(0, time_left / 1000)));
                }
            }

            event.setText(text);
        }
    }

    @EventHandler
    public void checkString(BSCheckStringForFeaturesEvent event) {
        String s = event.getText();
        if (s.contains("%uses%") || s.contains("%uses_") || s.contains("%cooldown_")) {
            event.approveFeature();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        manager.loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        manager.unloadPlayer(e.getPlayer(), true, false);
    }

    @EventHandler
    public void onKicked(PlayerKickEvent e) {
        if (e.isCancelled()) {
            return;
        }
        manager.unloadPlayer(e.getPlayer(), true, false);
    }

    public boolean hasConditionUses(BSBuy buy) {
        BSCondition condition = buy.getCondition();
        return getCondition(condition, "uses") != null;
    }

    public boolean hasConditionCooldown(BSBuy buy) {
        BSCondition condition = buy.getCondition();
        return getCondition(condition, "cooldown") != null;
    }

    private BSSingleCondition getCondition(BSCondition condition, String conditiontype) {
        if (condition != null) {
            if (condition instanceof BSConditionSet) {
                BSConditionSet set = (BSConditionSet) condition;
                for (BSCondition c : set.getConditions()) {
                    BSSingleCondition subcondition = getCondition(c, conditiontype);
                    if (subcondition != null) {
                        return subcondition;
                    }
                }
            } else {
                if (condition instanceof BSSingleCondition) {
                    BSSingleCondition c = (BSSingleCondition) condition;
                    if (c.getType() == BSConditionType.detectType(conditiontype)) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
}