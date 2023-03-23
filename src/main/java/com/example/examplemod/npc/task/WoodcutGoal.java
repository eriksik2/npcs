package com.example.examplemod.npc.task;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;

public class WoodcutGoal extends Goal {

    private NpcEntity mob;

    private NpcManager manager;
    private NpcTeam team;

    private BlockPos blockPos;
    private ArrayList<BlockPos> vein;
    private boolean foundBlock = false;
    private int ticksSinceFound = 0;

    public WoodcutGoal(NpcEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if(mob.npcData == null) {
            return false;
        }
        manager = NpcManager.get(mob.level);
        team = manager.getNpcTeam(mob.npcData);
        if(getRoles().isEmpty()) {
            return false;
        }
        
        if(this.blockPos == null && !findNearestBlock()) {
            return false;
        }
        return true;
    }

    protected List<NpcRole> getRoles() {
        if(mob.npcId == null || team == null) {
            return new ArrayList<>();
        }
        List<Integer> roleIds = team.getRolesOf(mob.npcId);
        if(roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return roleIds.stream().map(id -> team.getRole(id)).toList();
    }

    public boolean canContinueToUse() {
        return this.blockPos != null && this.vein != null && canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        this.mob.getNavigation().moveTo(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ(), 1.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.blockPos == null || this.vein == null) {
            return;
        }
        if(!foundBlock) {
            destroyBlockingLeaves();
            if(this.mob.distanceToSqr(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()) < 20.0D) {
                this.mob.getNavigation().stop();
                foundBlock = true;
                ticksSinceFound = 0;
            }
            else if(this.mob.getNavigation().isDone()) {
                this.mob.getNavigation().moveTo(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ(), 1.0D);
            }
        } else {
            this.mob.getNavigation().stop();
            this.mob.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()));
            this.mob.lookAt(Anchor.FEET, new Vec3(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()));
            //if(ticksSinceFound % 10 == 0) {
            //    this.mob.swing(InteractionHand.MAIN_HAND, true); Doesnt work?
            //}
            if(ticksSinceFound >= 20) {
                this.mob.level.destroyBlock(this.blockPos, false);
                nextBlock();
            }
            ticksSinceFound += 1;
        }
    }

    private void nextBlock() {
        if(this.vein == null || this.vein.isEmpty()) {
            this.blockPos = null;
            this.vein = null;
            return;
        }
        this.blockPos = this.vein.remove(0);
        this.foundBlock = false;
    }

    @Override
    public void stop() {
        this.blockPos = null;
        this.vein = null;
        this.foundBlock = false;
        super.stop();
    }

    private void destroyBlockingLeaves() {
        BlockPos pos = this.blockPos;
        ArrayList<BlockPos> positions = new ArrayList<>();
        positions.add(pos.north());
        positions.add(pos.south());
        positions.add(pos.east());
        positions.add(pos.west());
        positions.add(pos.above());
        positions.add(pos.above().north());
        positions.add(pos.above().south());
        positions.add(pos.above().east());
        positions.add(pos.above().west());
        for(BlockPos p : positions) {
            Block block = this.mob.level.getBlockState(p).getBlock();
            if(block == Blocks.OAK_LEAVES
            || block == Blocks.SPRUCE_LEAVES
            || block == Blocks.BIRCH_LEAVES
            || block == Blocks.JUNGLE_LEAVES
            || block == Blocks.ACACIA_LEAVES
            || block == Blocks.DARK_OAK_LEAVES) {
                this.mob.level.destroyBlock(p, false);
            }
        }
    }

    protected boolean findNearestBlock() {
        int searchRange = 8;
        int verticalSearchRange = 3;
        int verticalSearchStart = 1;
        BlockPos mobPos = this.mob.blockPosition();
        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

        for(int k = verticalSearchStart; k <= verticalSearchRange; k = k > 0 ? -k : 1 - k) {
            for(int radius = 0; radius < searchRange; ++radius) {
                for(int x = 0; x <= radius; x = x > 0 ? -x : 1 - x) {
                    for(int z = x < radius && x > -radius ? radius : 0; z <= radius; z = z > 0 ? -z : 1 - z) {
                        searchPos.setWithOffset(mobPos, x, k - 1, z);
                        if (this.mob.isWithinRestriction(searchPos) && this.isValidTarget(this.mob.level, searchPos)) {
                            this.vein = getVein(this.mob.level, searchPos);
                            this.blockPos = this.vein.remove(0);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    protected boolean isValidTarget(LevelReader level, BlockPos blockPos) {
        ChunkAccess chunkaccess = level.getChunk(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getZ()), ChunkStatus.FULL, false);
        if (chunkaccess == null) {
            return false;
        } else {
            if (!chunkaccess.getBlockState(blockPos).canEntityDestroy(level, blockPos, this.mob)) return false;
            return isAllowedBlock(chunkaccess.getBlockState(blockPos).getBlock());
        }
    }

    protected boolean isAllowedBlock(Block block) {
        return block == Blocks.OAK_LOG || block == Blocks.SPRUCE_LOG || block == Blocks.BIRCH_LOG || block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG;
    }

    private ArrayList<BlockPos> getVein(LevelReader level, BlockPos blockPos) {
        ArrayList<BlockPos> vein = new ArrayList<>();
        vein.add(blockPos);
        ArrayList<BlockPos> toCheck = new ArrayList<>();
        ArrayList<BlockPos> checked = new ArrayList<>();
        toCheck.add(blockPos);
        while(!toCheck.isEmpty()) {
            BlockPos pos = toCheck.remove(0);
            checked.add(pos);
            for(int x = -1; x <= 1; x++) {
                for(int y = -1; y <= 1; y++) {
                    for(int z = -1; z <= 1; z++) {
                        BlockPos newPos = pos.offset(x, y, z);
                        if(checked.contains(newPos)) {
                            continue;
                        }
                        if(!vein.contains(newPos) && isAllowedBlock(level.getBlockState(newPos).getBlock())) {
                            vein.add(newPos);
                            toCheck.add(newPos);
                        }
                    }
                }
            }
        }
        return vein;
    }
    
}
