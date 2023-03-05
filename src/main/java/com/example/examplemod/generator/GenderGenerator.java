package com.example.examplemod.generator;

import java.util.Map;

import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcData.Gender;

public class GenderGenerator extends Generator<NpcData.Gender>{

    @Override
    protected Gender build(Map<String, Object> subvalues) {
        NpcData.Gender[] opts = {NpcData.Gender.MALE, NpcData.Gender.FEMALE};
        int index = (int) Math.floor(opts.length*Math.random());
        return opts[index];
    }
    
}
