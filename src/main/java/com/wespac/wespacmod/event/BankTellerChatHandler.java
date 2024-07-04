// BankTellerChatHandler.java
package com.wespac.wespacmod.event;

import com.wespac.wespacmod.villager.ModVillagers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import okhttp3.*;

import java.io.IOException;

@Mod.EventBusSubscriber
public class BankTellerChatHandler {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://api.openai.com/v1/engines/davinci-codex/completions";
    private static final String API_KEY = "your-api-key";

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof Villager) {
            Villager villager = (Villager) event.getTarget();

            if (villager.getVillagerData().getProfession() == ModVillagers.BANK_TELLER.get()) {
                if (event.getEntity() instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer) event.getEntity();
                    player.sendSystemMessage(Component.literal("Hello! How can I assist you today?"));

                    // Send request to ChatGPT API
                    sendChatMessage("Hello! How can I assist you today?", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            player.sendSystemMessage(Component.literal("Failed to connect to the chatbot."));
                        }

                        @Override
                        public void onResponse(Call call, Response
                                response) throws IOException {
                            if (response.isSuccessful()) {
                                String responseBody = response.body().string();
                                // Parse and handle the response
                                player.sendSystemMessage(Component.literal(responseBody));
                            } else {
                                player.sendSystemMessage(Component.literal("The chatbot is unavailable at the moment."));
                            }
                        }
                    });
                }
            }
        }
    }

    public static void sendChatMessage(String message, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("prompt", message)
                .add("max_tokens", "150")
                .build();

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
