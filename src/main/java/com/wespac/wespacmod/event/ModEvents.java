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
import com.wespac.wespacmod.chat.GeminiAPI;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = WespacMod.MODID)
public class ModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

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
                    player.sendSystemMessage(Component.literal("Hello! My name is Westpac Steve, I'm a bank teller! I have a PhD in Finance at UQ so ask me anything... it can be about your bank account, any finance-related questions, instruction, or just anything!"));
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
                .url(GeminiAPI.API_URL + "/chat")
                .post(body)
                .build();
        LOGGER.info(request.toString());

        GeminiAPI.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //player.sendSystemMessage(Component.literal("Failed to connect to the chatbot."));
                LOGGER.error("Failed to connect to the chatbot", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    player.sendSystemMessage(Component.literal(responseBody));
                } else {
                    //player.sendSystemMessage(Component.literal("The chatbot is unavailable at the moment."));
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

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(ModItems.MONEY_ITEMS.get("ten_dollar_note").get(), 1),
                    5, 12, 0.02f));


            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("five_dollar_note").get(), 1),
                    new ItemStack(Items.BAKED_POTATO, 1),
                    16, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("one_dollar_coin").get(), 1),
                    new ItemStack(Items.APPLE, 1),
                    16, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("twenty_dollar_note").get(), 1),
                    new ItemStack(Items.BEEF, 1),
                    16, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("hundred_dollar_note").get(), 3),
                    new ItemStack(Items.DIAMOND, 1),
                    16, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("fifty_dollar_note").get(), 3),
                    new ItemStack(Items.GOLD_INGOT, 1),
                    16, 8, 0.02f));


            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("ten_dollar_note").get(), 1),
                    new ItemStack(Items.WATER_BUCKET, 1),
                    16, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("fifty_cent_coin").get(), 3),
                    new ItemStack(Items.TORCH, 1),
                    16, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("two_dollar_coin").get(), 2),
                    new ItemStack(Items.SUNFLOWER, 1),
                    16, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("ten_cent_coin").get(), 1),
                    new ItemStack(Items.AIR, 1),

                    16, 8, 0.02f));
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("five_cent_coin").get(), 3),
                    new ItemStack(Items.BLUE_DYE, 1),
                    16, 8, 0.02f));
            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("hundred_dollar_note").get(), 3),
                    new ItemStack(Items.MAGENTA_BED, 1),
                    16, 8, 0.02f));

            trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.MONEY_ITEMS.get("twenty_dollar_note").get(), 3),
                    new ItemStack(Items.WHITE_WOOL, 1),
                    16, 8, 0.02f));
        }

    }
}
