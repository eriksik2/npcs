package com.example.examplemod.npc.dialogue;

import com.example.examplemod.npc.NpcData;

public class NpcDialogueHelper {
    static enum SubjectOrObject {
        SUBJECT,
        OBJECT
    }

    static enum Person {
        FIRST,
        SECOND,
        THIRD
    }


    
    private NpcData npc;

    private SubjectOrObject subjectOrObject = SubjectOrObject.SUBJECT;
    private Person person = Person.THIRD;
    public NpcDialogueHelper(NpcData npc) {
        this.npc = npc;
    }

    public String getPronoun() {
        return switch(npc.gender) {
            case MALE -> switch(subjectOrObject) {
                case SUBJECT -> switch(person) {
                    case FIRST -> "I";
                    case SECOND -> "you";
                    case THIRD -> "he";
                };
                case OBJECT -> switch(person) {
                    case FIRST -> "me";
                    case SECOND -> "you";
                    case THIRD -> "him";
                };
            };
            case FEMALE -> switch(subjectOrObject) {
                case SUBJECT -> switch(person) {
                    case FIRST -> "I";
                    case SECOND -> "you";
                    case THIRD -> "she";
                };
                case OBJECT -> switch(person) {
                    case FIRST -> "me";
                    case SECOND -> "you";
                    case THIRD -> "her";
                };
            };
        };
    }

    
}
