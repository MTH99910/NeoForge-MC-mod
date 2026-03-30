package net.parados.infernalcarnage.entity.custom.goal.vfx;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class ParticlesHandler
{
    public static void SpawnLandingParticles(Mob mob)
    {
        if (mob.level().isClientSide())
            return;

        ServerLevel level = (ServerLevel) mob.level();

        // Get the block the mob is standing on
        BlockPos groundPos = mob.blockPosition().below();
        BlockState groundBlock = level.getBlockState(groundPos);

        // Map the block to an approximate color
        Vector3f color = getBlockColor(groundBlock);

        DustParticleOptions dust = new DustParticleOptions(color, 1.5f);

        // Spawn particles in a ring around the landing point
        double x = mob.getX();
        double y = mob.getY();
        double z = mob.getZ();

        for (int i = 0; i < 24; i++)
        {
            double angle = (2 * Math.PI / 24) * i;
            double offsetX = Math.cos(angle) * 0.8;
            double offsetZ = Math.sin(angle) * 0.8;

            // Small random burst velocity outward
            double velX = offsetX * 0.15;
            double velY = 0.1 + mob.level().random.nextDouble() * 0.2;
            double velZ = offsetZ * 0.15;

            level.sendParticles(dust, x + offsetX, y + 0.05, z + offsetZ, 1, velX, velY, velZ, 0.0);
        }
    }

    private static Vector3f getBlockColor(BlockState block)
    {
        // Returns an RGB Vector3F (values 0.0 - 1.0) based on block type
        Block b = block.getBlock();

        if (b == Blocks.GRASS_BLOCK || b == Blocks.SHORT_GRASS)  return new Vector3f(0.38f, 0.60f, 0.25f);
        if (b == Blocks.DIRT || b == Blocks.COARSE_DIRT)         return new Vector3f(0.53f, 0.36f, 0.22f);
        if (b == Blocks.SAND || b == Blocks.SANDSTONE)           return new Vector3f(0.90f, 0.85f, 0.62f);
        if (b == Blocks.GRAVEL)                                   return new Vector3f(0.55f, 0.55f, 0.55f);
        if (b == Blocks.STONE || b == Blocks.COBBLESTONE)        return new Vector3f(0.50f, 0.50f, 0.50f);
        if (b == Blocks.DEEPSLATE || b == Blocks.COBBLED_DEEPSLATE) return new Vector3f(0.30f, 0.30f, 0.35f);
        if (b == Blocks.OAK_PLANKS || b == Blocks.OAK_LOG)      return new Vector3f(0.65f, 0.50f, 0.28f);
        if (b == Blocks.NETHERRACK)                               return new Vector3f(0.55f, 0.18f, 0.18f);
        if (b == Blocks.SOUL_SAND || b == Blocks.SOUL_SOIL)      return new Vector3f(0.40f, 0.32f, 0.25f);
        if (b == Blocks.CRIMSON_NYLIUM)                          return new Vector3f(0.70f, 0.18f, 0.18f);
        if (b == Blocks.WARPED_NYLIUM)                           return new Vector3f(0.18f, 0.55f, 0.50f);
        if (b == Blocks.SNOW_BLOCK || b == Blocks.POWDER_SNOW)  return new Vector3f(0.95f, 0.95f, 0.98f);
        if (b == Blocks.ICE || b == Blocks.PACKED_ICE)          return new Vector3f(0.70f, 0.85f, 0.95f);
        if (b == Blocks.OBSIDIAN)                                return new Vector3f(0.10f, 0.08f, 0.18f);
        if (b == Blocks.END_STONE)                               return new Vector3f(0.88f, 0.88f, 0.68f);

        // Fallback -- generic gray dust for any unrecognized block
        return new Vector3f(0.6f, 0.6f, 0.6f);
    }
}
