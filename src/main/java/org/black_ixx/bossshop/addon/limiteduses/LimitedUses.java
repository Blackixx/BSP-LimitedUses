package org.black_ixx.bossshop.addon.limiteduses;

import org.black_ixx.bossshop.api.BossShopAddon;
import org.bukkit.command.CommandSender;

public class LimitedUses extends BossShopAddon {

    private BSListener listener;
    private LimitedUsesManager manager;

    @Override
    public String getAddonName() {
        return "LimitedUses";
    }

    @Override
    public String getRequiredBossShopVersion() {
        return "1.7.0";
    }

    @Override
    public void enableAddon() {
        getCommand("limiteduses").setExecutor(new CommandManager(this));
        manager = new LimitedUsesManager(this);
        listener = new BSListener(this, manager);
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void bossShopFinishedLoading() {
        listener.enable();
    }

    @Override
    public void disableAddon() {
        listener.disable(); //includes saving
    }

    @Override
    public void bossShopReloaded(CommandSender sender) {
        listener.disable(); //includes saving
        listener.enable();
    }

    public LimitedUsesManager getLimitedUsesManager() {
        return manager;
    }
}