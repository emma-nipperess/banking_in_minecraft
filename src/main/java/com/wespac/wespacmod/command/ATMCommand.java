package com.wespac.wespacmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.wespac.wespacmod.item.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ATMCommand {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "http://10.89.247.191:9000/deposit-money";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("deposit")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            String message = StringArgumentType.getString(context, "message");
                            CommandSourceStack source = context.getSource();
                            if (source.getEntity() instanceof ServerPlayer) {
                                ServerPlayer player = (ServerPlayer) source.getEntity();

                                if (hasItem(player, new ItemStack(ModItems.MONEY_ITEMS.get(getItemNameMap().get(message)).get()))) {
                                    removeItem(player, new ItemStack(ModItems.MONEY_ITEMS.get(getItemNameMap().get(message)).get()), 1);
                                    sendChatMessage(getValueMap().get(message), player);
                                } else {
                                    player.sendSystemMessage(Component.literal("You don't have that!"));
                                }
                            }
                            return 1;
                        })
                )
        );
    }

    private static Map<String, String> getItemNameMap() {
        String[][] currencyNamesAndValues = {
                {"5 cent coin", "five_cent_coin"},
                {"10 cent coin", "ten_cent_coin"},
                {"20 cent coin", "twenty_cent_coin"},
                {"50 cent coin", "fifty_cent_coin"},
                {"1 dollar coin", "one_dollar_coin"},
                {"2 dollar coin", "two_dollar_coin"},
                {"5 dollar note", "five_dollar_note"},
                {"10 dollar note", "ten_dollar_note"},
                {"20 dollar note", "twenty_dollar_note"},
                {"50 dollar note", "fifty_dollar_note"},
                {"100 dollar note", "hundred_dollar_note"}
        };

        // Create the map to hold the mappings
        Map<String, String> currencyMap = new HashMap<>();

        // Fill the map with the mappings
        for (String[] currency : currencyNamesAndValues) {
            currencyMap.put(currency[0], currency[1]);
        }

        return currencyMap;
    }

    private static Map<String, String> getValueMap() {
        String[][] currencyNamesAndValues = {
                {"5 cent coin", "0.05"},
                {"10 cent coin", "0.10"},
                {"20 cent coin", "0.20"},
                {"50 cent coin", "0.50"},
                {"1 dollar coin", "1"},
                {"2 dollar coin", "2"},
                {"5 dollar note", "5"},
                {"10 dollar note", "10"},
                {"20 dollar note", "20"},
                {"50 dollar note", "50"},
                {"100 dollar note", "100"}
        };

        // Create the map to hold the mappings
        Map<String, String> currencyMap = new HashMap<>();

        // Fill the map with the mappings
        for (String[] currency : currencyNamesAndValues) {
            currencyMap.put(currency[0], currency[1]);
        }

        return currencyMap;
    }

    private static boolean hasItem(ServerPlayer player, ItemStack itemStack) {
        return player.getInventory().contains(itemStack);
    }

    private static void removeItem(ServerPlayer player, ItemStack itemStack, int count) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slotItem = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(slotItem, itemStack)) {
                slotItem.shrink(count);
                if (slotItem.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
                return;
            }
        }
    }

    private static void sendChatMessage(String message, ServerPlayer player) {
        RequestBody body = new FormBody.Builder()
                .add("amount", message)
                .build();

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                player.sendSystemMessage(Component.literal("Server's down please try again."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    player.sendSystemMessage(Component.literal(responseBody));
                } else {
                    player.sendSystemMessage(Component.literal("Server's down please try again."));
                }
            }
        });
    }
}
