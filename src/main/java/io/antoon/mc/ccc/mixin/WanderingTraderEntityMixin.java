package io.antoon.mc.ccc.mixin;

import io.antoon.mc.ccc.CCCMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin {

	@Inject(at=@At(value="RETURN"), method="fillRecipes")
	protected void fillRecipes(CallbackInfo info) {
		// 50-50 to add a player's head
		if (CCCMain.cccRandom.nextBoolean()) {
			// Set up the head
			ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
			NbtCompound tag = new NbtCompound();
			tag.putString("SkullOwner", CCCMain.getRandomSkullOwner());
			itemStack.setNbt(tag);

			// Create trade offer
			TradeOffer headOffer = new TradeOffer(new ItemStack(Items.DIAMOND), ItemStack.EMPTY, itemStack, 4, 1, 1);

			// Add trade to list
			WanderingTraderEntity self = (WanderingTraderEntity) (Object) this;
			self.getOffers().add(headOffer);
		}
	}
}
