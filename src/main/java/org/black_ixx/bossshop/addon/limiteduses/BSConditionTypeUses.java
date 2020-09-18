package org.black_ixx.bossshop.addon.limiteduses;

import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShopHolder;
import org.black_ixx.bossshop.core.conditions.BSConditionTypeNumber;
import org.bukkit.entity.Player;

public class BSConditionTypeUses extends BSConditionTypeNumber {

    private final LimitedUsesManager manager;

    public BSConditionTypeUses(LimitedUsesManager manager) {
        this.manager = manager;
    }

    @Override
    public double getNumber(BSBuy shopitem, BSShopHolder holder, Player p) {
        return manager.detectUsedAmount(p, shopitem.getShop(), shopitem);
    }

    @Override
    public boolean dependsOnPlayer() {
        return true;
    }

    @Override
    public String[] createNames() {
        return new String[]{"uses", "use", "consumes"};
    }

    @Override
    public void enableType() {
    }
}