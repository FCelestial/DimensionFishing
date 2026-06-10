package org.evenmorefish.dimensionfishing.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.config.MainConfig;

@SuppressWarnings("UnstableApiUsage")
public class MainCommand {

    public static LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("dimensionfishing")
            .requires(stack -> stack.getSender().hasPermission("dimensionfishing.command"))
            .then(reload())
            .build();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reload() {
        return Commands.literal("reload")
            .executes(ctx -> {
                DimensionFishing.getInstance().reload();
                ctx.getSource().getSender().sendMessage(MainConfig.getInstance().getReloadMessage());
                return 1;
            });
    }

}
