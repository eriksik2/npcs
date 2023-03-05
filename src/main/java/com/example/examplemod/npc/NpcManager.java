package com.example.examplemod.npc;

import java.util.HashMap;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class NpcManager extends SavedData {

    private int nextTeamId;
    private int nextNpcId;
    private HashMap<Integer, NpcTeam> teams;
    public HashMap<Integer, NpcEntity> loadedNpcs;
    public HashMap<Integer, PassiveNpcData> unloadedNpcs;

    // This function can be used to get access to the data for a given level. It can only be called server-side!
    @Nonnull
    public static NpcManager get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        NpcManager data = storage.computeIfAbsent(NpcManager::new, NpcManager::new, "npc_world_data");
        if(data == null) throw new RuntimeException("Could not get npc world data.");
        return data;
    }
    
    // This constructor is called for a new world
    public NpcManager() {
        nextNpcId = 0;
        teams = new HashMap<Integer, NpcTeam>();
        loadedNpcs = new HashMap<Integer, NpcEntity>();
        unloadedNpcs = new HashMap<Integer, PassiveNpcData>();
    }

    // This constructor is called when loading from disk
    public NpcManager(CompoundTag data) {
        nextNpcId = data.getInt("next_npc_id");
        nextTeamId = data.getInt("next_team_id");
        teams = new HashMap<Integer, NpcTeam>();
        CompoundTag teamsTag = data.getCompound("teams");
        for(String teamKey : teamsTag.getAllKeys()) {
            Integer key = Integer.decode(teamKey);
            Tag teamTag = teamsTag.get(teamKey);
            teams.put(key, new NpcTeam((CompoundTag)teamTag, this));
        }
        unloadedNpcs = new HashMap<Integer, PassiveNpcData>();
        CompoundTag unloadedNpcsTag = data.getCompound("npcs");
        for(String npcKey : unloadedNpcsTag.getAllKeys()) {
            Integer key = Integer.decode(npcKey);
            Tag npcTag = unloadedNpcsTag.get(npcKey);
            unloadedNpcs.put(key, new PassiveNpcData((CompoundTag)npcTag));
        }
        loadedNpcs = new HashMap<Integer, NpcEntity>();
    }

    @Override
    public CompoundTag save(CompoundTag data) {
        data.putInt("next_npc_id", nextNpcId);
        data.putInt("next_team_id", nextTeamId);
        CompoundTag teamsTag = new CompoundTag();
        for(Integer key : teams.keySet()) {
           teamsTag.put(key.toString(), teams.get(key).toCompoundTag());
        }
        data.put("teams", teamsTag);

        CompoundTag npcsTag = new CompoundTag();
        for(Integer key : unloadedNpcs.keySet()) {
            npcsTag.put(key.toString(), unloadedNpcs.get(key).toCompoundTag());
        }
        for(Integer key : loadedNpcs.keySet()) {
            PassiveNpcData passiveData = new PassiveNpcData(loadedNpcs.get(key));
            npcsTag.put(key.toString(), passiveData.toCompoundTag());
        }
        data.put("npcs", npcsTag);
        return data;
    }

    public void registerNpcEntity(NpcEntity entity) {
        Integer npcId = entity.npcId;
        if(npcId == null) {
            npcId = getUniqueNpcId();
            entity.npcId = npcId;
            entity.npcData = NpcData.generate();
            entity.npcData.npcId = npcId;
            loadedNpcs.put(npcId, entity);
        } else {
            PassiveNpcData data = unloadedNpcs.get(npcId);
            if(data != null) {
                entity.npcData = data.data;
                unloadedNpcs.remove(npcId);
            } else {
                entity.npcData = NpcData.generate();
            }
            loadedNpcs.put(npcId, entity);
        }
        setDirty();
    }

    public void unregisterNpcEntity(NpcEntity entity) {
        Integer npcId = entity.npcId;
        if(npcId != null) {
            loadedNpcs.remove(npcId);
            unloadedNpcs.put(npcId, new PassiveNpcData(entity));
            setDirty();
        }
    }

    public int getUniqueNpcId() {
        int id = nextNpcId;
        ++nextNpcId;
        setDirty();
        return id;
    }

    public NpcData getNpcData(int npcId) {
        NpcEntity entity = loadedNpcs.get(npcId);
        if(entity != null) {
            return entity.npcData;
        }
        PassiveNpcData data = unloadedNpcs.get(npcId);
        if(data != null) {
            return data.data;
        }
        return null;
    }

    public NpcEntity getNpcEntity(int npcId) {
        return loadedNpcs.get(npcId);
    }

    public void addNpcToTeam(NpcData data, NpcTeam team) {
        if(data.teamId != null) {
            if(data.teamId == team.getId()) return;
            NpcTeam oldTeam = teams.get(data.teamId);
            if(oldTeam != null) {
                oldTeam.removeNpcId(data.npcId);
            }
        }
        team.addNpcId(data.npcId);
        data.teamId = team.getId();
        setDirty();
    }

    public void removeNpcFromTeam(NpcData data) {
        if(data.teamId != null) {
            NpcTeam team = teams.get(data.teamId);
            if(team != null) {
                team.removeNpcId(data.npcId);
            }
            data.teamId = null;
            setDirty();
        }
    }

    public NpcTeam createTeam() {
        Integer id = nextTeamId;
        ++nextTeamId;
        NpcTeam team = new NpcTeam(id, this);
        teams.put(id, team);
        setDirty();
        return team;
    }

    public NpcTeam getTeam(Integer teamId) {
        return teams.get(teamId);
    }

    public NpcTeam getPlayerTeam(Player player) {
        NpcTeam team = null;
        for(Integer key : teams.keySet()) {
            NpcTeam t = teams.get(key);
            if(t.isOwner(player)) {
                team = t;
                break;
            }
        }
        if(team == null) {
            team = createTeam()
                .addOwner(player);
        }
        return team;
    }
}
