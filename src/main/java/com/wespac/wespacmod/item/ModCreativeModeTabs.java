package com.wespac.wespacmod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import com.wespac.wespacmod.WespacMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = WespacMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WespacMod.MODID);

    public static final RegistryObject<CreativeModeTab> MONEY_TAB = CREATIVE_MODE_TABS.register("money_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MONEY_ITEMS.get("five_dollar_note").get()))
                    .title(Component.translatable("creativetab.money_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        for (RegistryObject<Item> item : ModItems.MONEY_ITEMS.values()) {
                            pOutput.accept(item.get());
                        }

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
