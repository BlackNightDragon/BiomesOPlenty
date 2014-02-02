package biomesoplenty.common.biomes;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import biomesoplenty.api.BOPBlockHelper;
import biomesoplenty.common.world.features.WorldGenBOPFlora;

public class BiomeGenThicket extends BOPBiome
{
	private static final Height biomeHeight = new Height(0.1F, 0.1F);
	
	public BiomeGenThicket(int id)
	{
		super(id);
		
        //TODO: setHeight()
        this.func_150570_a(biomeHeight);
        //TODO:	setColor()
        this.setColor(7248193);
        this.setTemperatureRainfall(0.6F, 0.2F);

		this.theBiomeDecorator.treesPerChunk = 17;
		this.theBiomeDecorator.grassPerChunk = 1;

	    this.bopWorldFeatures.bopFlowersPerChunk = 5;
		this.bopWorldFeatures.thornsPerChunk = 55;
		this.bopWorldFeatures.shrubsPerChunk = 5;
	}

	@Override
	//TODO:						getRandomWorldGenForTrees()
	public WorldGenAbstractTree func_150567_a(Random random)
	{
		return random.nextInt(5) == 0 ? worldGeneratorTrees : new WorldGenShrub(0, 0);
	}
	
    @Override
    public HashMap<WorldGenBOPFlora, Integer> getWeightedWorldGenForBOPFlowers()
    {
        HashMap<WorldGenBOPFlora, Integer> flowerMap = new HashMap();
        
        flowerMap.put(new WorldGenBOPFlora(Blocks.red_flower, 2), 4);
        
        return flowerMap;
    }

	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random random)
	{
		return random.nextInt(4) == 0 ? new WorldGenTallGrass(Blocks.tallgrass, 1) : new WorldGenTallGrass(BOPBlockHelper.get("foliage"), 10);
	}
	
	@Override
    //TODO:     getBiomeGrassColor()
    public int func_150558_b(int p_150558_1_, int p_150558_2_, int p_150558_3_)
    {
		return 11049591;
	}

	@Override
    //TODO:     getBiomeFoliageColor()
    public int func_150571_c(int x, int y, int z)
    {
		return 10854765;
	}
}
