package com.buoobuoo.minecraftenhanced.core.item.impl;

import com.buoobuoo.minecraftenhanced.MinecraftEnhanced;
import com.buoobuoo.minecraftenhanced.core.ability.Ability;
import com.buoobuoo.minecraftenhanced.core.item.CustomItem;
import com.buoobuoo.minecraftenhanced.core.item.interfaces.NotStackable;
import com.buoobuoo.minecraftenhanced.core.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

@Getter
@Setter
public class AbilityGemItem extends CustomItem implements NotStackable {
    private Ability ability;

    public AbilityGemItem(Ability ability) {
        super("ABILITY_GEM", Material.PAPER, "", "");
        this.ability = ability;
        this.lore = ability == null ? null : ability.getLore();
        this.displayName = ability == null ? null : ability.getName();
    }

    @Override
    public void onCreate(MinecraftEnhanced plugin, ItemBuilder ib){
        ib.material(ability.getType().getMat().getMat());
        ib.setCustomModelData(ability.getType().getMat().getCustomModelData());
        ib.name(ability.getName());
        ib.lore(ability.getLore());
        ib.nbtString(plugin, "ABILITY_ID", ability.getId());
    }
}
