package io.antoon.mc.ccc.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import io.antoon.mc.ccc.CCCMain;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin {

	@Inject(at=@At(value="RETURN"), method="fillRecipes")
	protected void fillRecipes(CallbackInfo info) {
		// 50-50 to add a player's head
		if (CCCMain.cccRandom.nextBoolean()) {
			// Set up the head
			ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
			itemStack.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.of(CCCMain.getRandomSkullOwner()), Optional.empty(), new PropertyMap()));

			// Create trade offer
			TradeOffer headOffer = new TradeOffer(new TradedItem(Items.DIAMOND), Optional.empty(), itemStack, 4, 1, 1);

			// Add trade to list
			WanderingTraderEntity self = (WanderingTraderEntity) (Object) this;
			self.getOffers().add(headOffer);
		}
	}
}
