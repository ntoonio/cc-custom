package io.antoon.mc.ccc;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static io.antoon.mc.ccc.CCCMain.CONFIG;
import static net.minecraft.server.command.CommandManager.literal;

public class CCCommand {
	public static void register(CommandDispatcher<ServerCommandSource>  dispatcher) {
		dispatcher.register(
				literal("cc").then(literal("heads").executes(
						(context) -> executeHeads(context.getSource())
				))
		);
	}

	private static int executeHeads(ServerCommandSource source) {
		if (CONFIG.playerHeadsAvailable()) {
			source.sendFeedback(() -> Text.literal(String.join(", ", CONFIG.skullOwners)), false);
		}
		else {
			source.sendFeedback(() -> Text.literal("No heads are available"), false);
		}

		return 1;
	}
}
