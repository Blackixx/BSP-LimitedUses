package org.black_ixx.bossshop.addon.limiteduses;

import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShopHolder;
import org.black_ixx.bossshop.core.conditions.BSConditionTypeNumber;
import org.bukkit.entity.Player;

public class BSConditionTypeCooldown extends BSConditionTypeNumber {

    private LimitedUsesManager manager;

    public BSConditionTypeCooldown(LimitedUsesManager manager) {
        this.manager = manager;
    }

    @Override
    public double getNumber(BSBuy shopitem, BSShopHolder holder, Player p) {
        return manager.detectLastUseDelay(p, shopitem.getShop(), shopitem) / 1000;
    }

    @Override
    public boolean dependsOnPlayer() {
        return true;
    }

    @Override
    public String[] createNames() {
        return new String[]{"cooldown", "delay"};
    }


    @Override
    public void enableType() {
    }


}
