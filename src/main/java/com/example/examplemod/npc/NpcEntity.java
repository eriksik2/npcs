package com.example.examplemod.npc;

import com.example.examplemod.npc.NpcData.Gender;
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

    static final EntityDataAccessor<NpcRenderData> npcRenderData = SynchedEntityData.defineId(NpcEntity.class, Registration.NPC_RENDER_DATA_SERIALIZER.get());
    public Integer npcId = null;
    public NpcData npcData = null;


    public NpcEntity(EntityType<NpcEntity> type, Level level) {
        super(type, level);
    }

    public NpcRenderData getRenderData() {
        return entityData.get(npcRenderData);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(npcRenderData, new NpcRenderData(Gender.MALE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag data) {
        super.readAdditionalSaveData(data);
        npcId = data.getInt("npc_id");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag data) {
        super.addAdditionalSaveData(data);
        data.putInt("npc_id", npcId);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if(!level.isClientSide) {
            NpcManager worldData = NpcManager.get(level);
            worldData.registerNpcEntity(this);
            entityData.set(npcRenderData, new NpcRenderData(npcData.getGender()));
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if(!level.isClientSide) {
            RemovalReason reason = this.getRemovalReason();
            boolean shouldSave = reason==null ? true : reason.shouldSave();
            boolean shouldDestroy = reason==null ? false : reason.shouldDestroy();
            NpcManager worldData = NpcManager.get(level);
            if(shouldSave) {
                worldData.unregisterNpcEntity(this);
            }
            if(shouldDestroy) {
                worldData.removeNpc(npcId);
            }
        }
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        NetworkHooks.openScreen((ServerPlayer) player, this, getOnPos());
        return super.mobInteract(player, hand);
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
