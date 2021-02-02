package de.myxrcrs.corndoors.blocks;

import javax.annotation.Nullable;

import de.myxrcrs.corndoors.util.Matrix;
import de.myxrcrs.corndoors.util.Zero;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * The template of Corn Doors.
 */
public abstract class AbstractTemplateDoor extends AbstractPanel {

    public static class DoorRoot {

        protected DoorRoot(BlockState state,BlockPos pos){
            if(!(state.getBlock() instanceof AbstractTemplateDoor)){
                throw new IllegalArgumentException("State is not door.");
            }
            rootState = state;
            rootPos = pos;
        }

        public static DoorRoot of(BlockState state,BlockPos pos){
            return new DoorRoot(state,pos);
        }

        protected final BlockState rootState;
        protected final BlockPos rootPos;

        public BlockState getState(){
            return rootState;
        }

        public BlockPos getPos(){
            return rootPos;
        }

        @Override
        public int hashCode(){
            return rootPos.hashCode();
        }

        @Override
        public boolean equals(Object obj){
            if(!(obj instanceof DoorRoot))return false;
            return hashCode() == obj.hashCode();
        }

    }

    /**
     * The range where a door locates.
     */
    public static class DoorRange {

        public interface RangeIterationConsumer {
            void iterate(int x,int y,int z);
        }

        public interface BooleanRangeIterationConsumer {
            boolean iterate(int x,int y,int z);
        }

        /**
         * The position of the block in the door whose bottom is adjacent to the ground and side is the hinge.
         */
        protected final BlockPos from;

        /**
         * The position of the block on the other end of diagonal.
         */
        protected final BlockPos to;

        /**
         * The hinge vector.
         * @see {@link de.myxrcrs.corndoors.util.Matrix#getHingeVector(double[][], net.minecraft.state.properties.DoorHingeSide)}
         */
        protected final double[][] dirVec;
        
        protected DoorRange(BlockPos from, BlockPos to, double[][] dirVec){
            this.from = from;
            this.to = to;
            this.dirVec = dirVec;
        }

        public static DoorRange of(BlockPos from, BlockPos to, double[][] dirVec){
            return new DoorRange(from, to, dirVec);
        }

        public BlockPos getFrom(){
            return this.from;
        }

        public BlockPos getTo(){
            return this.to;
        }

        public double[][] getDirVec(){
            return this.dirVec;
        }

        public boolean iterateRange(RangeIterationConsumer func){
            return iterateRange((x,y,z)->{
                func.iterate(x, y, z);
                return true;
            });
        }

        public boolean iterateRange(BooleanRangeIterationConsumer func){
            boolean xOnce = false;
            for(int x = from.getX() ; x != to.getX() || (dirVec[0][0]==0&&!xOnce) ; x-=(int)dirVec[0][0]){
                for(int y = from.getY() ; y != to.getY() ; y++){
                    boolean zOnce = false;
                    for(int z = from.getZ(); z != to.getZ() || (dirVec[0][1]==0&&!zOnce) ; z-=(int)dirVec[0][1]){
                        if(!func.iterate(x, y, z))return false;
                        zOnce = true;
                    }
                }
                xOnce = true;
            }
            return true;
        }
    }



    /**
     * Create horizontal property.
     * <p>
     * Horizontal position represents the distance of a door block relative to the hinge. The position starts from zero.
     * <img src="https://img.imgdb.cn/item/600101793ffa7d37b318162b.png" alt="The diagram of position properties.">
     * @param width The width of the door.
     * @return Horizontal position property.
     */
    protected static Property<Integer> createHorizontalPosProperty(int width){
        if(width>1)
            return IntegerProperty.create("horizontal_pos", 0, width-1);
        else
            return Zero.create("horizontal_pos");
    }

    /**
     * Create vertical property.
     * <p>
     * Vertical position represents the distance of a door block relative to the ground. The position starts from zero.
     * <img src="https://img.imgdb.cn/item/600101793ffa7d37b318162b.png" alt="The diagram of position properties.">
     * @param height The height of the door.
     * @return Vertical position property.
     */
    protected static Property<Integer> createVerticalPosProperty(int height){
        if(height>1)
            return IntegerProperty.create("vertical_pos", 0, height-1);
        else
            return Zero.create("vertical_pos");
    }

    public static final BooleanProperty IS_OPENED = BooleanProperty.create("is_opened");

    

    /**
     * 
     * @param props Block properties. As the properties in {@link net.minecraft.block.Block#Block}.
     * @param thickness The thickness of the door model.
     */
    public AbstractTemplateDoor(Properties props,double thickness,boolean isMiddle){
        super(props,thickness,isMiddle);
        this.setDefaultState(this.getDefaultState()
            .with(IS_OPENED, false));
    }

