package io.antoon.mc.ccc.mixin.Enderman;

import io.antoon.mc.ccc.CCCMain;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.antoon.mc.ccc.CCCMain.CONFIG;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin extends MobEntity {
	protected EndermanEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	Block getRandomSpawnBlock() {
		int i = CCCMain.cccRandom.nextInt(500);

		if (i < 25) { // 5%
			return Blocks.GRASS_BLOCK;
		}

		if (i < 35) { // 2%
			return Blocks.SAND;
		}

		if (i < 45) { // 2%
			return Blocks.GRAVEL;
		}

		if (i < 50) { // 1%
			return Blocks.GLOWSTONE;
		}

		if (i < 53) { // 0.6%
			return Blocks.TNT;
		}

		if (i == 499) { // 0.2%
			return Blocks.DIAMOND_BLOCK;
		}

		return Blocks.AIR; // This works but I hate that it works. Should have to be null here or something
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		EndermanEntity self = (EndermanEntity) (Object) this;

		// Chance to spawn with a block if in the overworld
		if (CONFIG.customEndermenBlocksEnabled && CCCMain.worldIsOverworld(world.toServerWorld())) {
			self.setCarriedBlock(getRandomSpawnBlock().getDefaultState());
		}

		return entityData;
	}

	// Ignore whether the Enderman is holding a block or not
	@Override
	public boolean cannotDespawn() {
		return super.cannotDespawn();
	}

	// Don't drop the block the enderman is holding
	@Inject(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/EndermanEntity;getCarriedBlock()Lnet/minecraft/block/BlockState;", shift = At.Shift.BEFORE), cancellable = true)
	void dropEquipment(CallbackInfo info) {
		info.cancel();
	}
}
