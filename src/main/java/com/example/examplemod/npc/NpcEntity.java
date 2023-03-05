package com.example.examplemod.npc;

import com.example.examplemod.setup.Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class NpcEntity extends PathfinderMob implements MenuProvider {

    static final EntityDataAccessor<NpcData> npcDataSync = SynchedEntityData.defineId(NpcEntity.class, Registration.NPC_DATA_SERIALIZER.get());
    private Integer npcId = null;

    public NpcEntity(EntityType<NpcEntity> type, Level level) {
        super(type, level);
    }

    public NpcData getNpcData() {
        return this.entityData.get(npcDataSync);
    }

    public void setNpcData(NpcData newData) {
        this.entityData.set(npcDataSync, newData);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(npcDataSync, new NpcData());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag data) {
        super.readAdditionalSaveData(data);
        npcId = data.getInt("npc_id");
        if(data.contains("npc_data")) {
            setNpcData(new NpcData(data));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag data) {
        super.addAdditionalSaveData(data);
        data.putInt("npc_id", npcId);
        
        data.put("npc_data", getNpcData().toCompoundTag());
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if(!level.isClientSide) {
            NpcWorldData worldData = NpcWorldData.get(level);
            if(npcId == null) {
                npcId = worldData.getUniqueNpcId();
                NpcData newData = entityData.get(npcDataSync).copy();
                newData.npcId = npcId;
                entityData.set(npcDataSync, newData);
            }
            if(!getNpcData().isInitialized()) {
                setNpcData(NpcData.generate());
            }
            worldData.npcIdToEntityIdMap.put(npcId, getId());
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if(!level.isClientSide) {
            NpcWorldData worldData = NpcWorldData.get(level);
            worldData.npcIdToEntityIdMap.remove(npcId);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        NetworkHooks.openScreen((ServerPlayer) player, this, getOnPos());
        return super.mobInteract(player, hand);
    }


    public void addToTeam(NpcTeam team) {
        NpcData data = getNpcData();
        if(data.teamId == team.getId()) return;
        if(data.teamId != null) {
            NpcWorldData worldData = NpcWorldData.get(level);
            NpcTeam oldTeam = worldData.getTeam(data.teamId);
            oldTeam.removeNpcId(npcId);
        }
        NpcData newData = data.copy();
        newData.teamId = team.getId();
        setNpcData(newData);
        team.addNpcId(npcId);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Npc Menu");
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        BlockPos pos = getOnPos();
        return new NpcInteractMenu(windowId, pos, playerInventory, playerEntity, this);
    }
    

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }
}
