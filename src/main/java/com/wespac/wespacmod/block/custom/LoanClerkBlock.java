package com.wespac.wespacmod.block.custom;

import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.block.entity.LoanClerkBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class LoanClerkBlock extends BaseEntityBlock {

    public LoanClerkBlock(Properties pProperties) {
        super(pProperties);
    }

    private void interactWithClerk(Player player, Level pLevel) {
        player.sendSystemMessage(Component.translatable("Welcome to Westpac Loaning Solutions! Our current interest rate on loans is 10%, to request a loan, type /loan [amount]."));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            WespacMod.LOGGER.info("Interacted with loan");
            BlockEntity entity = pLevel.getBlockEntity(pPos);

            if(entity instanceof LoanClerkBlockEntity) {
                interactWithClerk(pPlayer, pLevel);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LoanClerkBlockEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

}