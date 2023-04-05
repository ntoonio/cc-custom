package io.antoon.mc.ccc.mixin;

import io.antoon.mc.ccc.CCCMain;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Inject(at = @At(value = "HEAD"), method = "shutdown")
	public void shutdown(CallbackInfo info) {
		CCCMain.scheduler.shutdown();
	}
}
