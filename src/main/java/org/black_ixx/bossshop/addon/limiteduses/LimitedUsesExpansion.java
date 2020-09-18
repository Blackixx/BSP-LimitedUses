package org.black_ixx.bossshop.addon.limiteduses;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.misc.TimeTools;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LimitedUsesExpansion extends PlaceholderExpansion {

    private final LimitedUses plugin;
    private final LimitedUsesManager manager;

    public LimitedUsesExpansion(LimitedUses plugin) {
        this.plugin = plugin;
        this.manager = plugin.getLimitedUsesManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "limiteduses";
    }

    @Override
    public @NotNull String getAuthor() {
        return "black_ixx (BS author), Wertik1206";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        if (player == null) return "no_player";

        String[] args = params.split("_");

        if (args.length == 0) return "not_enough_args";

        if (args[0].equalsIgnoreCase("uses")) {
            if (args.length < 2)
                return "not_enough_args";

            long uses = manager.detectUsedAmount(player, args[1]);
            return String.valueOf(uses);
        } else if (args[0].equalsIgnoreCase("cooldown")) {
            if (args.length < 2)
                return "not_enough_args";

            BSBuy buy = manager.getShopItem(args[1]);

            if (buy == null) return "invalid_item";

            long time = manager.detectLastUseDelay(player, buy.getShop(), buy);
            long time_to_wait = ShopTools.getTimeToWait(buy);

            long time_left = time_to_wait - time;
            return TimeTools.transform(Math.max(0, time_left / 1000));
        } else if (args[0].equalsIgnoreCase("hascooldown")) {

            if (args.length < 2) return "not_enogh_args";

            BSBuy buy = manager.getShopItem(args[1]);

            if (buy == null) return "invalid_item";

            long time = manager.detectLastUseDelay(player, buy.getShop(), buy);
            long time_to_wait = ShopTools.getTimeToWait(buy);

            long time_left = time_to_wait - time;
            return time_left <= 0 ? "no" : "yes";
        }

        return "invalid_params";
    }
}