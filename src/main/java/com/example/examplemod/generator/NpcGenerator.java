package com.example.examplemod.generator;

import java.util.Map;

import com.example.examplemod.npc.NpcData;

import java.util.HashMap;

public class NpcGenerator extends Generator<NpcData> {

    @Override
    protected Map<String, Generator<? extends Object>> getSubgenerators() {
        Map<String, Generator<? extends Object>> map = new HashMap<String, Generator<? extends Object>>();
        GenderGenerator gender = new GenderGenerator();
        map.put("gender", gender);
        map.put("name", new NameGenerator(gender));
        return map;
    }

    @Override
    protected NpcData build(Map<String, Object> values) {
        NpcData.Gender gender = (NpcData.Gender)values.get("gender");
        String name = (String)values.get("name");
        return new NpcData(gender, name);
    }
    
}