    /**
     * Get the sound of door toggling.
     * @param stateBeforeToggled The blockstate before the door is toggled.
     * @return
     */
    public int getToggleSound(BlockState stateBeforeToggled){
        boolean opened = stateBeforeToggled.get(IS_OPENED);
        if(stateBeforeToggled.getMaterial()==Material.IRON){
            return opened ? 1011 : 1005;
        }else{
            return opened ? 1012 : 1006;
        }
    }

    /**
     * Get the width of the door.
     * @return The width.
     */
    abstract int getWidth(BlockState state);

    /**
     * Get the height of the door.
     * @return The height.
     */
    abstract int getHeight(BlockState state);

    /**
     * Toggle door open/close.
     * @param side The hinge side of the door, about which the door rotates or towards which the door shrinks.
     * @return Whether the operation is successful.
     */
    abstract public boolean toggleDoor(World world, BlockPos pos, BlockState state, DoorHingeSide side);

    /**
     * Called on the block is destroyed (before it is set to air).
     */
    abstract public void onHarvested(World world, BlockState state, BlockPos pos);

    /**
     * Called on the block is being placed (before the position is set to the door).
     * @param stateTemplate Template state (Default to be {@link #getDefaultState()}).
     */
    abstract public void onPlaced(BlockItemUseContext context, BlockState stateTemplate);

    /**
     * Fill a door into the given range. 
     * <p>
     * Before invoked, {@link #canFillRange(World, DoorRange)} must be called first to judge whether the given range is able to be filled.
     */
    abstract public void fillRange(World world, DoorRange range, BlockState stateTemplate, BlockItemUseContext context);

    @Nullable
    abstract public DoorRoot getDoorRoot(World world, BlockState state, BlockPos pos);

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        try{
            onHarvested(worldIn, state, pos);
        }catch(Exception e){
            LOGGER.error(e);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context){
        try{
            onPlaced(context,this.getDefaultState());
            return null;
        }catch(Exception e){
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * Judge whether the door block can be set to the target pos.
     * @return what you think it will return.
     */
    public boolean canTogglePos(World world, BlockPos target){
        return world.isAirBlock(target);
    }

    public boolean canTogglePos(World world, BlockState state, BlockPos target){
        return canTogglePos(world, target);
    }

    /**
     * Judge whether a the door block can be filled into the range.
     * @return what you think it will return.
     */
    public boolean canFillRange(World world, DoorRange range){
        return range.iterateRange((x,y,z)->{
            if(!canTogglePos(world, new BlockPos(x,y,z))){
                return false;
            }
            return true;
        });
    }

    /**
     * Get the range according to specified parameters.
     * @param facing The direction along which the door faces.
     * @param pos Current block position.
     * @param side The hinge side.
     * @param width The width of the entire door
     * @param height The height of the entire door
     * @param horizontalPos The horizontal position of current block.
     * @param verticalPos The vertical position of current block.
     * @return The result range.
     */
    public DoorRange getDoorRange(Direction facing, BlockPos pos, DoorHingeSide side, int width, int height, int horizontalPos, int verticalPos) {
        final double[][] hL = 
            {{ 0, 1},
            { -1, 0}};
        final double[][] hR = 
            {{ 0,-1},
            {  1, 0}};
            double[][] a = {{pos.getX(),pos.getZ()}};
            double[][] u = Matrix.horizontalDirectionToMatrix(facing);
            boolean flag = side == DoorHingeSide.LEFT;
            double[][] v = Matrix.mul(u,flag?hL:hR);
            double[][] center = Matrix.add(a,Matrix.mul(v,horizontalPos));
            BlockPos fromPos = new BlockPos(center[0][0],pos.getY()-verticalPos,center[0][1]);
            //double[][] edge = Matrix.add(a,Matrix.mul(v,horizontalPos-width+1));
            double[][] edge = Matrix.add(a,Matrix.mul(v,horizontalPos-width));
            BlockPos toPos = new BlockPos(edge[0][0],pos.getY()+height-verticalPos,edge[0][1]);
            return DoorRange.of(fromPos, toPos,v);
    }

    /**
     * Get the size using blockstate property.
     * @param p Horizontal or vertical position property.
     * @return The size.
     */
    public int getSize(Property<Integer> p){
        if(p.getAllowedValues().contains(114514))return 1;
        else return p.getAllowedValues().size();
    }

    public boolean onDidCheck(World world,BlockState state,BlockPos pos){
        return true;
    }

    public void onDidHarvest(World world,BlockState state,BlockPos pos){

    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(IS_OPENED);
    }

    public BlockState onWillSetNewState(World world,BlockState state,BlockPos pos){
        return state;
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
     }
   
    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
   
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
}
