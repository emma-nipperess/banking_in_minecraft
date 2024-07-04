package com.wespac.wespacmod.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CreditCardItem extends Item {
    public CreditCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);


        // Ensure the credit card has an account number
        if (!pStack.hasTag()) {
            pStack.setTag(new CompoundTag());
        }

        CompoundTag tag = pStack.getTag();
        if (!tag.contains("AccountNumber")) {
            tag.putString("AccountNumber", "1234-5678-9101-1121"); // Default account number
        }
    }

    public static String getAccountNumber(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("AccountNumber")) {
            return stack.getTag().getString("AccountNumber");
        }
        return "No Account Number";
    }
}
