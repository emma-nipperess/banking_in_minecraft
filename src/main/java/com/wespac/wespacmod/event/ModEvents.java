package com.wespac.wespacmod.event;


import com.mojang.logging.LogUtils;
import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.item.ModItems;
import com.wespac.wespacmod.villager.ModVillagers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import okhttp3.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = WespacMod.MODID)
public class ModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "http://10.89.247.191:9000/chat";

    private static final Set<UUID> chattingPlayers = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget().getCommandSenderWorld().isClientSide) {
            return; // Only handle on the server side
        }

        if (event.getTarget() instanceof Villager) {
            Villager villager = (Villager) event.getTarget();
            if (villager.getVillagerData().getProfession() == ModVillagers.BANK_TELLER.get()) {
                if (event.getEntity() instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer) event.getEntity();
                    chattingPlayers.add(player.getUUID());
                    player.sendSystemMessage(Component.literal("You are now chatting with the bank teller. Type your message in the chat."));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        UUID playerUUID = player.getUUID();

        if (chattingPlayers.contains(playerUUID)) {
            //event.setCanceled(true); // Cancel the chat event to prevent it from being broadcast

            String message = event.getMessage().getString();
            LOGGER.info("RAW MESSAGE " + message);
            if (message.equals("Bye")) {
                chattingPlayers.remove(playerUUID); // Remove player from the chatting set
            } else {
                // Send message to ChatGPT API
                sendChatMessage(message, player);
            }
;
        }
    }


    private static void sendChatMessage(String message, ServerPlayer player) {

        RequestBody body = new FormBody.Builder()
                .add("prompt", message)
                .build();

        LOGGER.info("Sending message: {}", message);

        //RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
        LOGGER.info(request.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                player.sendSystemMessage(Component.literal("Failed to connect to the chatbot."));
                LOGGER.error("Failed to connect to the chatbot", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    player.sendSystemMessage(Component.literal(responseBody));
                } else {
                    player.sendSystemMessage(Component.literal("The chatbot is unavailable at the moment."));
                    LOGGER.warn("ChatGPT is unavailable: {}", response.message());
                }
            }
        });
    }


    /*
    Custom trades for Villager
     */
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if(event.getType() == ModVillagers.SOUND_MASTER.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            // villager trades
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 5),
                    new ItemStack(ModItems.MONEY_ITEMS.get("five_dollar_note").get(), 1),
                    16, 8, 0.02f));

            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(ModItems.MONEY_ITEMS.get("ten_dollar_note").get(), 1),
                    5, 12, 0.02f));
        }

    }
}
