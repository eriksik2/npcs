package com.example.examplemod;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindings {
    public static final String KEY_CATEGORIES_NPC_MOD = "key.categories.npc_mod";
    public static final String KEY_OPEN_EXPLORER = "key.open_encyclopedia";
    public static final String KEY_OPEN_TEAM_EDIT = "key.open_team_edit";

    public static KeyMapping openExplorerKeyMapping;
    public static KeyMapping openTeamEditMapping;

    public static void init(RegisterKeyMappingsEvent event) {
        // Use KeyConflictContext.IN_GAME to indicate this key is meant for usage in-game
        openExplorerKeyMapping = new KeyMapping(KEY_OPEN_EXPLORER, KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.o"), KEY_CATEGORIES_NPC_MOD);
        event.register(openExplorerKeyMapping);

        openTeamEditMapping = new KeyMapping(KEY_OPEN_TEAM_EDIT, KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.l"), KEY_CATEGORIES_NPC_MOD);
        event.register(openTeamEditMapping);
    }
}
