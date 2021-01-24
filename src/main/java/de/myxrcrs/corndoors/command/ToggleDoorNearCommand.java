package de.myxrcrs.corndoors.command;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import org.apache.commons.lang3.tuple.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.myxrcrs.corndoors.blocks.AbstractTemplateDoor;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ToggleDoorNearCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> cmd = Commands.literal("toggledoor").requires((commandSource) -> {
            return commandSource.hasPermissionLevel(0);
        }).then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("radius", IntegerArgumentType.integer(0, 15)).executes((context) -> {
                    return toggleDoorNear(context.getSource(),BlockPosArgument.getBlockPos(context, "pos"),
                            IntegerArgumentType.getInteger(context, "radius"), context.getSource().getWorld(), null);
                })

                
                    .then(Commands.literal("toggle").executes((context)->{
                        return toggleDoorNear(context.getSource(),BlockPosArgument.getBlockPos(context, "pos"),
                                IntegerArgumentType.getInteger(context, "radius"), context.getSource().getWorld(), null);
                    }))

                
                    .then(Commands.literal("open").executes((context) -> {
                        return toggleDoorNear(context.getSource(),BlockPosArgument.getBlockPos(context, "pos"),
                                IntegerArgumentType.getInteger(context, "radius"), context.getSource().getWorld(), true);
                    }))

                    .then(Commands.literal("close").executes((context) -> {
                        return toggleDoorNear(context.getSource(),BlockPosArgument.getBlockPos(context, "pos"),
                                IntegerArgumentType.getInteger(context, "radius"), context.getSource().getWorld(),
                                false);
                    }))

                ));
        dispatcher.register(cmd);
    }

    public static int toggleDoorNear(CommandSource source,BlockPos pos, int radius, World world, @Nullable Boolean isToggleOpen) throws CommandSyntaxException {
        LOGGER.debug(pos+" "+radius+" "+isToggleOpen);
        TreeSet<Pair<BlockPos,BlockState>> set = new TreeSet<Pair<BlockPos,BlockState>>((pos1,pos2)->{
            double d1 = pos.distanceSq(pos1.getLeft());
            double d2 = pos.distanceSq(pos2.getLeft());
            return d1-d2>0?1:d1==d2?0:-1;
        });
        for(int dx = -radius ; dx <= radius ; dx++){
            for(int dy = -radius ; dy <= radius ; dy++){
                for(int dz = -radius ; dz <= radius ; dz++){
                    BlockPos currentPos = pos.add(dx, dy, dz);
                    BlockState currentState = world.getBlockState(currentPos);
                    if(currentState.getBlock() instanceof AbstractTemplateDoor){
                        set.add(Pair.of(currentPos,currentState));
                    }
                }
            }
        }
        for(Pair<BlockPos,BlockState> p:set){
            BlockPos currentPos = p.getLeft();
            BlockState currentState = p.getRight();
            AbstractTemplateDoor doorBlock = (AbstractTemplateDoor)currentState.getBlock();
            if(currentState.has(BlockStateProperties.DOOR_HINGE)){
                if(
                    (isToggleOpen==null) || 
                    (isToggleOpen && !currentState.get(AbstractTemplateDoor.IS_OPENED)) ||
                    (!isToggleOpen && currentState.get(AbstractTemplateDoor.IS_OPENED))
                ){
                    if(doorBlock.toggleDoor(world, currentPos, currentState, currentState.get(BlockStateProperties.DOOR_HINGE))){
                        world.playEvent(doorBlock.getToggleSound(currentState), currentPos, 0);
                        source.sendFeedback(new TranslationTextComponent("commands.toggleDoor.successOne"), true);
                        return 1;
                    }
                }
                
            }
        }
        throw new SimpleCommandExceptionType(new TranslationTextComponent("commands.toggleDoor.failed")).create();
    }
}
