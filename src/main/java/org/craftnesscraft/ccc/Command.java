package org.craftnesscraft.ccc;

import static org.craftnesscraft.ccc.CCCustom.CONFIG;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class Command {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("cc")
				.then(Commands.literal("heads").executes(context -> {
					executeHeads(context.getSource());
					return 1;
				}))
		);
	}

	private static int executeHeads(CommandSourceStack source) {
		if (CONFIG.playerHeadsAvailable()) {
			String responseString = String.join(", ", CONFIG.skullOwners);

			source.sendSuccess(() -> Component.literal(responseString), false);
		}
		else {
			source.sendFailure(Component.literal("No heads are available"));
		}

		return 1;
	}
}

