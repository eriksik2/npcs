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

public class NpcWorldData extends SavedData {

    private Level level;
    private int nextTeamId;
    private int nextNpcId;
    private HashMap<Integer, NpcTeam> teams;
    public HashMap<Integer, Integer> npcIdToEntityIdMap;

    // This function can be used to get access to the data for a given level. It can only be called server-side!
    @Nonnull
    public static NpcWorldData get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        NpcWorldData data = storage.computeIfAbsent(NpcWorldData::new, NpcWorldData::new, "npc_world_data");
        if(data == null) throw new RuntimeException("Could not get npc world data.");
        data.level = level;
        return data;
    }
    
    // This constructor is called for a new world
    public NpcWorldData() {
        nextNpcId = 0;
        teams = new HashMap<Integer, NpcTeam>();
        npcIdToEntityIdMap = new HashMap<Integer, Integer>();
    }

    // This constructor is called when loading from disk
    public NpcWorldData(CompoundTag data) {
        nextNpcId = data.getInt("next_npc_id");
        nextTeamId = data.getInt("next_team_id");
        teams = new HashMap<Integer, NpcTeam>();
        CompoundTag teamsTag = data.getCompound("teams");
        for(String teamKey : teamsTag.getAllKeys()) {
            Integer key = Integer.decode(teamKey);
            Tag teamTag = teamsTag.get(teamKey);
            teams.put(key, new NpcTeam((CompoundTag)teamTag, this));
        }
        npcIdToEntityIdMap = new HashMap<Integer, Integer>();
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
         return data;
    }

    public int getUniqueNpcId() {
        int id = nextNpcId;
        ++nextNpcId;
        setDirty();
        return id;
    }

    public NpcData getNpcData(int npcId) {
        Integer entityId = npcIdToEntityIdMap.get(npcId);
        if(entityId != null) {
            NpcEntity entity = (NpcEntity)level.getEntity(entityId);
            if(entity != null) {
                return entity.getNpcData();
            }
        }
        return null;
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
        String playerName = player.getName().getString();
        NpcTeam team = null;
        for(Integer key : teams.keySet()) {
            NpcTeam t = teams.get(key);
            if(t.getLeader() == playerName) {
                team = t;
                break;
            }
        }
        if(team == null) {
            team = createTeam()
                .setLeader(playerName);
        }
        return team;
    }
}
