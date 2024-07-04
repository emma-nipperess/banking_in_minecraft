package com.wespac.wespacmod.item;

import com.wespac.wespacmod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import com.wespac.wespacmod.WespacMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WespacMod.MODID);

    public static final RegistryObject<CreativeModeTab> MONEY_TAB = CREATIVE_MODE_TABS.register("money_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MONEY_ITEMS.get("five_dollar_note").get()))
                    .title(Component.translatable("creativetab.money_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        for (RegistryObject<Item> item : ModItems.MONEY_ITEMS.values()) {
                            pOutput.accept(item.get());
                        }
                        pOutput.accept(ModBlocks.GEM_POLISHING_STATION.get());
                        pOutput.accept(ModBlocks.BANK_COUNTER.get());
                        pOutput.accept(ModBlocks.WESPAC_TELLER.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
