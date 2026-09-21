package de.fuchsmod.features.partycommands;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.network.chat.Component;

import static de.fuchsmod.FuchsMod.CONFIG;

@SuppressWarnings("UnstableApiUsage")
public class PartyCommandsTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.runOnClient(client -> {
                CONFIG.enablePartyCommands = true;
                CONFIG.commandDelay = 500;
            });
            singleplayer.getConnection().waitForChunksRender();

            String[] commands = {
                    "Party > Player: !warp",
                    "Party > [VIP] Player: !ptme",
                    "Party > [VIP+] Player: !f7",
                    "Party > [MVP] Player: !m5",
                    "Party > [MVP+] Player: !t4",
                    "Party > [MVP++] Player: !dice",
                    "Party > Player: !dice 10 100",
                    "Party > [VIP] Player: !tps",
                    "Party > [VIP+] Player: !fps",
                    "Party > [MVP] Player: !ping",
                    "Party > [MVP+] Player: !invite Player",
                    "Party > [MVP++] Player: !kick Player",
                    "Party > Player: !kickoffline",
                    "Party > [VIP] Player: !promote Player",
                    "Party > [VIP+] Player: !demote Player",
                    "Party > [MVP] Player: !allinv Player",
                    "Party > [MVP+] Player: !b8x2",
                    "Party > [MVP++] Player: !b4x3",
                    "Party > Player: !b4v4",
                    "Party > [VIP] Player: !b4x4d",
                    "Party > [VIP+] Player: !mc",
                    "Party > [MVP] Player: !ma",
                    "Guild > [MVP] Player: !warp",
                    "Player: !warp",
            };
            for (String command : commands) {
                context.waitTicks(30);
                singleplayer.getServer().runOnServer(server -> {
                    server.getPlayerList().broadcastSystemMessage(Component.literal(command), false);
                });
            }
            context.waitTicks(100);
        }
    }
}
