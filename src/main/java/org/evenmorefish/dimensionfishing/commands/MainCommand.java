package org.evenmorefish.dimensionfishing.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.evenmorefish.dimensionfishing.DimensionFishing;

@SuppressWarnings("UnstableApiUsage")
public class MainCommand {

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("dimensionfishing")
            .then(reload())
            .build();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reload() {
        return Commands.literal("reload")
            .executes(ctx -> {
                DimensionFishing.getInstance().reload();
                ctx.getSource().getSender().sendPlainMessage("[DimensionFishing] Successfully reloaded the plugin.");
                return 1;
            });
    }

}
