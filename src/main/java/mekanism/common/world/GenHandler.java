package mekanism.common.world;

import java.util.Random;

import mekanism.api.MekanismConfig.general;
import mekanism.common.MekanismBlocks;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

public class GenHandler implements IWorldGenerator
{
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
	{
		if(!(chunkGenerator instanceof ChunkProviderHell) && !(chunkGenerator instanceof ChunkProviderEnd))
		{
			for(int i = 0; i < general.osmiumPerChunk; i++)
			{
				BlockPos pos = new BlockPos(chunkX*16 + random.nextInt(16), random.nextInt(60), (chunkZ*16) + random.nextInt(16));
				new WorldGenMinable(MekanismBlocks.OreBlock.getStateFromMeta(0), 8, BlockMatcher.forBlock(Blocks.STONE)).generate(world, random, pos);
			}

			for(int i = 0; i < general.copperPerChunk; i++)
			{
				BlockPos pos = new BlockPos(chunkX*16 + random.nextInt(16), random.nextInt(60), (chunkZ*16) + random.nextInt(16));
				new WorldGenMinable(MekanismBlocks.OreBlock.getStateFromMeta(1), 8, BlockMatcher.forBlock(Blocks.STONE)).generate(world, random, pos);
			}

			for(int i = 0; i < general.tinPerChunk; i++)
			{
				BlockPos pos = new BlockPos(chunkX*16 + random.nextInt(16), random.nextInt(60), (chunkZ*16) + random.nextInt(16));
				new WorldGenMinable(MekanismBlocks.OreBlock.getStateFromMeta(2), 8, BlockMatcher.forBlock(Blocks.STONE)).generate(world, random, pos);
			}
			
			for(int i = 0; i < general.saltPerChunk; i++)
			{
				int randPosX = (chunkX*16) + random.nextInt(16);
				int randPosZ = (chunkZ*16) + random.nextInt(16);
				BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(randPosX, 60, randPosZ));
				new WorldGenSalt(6).generate(world, random, pos);
			}
		}
	}
}
