package de.myxrcrs.corndoors.command;

import java.util.HashSet;
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
import de.myxrcrs.corndoors.blocks.AbstractTemplateDoor.DoorRoot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ToggleAllDoorsWithinCommand {
    private static final Logger LOGGER = LogManager.getLogger();
    public static void register(CommandDispatcher<CommandSource> dispatcher){
        LiteralArgumentBuilder<CommandSource> cmd =  Commands.literal("toggledoor").requires((commandSource)->{return commandSource.hasPermissionLevel(0);})
            .then(Commands.argument("from", BlockPosArgument.blockPos())
                .then(Commands.argument("to", BlockPosArgument.blockPos())
                .executes((context)->{
                    return toggleAllDoorsWithin(context.getSource(),BlockPosArgument.getBlockPos(context, "from"), BlockPosArgument.getBlockPos(context, "to"),context.getSource().getWorld(),null);
                })

                    .then(Commands.literal("toggle")
                    .executes((context)->{
                        return toggleAllDoorsWithin(context.getSource(),BlockPosArgument.getBlockPos(context, "from"), BlockPosArgument.getBlockPos(context, "to"),context.getSource().getWorld(),null);
                    }))

                    .then(Commands.literal("open")
                    .executes((context)->{
                        return toggleAllDoorsWithin(context.getSource(),BlockPosArgument.getBlockPos(context, "from"), BlockPosArgument.getBlockPos(context, "to"),context.getSource().getWorld(),true);
                    }))
                    
                    .then(Commands.literal("close")
                    .executes((context)->{
                        return toggleAllDoorsWithin(context.getSource(),BlockPosArgument.getBlockPos(context, "from"), BlockPosArgument.getBlockPos(context, "to"),context.getSource().getWorld(),false);
                    }))
                    
                )
            );
        dispatcher.register(cmd);
    }
    public static int toggleAllDoorsWithin(CommandSource source,BlockPos from,BlockPos to,World world,@Nullable Boolean isToggleOpen) throws CommandSyntaxException {
        HashSet<DoorRoot> set = new HashSet<DoorRoot>();
        MutableBoundingBox area = new MutableBoundingBox(from,to);
        int size = area.getXSize()*area.getYSize()*area.getZSize();
        if(size>32768){
            throw new SimpleCommandExceptionType(new TranslationTextComponent("commands.toggleDoor.toobig", 32768, size)).create();
        }
        for(BlockPos currentPos:BlockPos.getAllInBoxMutable(from, to)){
            BlockState currentState = world.getBlockState(currentPos);
            Block block = currentState.getBlock();
            if(block instanceof AbstractTemplateDoor){
                @Nullable DoorRoot doorRoot = ((AbstractTemplateDoor)block).getDoorRoot(world, currentState, currentPos);
                if(doorRoot!=null){
                    set.add(doorRoot);
                }

            }
        }
        int i = 0;
        for(DoorRoot doorRoot:set){
            BlockPos currentPos = doorRoot.getPos();
            BlockState currentState = doorRoot.getState();
            AbstractTemplateDoor doorBlock = (AbstractTemplateDoor)currentState.getBlock();
            if(currentState.has(BlockStateProperties.DOOR_HINGE)){
                if(
                    (isToggleOpen==null) || 
                    (isToggleOpen && !currentState.get(AbstractTemplateDoor.IS_OPENED)) ||
                    (!isToggleOpen && currentState.get(AbstractTemplateDoor.IS_OPENED))
                ){
                    if(doorBlock.toggleDoor(world, currentPos, currentState, currentState.get(BlockStateProperties.DOOR_HINGE))){
                        world.playEvent(doorBlock.getToggleSound(currentState), currentPos, 0);
                        i++;
                    }
                        
                }
                
            }
        }
        if(i==0){
            throw new SimpleCommandExceptionType(new TranslationTextComponent("commands.toggleDoor.failed")).create();
        }else{
            source.sendFeedback(new TranslationTextComponent("commands.toggleDoor.successAll",i), true);
        }
        return i;
    }
}
