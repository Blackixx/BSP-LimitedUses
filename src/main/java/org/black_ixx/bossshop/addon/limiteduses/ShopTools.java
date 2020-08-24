package org.black_ixx.bossshop.addon.limiteduses;

import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.conditions.BSCondition;
import org.black_ixx.bossshop.core.conditions.BSConditionSet;
import org.black_ixx.bossshop.core.conditions.BSConditionType;
import org.black_ixx.bossshop.core.conditions.BSSingleCondition;
import org.black_ixx.bossshop.managers.misc.InputReader;

public class ShopTools {

    public static BSSingleCondition getCondition(BSCondition condition, String conditiontype) {
        if (condition == null)
            return null;

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
        return null;
    }

    public static boolean hasConditionUses(BSBuy buy) {
        BSCondition condition = buy.getCondition();
        return getCondition(condition, "uses") != null;
    }

    public static boolean hasConditionCooldown(BSBuy buy) {
        BSCondition condition = buy.getCondition();
        return getCondition(condition, "cooldown") != null;
    }

    public static long getTimeToWait(BSBuy buy) {
        BSSingleCondition condition = ShopTools.getCondition(buy.getCondition(), "cooldown");
        if (condition != null) {
            if (condition.getConditionType().equalsIgnoreCase(">") || condition.getConditionType().equalsIgnoreCase("over")) {
                return InputReader.getInt(condition.getCondition(), 0) * 1000;
            }
        }
        return 0;
    }
}