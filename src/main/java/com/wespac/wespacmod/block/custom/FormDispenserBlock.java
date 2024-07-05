package com.wespac.wespacmod.block.custom;

import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.block.entity.FormDispenserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class FormDispenserBlock extends BaseEntityBlock {

    public FormDispenserBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            WespacMod.LOGGER.info("Interacted with form");
            BlockEntity entity = pLevel.getBlockEntity(pPos);

             if(entity instanceof FormDispenserBlockEntity) {

                 dropForm(pPlayer, pLevel);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }



        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    /**
     * Drops a form for player to fill out
     * @param pPlayer
     * @param pLevel
     */
    public void dropForm(Player pPlayer, Level pLevel) {
        ItemStack book = new ItemStack(Items.WRITABLE_BOOK);

        // Create the NBT data for the book
        CompoundTag tag = new CompoundTag();
        ListTag pages = new ListTag();

        // Add pages to the book
        pages.add(StringTag.valueOf(Component.literal("First Name: \nLast Name: \nPassword: ").getString()));

        // Set the author, title, and pages
        tag.putString("author", "Westpac");
        tag.putString("title", "Westpac Account Application Form");
        tag.put("pages", pages);

        CompoundTag displayTag = new CompoundTag();
        displayTag.putString("Name", Component.Serializer.toJson(Component.literal("Westpac Application Form")));
        tag.put("display", displayTag);

        // Set the tag to the book item stack
        book.setTag(tag);
        WespacMod.LOGGER.info("Attemping to drop form");


        if (!pPlayer.addItem(book)) {
            ItemEntity itemEntity = new ItemEntity(pLevel, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), book);
            pLevel.addFreshEntity(itemEntity);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FormDispenserBlockEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

}