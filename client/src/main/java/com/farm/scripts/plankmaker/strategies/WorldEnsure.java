package com.farm.scripts.plankmaker.strategies;

import com.farm.ibot.api.accessors.Client;
import com.farm.ibot.api.accessors.Player;
import com.farm.ibot.api.methods.Dialogue;
import com.farm.ibot.api.methods.Varbit;
import com.farm.ibot.api.methods.WorldHopping;
import com.farm.ibot.api.methods.entities.Widgets;
import com.farm.ibot.api.methods.walking.WebWalking;
import com.farm.ibot.api.util.ScriptUtils;
import com.farm.ibot.api.util.WebUtils;
import com.farm.ibot.api.wrapper.Tile;
import com.farm.ibot.core.Bot;
import com.farm.ibot.core.script.Strategy;
import com.farm.ibot.init.AccountData;
import com.farm.scripts.plankmaker.Plankmaker;
import com.farm.scripts.plankmaker.Strategies;

public class WorldEnsure extends Strategy {
    public static boolean ensureMembership() {
        if (Client.isInGame() && !Varbit.MEMBERSHIP_DAYS.booleanValue()) {
            AccountData.current().autostartScript = "&script=FlaxMembershipReady";
            WebUtils.downloadObject(AccountData.class, "http://api.hax0r.farm:8080/accounts/bind/?username=" + AccountData.current().username + "&script=FlaxMembershipReady");
            Bot.get().getSession().setAccount((AccountData) null);
            Plankmaker.get().getScriptHandler().stop();
            return false;
        } else {
            return true;
        }
    }

    protected void onAction() {
        if (Strategies.forceTrade.intValue() == 1) {
            Strategies.muleManager.itemsToKeep = null;
            Strategies.muleManager.wealthToKeep = 0;
            Strategies.muleManager.wealthToGive = 1000;
            Strategies.muleManager.supplyFromMuleActionId = -1;
            if (WorldHopping.isF2p(Client.getCurrentWorld()) || Strategies.muleManager != null && WorldHopping.toRegularWorldNumber(Client.getCurrentWorld()) == WorldHopping.toRegularWorldNumber(Strategies.muleManager.tradeOnWorldGive)) {
                this.hopWorld();
            }

        } else if (ensureMembership()) {
            if (WorldHopping.isF2p(Client.getCurrentWorld()) || Strategies.muleManager != null && WorldHopping.toRegularWorldNumber(Client.getCurrentWorld()) == WorldHopping.toRegularWorldNumber(Strategies.muleManager.tradeOnWorldGive)) {
                this.hopWorld();
            }

        }
    }

    private void hopWorld() {
        ScriptUtils.interruptCurrentLoop();
        if (Dialogue.isInDialouge()) {
            WebWalking.walkTo(Player.getLocal().getPosition(), -1, new Tile[0]);
        } else {
            Widgets.closeTopInterface();
            WorldHopping.hop(WorldHopping.getRandomP2p());
        }
    }
}
