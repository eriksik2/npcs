package com.example.examplemod;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.OpenEncyclopedia;

import net.minecraftforge.client.event.InputEvent;

public class KeyInputHandler {
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.openExplorerKeyMapping.consumeClick()) {
            Messages.sendToServer(new OpenEncyclopedia());
        }
    }
}
