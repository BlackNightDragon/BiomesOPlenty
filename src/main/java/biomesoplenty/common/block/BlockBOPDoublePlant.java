/*******************************************************************************
 * Copyright 2014-2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/

package biomesoplenty.common.block;

import java.util.List;

import com.google.common.collect.ImmutableSet;

import biomesoplenty.api.block.BOPBlocks;
import biomesoplenty.api.block.BlockQueries;
import biomesoplenty.common.enums.BOPPlants;
import biomesoplenty.common.util.block.BlockStateUtils;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBOPDoublePlant extends BlockBOPDoubleDecoration implements IShearable
{
    
    // add properties (note we inherit HALF from BlockDoubleDecoration)
    public static enum DoublePlantType implements IStringSerializable
    {
        FLAX, TALL_CATTAIL, EYEBULB;
        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }
        @Override
        public String toString()
        {
            return this.getName();
        }
    };
    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", DoublePlantType.class);
    @Override
    protected BlockState createBlockState() {return new BlockStateBase(this, new IProperty[] { HALF, VARIANT });}
    
    
    // implement IBOPBlock
    @Override
    public IProperty[] getPresetProperties() { return new IProperty[] {VARIANT}; }
    @Override
    public IProperty[] getNonRenderingProperties() { return null; }
    @Override
    public String getStateName(IBlockState state)
    {
        return ((DoublePlantType) state.getValue(VARIANT)).getName();
    }
    
    
    public BlockBOPDoublePlant()
    {
        super();
        this.setDefaultState( this.blockState.getBaseState().withProperty(HALF, Half.LOWER) .withProperty(VARIANT, DoublePlantType.FLAX) );
    }
    
    // map from state to meta and vice verca - use highest bit for Half, and lower bits for variant
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(HALF, Half.values()[meta >> 3]).withProperty(VARIANT, DoublePlantType.values()[meta & 7]);
    }
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((Half) state.getValue(HALF)).ordinal() * 8 + ((DoublePlantType) state.getValue(VARIANT)).ordinal();
    }
    
    
    
    public enum ColoringType {PLAIN, LIKE_LEAVES, LIKE_GRASS};
    
    public static ColoringType getColoringType(DoublePlantType plant)
    {
        switch (plant)
        {
            case FLAX:
                return ColoringType.LIKE_GRASS;
            default:
                return ColoringType.PLAIN;
        }       
    }
 
    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return 0xFFFFFF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(IBlockState state)
    {
        switch (getColoringType((DoublePlantType) state.getValue(VARIANT)))
        {
            case LIKE_LEAVES:
                return ColorizerFoliage.getFoliageColorBasic();
            case LIKE_GRASS:
                return ColorizerGrass.getGrassColor(0.5D, 1.0D);
            case PLAIN: default:
                return 0xFFFFFF;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        switch (getColoringType((DoublePlantType) worldIn.getBlockState(pos).getValue(VARIANT)))
        {
            case LIKE_LEAVES:
                return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
            case LIKE_GRASS:
                return BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
            case PLAIN: default:
                return 0xFFFFFF;
        }
    }
    
    // flax item should not be tinted, even though the model is
    @Override
    public int getItemRenderColor(IBlockState state, int tintIndex)
    {
        switch ((DoublePlantType) state.getValue(VARIANT))
        {
            case FLAX:
                return 0xFFFFFF;
            default:
                return this.getRenderColor(state);
        }
    }
    
    
    
    // different variants have different sizes
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {    
        boolean isLower = ((Half) worldIn.getBlockState(pos).getValue(HALF) == Half.LOWER);
        switch ((DoublePlantType) worldIn.getBlockState(pos).getValue(VARIANT))
        {
            default:
                this.setBlockBoundsByRadiusAndHeightWithXZOffset(0.4F, isLower ? 1.0F : 0.8F, pos);
                break;
        }
    }

    
    @Override
    public boolean canBlockStay(World world, BlockPos lowerPos, IBlockState state)
    {     
        DoublePlantType plant = ((DoublePlantType) state.getValue(VARIANT));

        switch (plant)
        {
            case TALL_CATTAIL:
                return BlockQueries.litFertileWaterside.matches(world, lowerPos.down());
            case EYEBULB:
                return BlockQueries.hellish.matches(world, lowerPos.down());
            case FLAX: default:
                return BlockQueries.litFertile.matches(world, lowerPos.down());
        }
        
    }

    @Override
    public List<ItemStack> getUpperDrops(IBlockAccess world, BlockPos upperPos, IBlockState upperState, int fortune)
    {
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

        DoublePlantType type = (DoublePlantType) upperState.getValue(VARIANT);
        switch (type) {

            case TALL_CATTAIL:
                ret.add(BlockBOPPlant.paging.getVariantItem(BOPPlants.CATTAIL));

            default:
                break;
        }
        return ret;
    }
    
    
    // get the items dropped when you bash the bush
    @Override
    public List<ItemStack> getLowerDrops(IBlockAccess world, BlockPos lowerPos, IBlockState lowerState, int fortune)
    {
        // start with an empty stack
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

        // add items based on the VARIANT - default is to drop (lower) block
        DoublePlantType type = (DoublePlantType) lowerState.getValue(VARIANT);
        switch (type)
        {
            case FLAX:
                break;
            case TALL_CATTAIL:
                ret.add(BlockBOPPlant.paging.getVariantItem(BOPPlants.CATTAIL));
                break;

            default:
                // drop self
                ret.add(this.getVariantItem(type));
        }
        return ret;
    }
    
    
    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }
    
    @Override
    public List<ItemStack> getLowerShearDrops(ItemStack item, IBlockAccess world, BlockPos lowerPos, IBlockState lowerState, int fortune)
    {
        // start with an empty stack
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

        // add items based on the VARIANT
        // note that the sheared items are dropped in addition to regular drops
        // since the default in getLowerDrops is to drop the (lower) block, the default here is to drop nothing (so we don't have a duplicate)
        // at the moment, this code is pretty useless, but if in the future we add a double block which can't be collected except with shears
        // then a case will need to be inserted for it in the switch below
        DoublePlantType type = (DoublePlantType) lowerState.getValue(VARIANT);
        switch (type)
        {
            case FLAX:
                ret.add(this.getVariantItem(type));
            default:
                break;
        }
        return ret;
    }

    public ItemStack getVariantItem(DoublePlantType type) {
        IBlockState state = this.getDefaultState().withProperty(HALF, Half.LOWER).withProperty(VARIANT, type);
        return new ItemStack(this, 1, this.getMetaFromState(state));
    }
    
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player)
    {
    	IBlockState state = world.getBlockState(pos);
    	
    	switch ((DoublePlantType) state.getValue(VARIANT))
    	{
    		case TALL_CATTAIL:
    			return new ItemStack(BOPBlocks.plant_1, 1, BlockBOPPlant.paging.getIndex(BOPPlants.CATTAIL));
    	
    		default:
    			return new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)));
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
    	// get the preset blocks variants
        ImmutableSet<IBlockState> presets = BlockStateUtils.getBlockPresets(this);
        
        for (IBlockState state : presets)
        {
        	if (state != BOPBlocks.double_plant.getDefaultState().withProperty(VARIANT,BlockBOPDoublePlant.DoublePlantType.TALL_CATTAIL))
        	{
        		list.add(new ItemStack(itemIn, 1, this.getMetaFromState(state)));
        	}
        }
    }
    
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return Blocks.tallgrass.getFlammability(world, pos, face);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return Blocks.tallgrass.getFlammability(world, pos, face);
    }
}