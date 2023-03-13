package com.example.examplemod.npc.dialogue;

import com.example.examplemod.networking.AddNpcToPlayerTeam;
import com.example.examplemod.networking.Messages;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.team.NpcTeam;

public class NpcDialogue extends DialogueGraph<String, String> {

    private boolean isOnTeam = false;
    private String task;

    private NpcData npcData;
    private NpcTeam teamData;

    @Override
    protected DialogueNodeBuilder<String, String> buildDialogueGraph() {
        var root = createNode(() -> {
            return "Hi.";
            //if(npcData.teamId == teamData.getId()) {
            //    return "Hey boss!";
            //} else {
            //    return "Hello...";
            //}
        });
            //.canEnterIf(() -> {
            //    if(npcData == null) return false;
            //    if(teamData == null) return false;
            //    return true;
            //});

        // Ask to join team
        root.withTransition("Ask him to join you.",
            createNode("Sure, I'll join you.")
                .canEnterIf(() -> {
                    if(npcData == null) return false;
                    if(teamData == null) return false;
                    if(npcData.teamId == teamData.getId()) return false;
                    return true;
                })
                .onEnter(() -> {
                    Messages.sendToServer(new AddNpcToPlayerTeam(npcData.npcId));
                    npcData = null;
                })
                .withTransition("Great!", root)
        );

        // Ask to leave team
        root.withTransition("Tell him you want him to leave your group.",
            createNode("Okay...")
                .canEnterIf(() -> {
                    if(npcData == null) return false;
                    if(teamData == null) return false;
                    if(npcData.teamId != teamData.getId()) return false;
                    return true;
                })
                .onEnter(() -> isOnTeam = false)
                .withTransition("Yeah, leave.", root)
        );

        // Ask about task
        root.withTransition("Ask him his job.",
            createNode(() -> {
                if(task == null) {
                    return "I don't have a job.";
                } else {
                    return "I'm a " + task + "!";
                }
            })
                .canEnterIf(() -> isOnTeam)
                .withTransition("Oh, okay.", root)
                .withTransition("Assign him a job...",
                    createNode("")
                        .withTransition("Farmer",
                            createNode("I'll be a farmer!")
                                .onEnter(() -> task = "farmer")
                                .withTransition("Great!", root)
                        )
                        .withTransition("Woodcutter",
                            createNode("I'll be a woodcutter!")
                                .onEnter((from, tran, to) -> task = "woodcutter")
                                .withTransition("Great!", root)
                        )
                )
        );

        return root;
    }

    public void setNpcData(NpcData npcData) {
        this.npcData = npcData;
    }

    public void setTeamData(NpcTeam teamData) {
        this.teamData = teamData;
    }
}
