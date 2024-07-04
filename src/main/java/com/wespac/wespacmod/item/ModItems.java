package com.wespac.wespacmod.item;

import com.wespac.wespacmod.WespacMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModItems {
    public static DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WespacMod.MODID);

    // Map to hold the money items
    public static final Map<String, RegistryObject<Item>> MONEY_ITEMS = new HashMap<>();

    // Array of money item names
    private static final String[] MONEY_ITEM_NAMES = {
            "five_cent_coin", "ten_cent_coin", "twenty_cent_coin", "fifty_cent_coin",
            "one_dollar_coin", "two_dollar_coin", "five_dollar_note", "ten_dollar_note",
            "twenty_dollar_note", "fifty_dollar_note", "hundred_dollar_note", "open_account_form"
    };

    static {
        // Iteratively register money items
        for (String itemName : MONEY_ITEM_NAMES) {
            MONEY_ITEMS.put(itemName, ITEMS.register(itemName, () -> new Item(new Item.Properties())));
        }

        // Register the credit card with custom item class
        MONEY_ITEMS.put("credit_card", ITEMS.register("credit_card", () -> new CreditCardItem(new Item.Properties())));

    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
