package io.antoon.mc.ccc;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.antoon.mc.ccc.CCCMain;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CCCommand {
	public static void register(CommandDispatcher<ServerCommandSource>  dispatcher) {
		dispatcher.register(
				literal("cc").then(literal("verify").then(CommandManager.argument("code", StringArgumentType.word()).executes(
						(context) -> executeVerify(context.getSource(), context.getSource().getPlayer().getUuidAsString(), StringArgumentType.getString(context, "code"))
				)))
		);

		dispatcher.register(
				literal("cc").then(literal("heads").executes(
						(context) -> executeHeads(context.getSource())
				))
		);
	}

	private static int executeVerify(ServerCommandSource source, String uuid, String code) {
		ExternalRequestManager.verifyCode(uuid, code, success -> {
			if (success)
				source.sendFeedback(() -> Text.literal("Verification successful!"), false);
			else
				source.sendFeedback(() -> Text.literal("Verification failed"), false);
		});

		return 1;
	}

	private static int executeHeads(ServerCommandSource source) {
		source.sendFeedback(() -> Text.literal(String.join(", ", CCCMain.skullOwners)), false);
		return 1;
	}
}
