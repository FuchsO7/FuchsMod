package de.fuchsmod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.chat.GuiMessage;

public class ChatEvents {

    public static final Event<Message> MESSAGE = EventFactory.createArrayBacked(Message.class, callbacks -> message -> {
        for (Message event : callbacks) {
            event.onMessage(message);
        }
    });

    @FunctionalInterface
    public interface Message {
        void onMessage(GuiMessage message);
    }
}
