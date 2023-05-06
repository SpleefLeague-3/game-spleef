package com.spleefleague.spleef.game.effects;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.projectile.global.GlobalWorld;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 * @since 3/1/2021
 */
public class EffectSnowBlizzard {

    private final GlobalWorld globalWorld;
    private final Player player;

    public EffectSnowBlizzard(GlobalWorld globalWorld, CorePlayer corePlayer) {
        this.globalWorld = globalWorld;
        this.player = corePlayer.getPlayer();
    }

    public void tick() {
        
    }

}
