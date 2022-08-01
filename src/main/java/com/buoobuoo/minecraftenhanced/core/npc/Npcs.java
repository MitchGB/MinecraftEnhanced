package com.buoobuoo.minecraftenhanced.core.npc;

import com.buoobuoo.minecraftenhanced.core.npc.impl.JaydenNpc;
import lombok.Getter;

@Getter
public enum Npcs {

    Jayden(new JaydenNpc());

    private Npc handler;

    Npcs(Npc handler) {
        this.handler = handler;
    }
}