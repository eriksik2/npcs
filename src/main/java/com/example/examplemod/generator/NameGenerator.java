package com.example.examplemod.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.examplemod.npc.NpcData;




public class NameGenerator extends Generator<String> {

    private Generator<NpcData.Gender> gender;

    public NameGenerator(Generator<NpcData.Gender> gender) {
        this.gender = gender;
    }

    public NameGenerator(NpcData.Gender gender) {
        this.gender = Generator.value(gender);
    }

    public NameGenerator() {
        this.gender = null;
    }

    @Override
    public String build(Map<String, Object> subvalues) {
        NpcData.Gender gender = this.gender == null ? null : this.gender.generate();
        List<String> cands = new ArrayList<String>();
        if(gender == null || gender == NpcData.Gender.MALE) {
            String[] names = {"Mateo","Bautista","Juan","Felipe","Bruno","Noah","Benicio","Thiago","Ciro","Liam"};
            cands.addAll(List.of(names));
        }
        if(gender == null || gender == NpcData.Gender.FEMALE) {
            String[] names = {"Emma","Olivia","Martina","Isabella","Alma","Catalina","Mia","Ambar","Victoria","Delfina"};
            cands.addAll(List.of(names));
        }
        int index = (int) Math.floor(cands.size()*Math.random());
        return cands.get(index);
    }

    
}
