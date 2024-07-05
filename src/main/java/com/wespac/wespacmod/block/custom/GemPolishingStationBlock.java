package com.wespac.wespacmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.wespac.wespacmod.block.entity.GemPolishingStationBlockEntity;
import com.wespac.wespacmod.block.entity.ModBlockEntities;
import com.wespac.wespacmod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class GemPolishingStationBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

    public GemPolishingStationBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof GemPolishingStationBlockEntity) {
                ((GemPolishingStationBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof GemPolishingStationBlockEntity) {
                ItemStack heldItem = pPlayer.getItemInHand(pHand);

                // Check if the player is holding a specific item (e.g., credit card)
                if (ModItems.MONEY_ITEMS.containsKey("credit_card") && heldItem.getItem() == ModItems.MONEY_ITEMS.get("credit_card").get()) {
                    // Perform actions specific to the credit card item
                    pPlayer.sendSystemMessage(Component.literal("Scanning your card... please wait"));

                    sendChatMessage(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            pPlayer.sendSystemMessage(Component.literal("Oops! Our servers are having problems, please try again :("));
                        }

                        @Override
                        public void onResponse(Call call, Response
                                response) throws IOException {
                            if (response.isSuccessful()) {
                                String responseBody = response.body().string();
                                // Parse and handle the response
                                pPlayer.sendSystemMessage(Component.literal(responseBody));
                            } else {
                                pPlayer.sendSystemMessage(Component.literal("Oops! Our servers are having problems, please try again :("));
                            }
                        }
                    });
                } else {
                    pPlayer.sendSystemMessage(Component.literal("Please hold a credit card to proceed."));
                }

            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_URL =  "http://10.89.247.191:9000/intro";

    public static void sendChatMessage(Callback callback) {

        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GemPolishingStationBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.GEM_POLISHING_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
}