package io.antoon.mc.ccc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CCCommand {
	public static void register(CommandDispatcher<ServerCommandSource>  dispatcher) {
		dispatcher.register(
				literal("cc").then(literal("verify").then(CommandManager.argument("code", StringArgumentType.string()).executes(
						(context) -> executeVerify(context.getSource(), context.getSource().getPlayer().getUuidAsString(), StringArgumentType.getString(context, "code"))
				)))
		);
	}

	private static int executeVerify(ServerCommandSource source, String uuid, String code) {
		ExternalRequestManager.verifyCode(uuid, code, success -> {
			if (success)
				source.sendFeedback(Text.of("Verification successful!"), false);
			else
				source.sendFeedback(Text.of("Verification failed"), false);
		});

		return 1;
	}
}
