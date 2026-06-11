package org.evenmorefish.dimensionfishing.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.evenmorefish.dimensionfishing.DimensionFishing;
import org.evenmorefish.dimensionfishing.config.MainConfig;
import org.evenmorefish.dimensionfishing.state.FishingState;

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
                CommandSender sender = ctx.getSource().getSender();
                if (sender instanceof Player player) {
                    FishingState.LAVA.getLureParticles().show(player.getLocation());
                    FishingState.VOID.getLureParticles().show(player.getLocation());
                }
                sender.sendMessage(MainConfig.getInstance().getReloadMessage());
                return 1;
            });
    }

}
