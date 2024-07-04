package com.wespac.wespacmod.event;


import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.block.ModBlocks;
import com.wespac.wespacmod.item.ModItems;
import com.wespac.wespacmod.villager.ModVillagers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

@Mod.EventBusSubscriber(modid = WespacMod.MODID)
public class ModEvents {

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

        if(event.getType() == ModVillagers.BANK_TELLER.get()) {
            LocalPlayer player = Minecraft.getInstance().player;
            Component message = Component.literal("Hello!");

            if (player != null){
                player.sendSystemMessage(message);
            }

            BankTellerChatHandler.sendChatMessage("Hello! How can I assist you today?", new Callback() {
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