package io.antoon.mc.ccc.mixin.Enderman;

import io.antoon.mc.ccc.CCCMain;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin extends MobEntity {
	protected EndermanEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	Block getCarriedBlockOnSpawn() {
		int i = CCCMain.cccRandom.nextInt(500);

		if (i < 125) { // 25%
			return Blocks.GRASS_BLOCK;
		}

		if (i < 175) { // 10%
			return Blocks.SAND;
		}

		if (i < 200) { // 5%
			return Blocks.GRAVEL;
		}

		if (i < 205) { // 1%
			return Blocks.GLOWSTONE;
		}

		if (i == 499) { // 0.2%
			return Blocks.DIAMOND_BLOCK;
		}

		return Blocks.AIR; // This works but I hate that it works. Should have to be null here or something
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
		EndermanEntity self = (EndermanEntity) (Object) this;

		// Chance to spawn with a block if there is no NBT
		if (entityNbt == null) {
			self.setCarriedBlock(getCarriedBlockOnSpawn().getDefaultState());
		}

		return entityData;
	}

	// Don't drop the block the enderman is holding
	@Inject(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/EndermanEntity;getCarriedBlock()Lnet/minecraft/block/BlockState;", shift = At.Shift.BEFORE), cancellable = true)
	void dropEquipment(CallbackInfo info) {
		info.cancel();
	}
}