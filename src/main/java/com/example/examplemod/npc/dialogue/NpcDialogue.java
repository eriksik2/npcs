package com.example.examplemod.npc.dialogue;

import java.util.List;

import com.example.examplemod.networking.AddNpcToPlayerTeam;
import com.example.examplemod.networking.Messages;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.npc.role.ToggleNpcHasRoleMsg;
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
        );

        root.withTransition("Assign role...", 
            () -> {
                var tasksNode = createNode("")
                    .canEnterIf(() -> {
                        if(npcData == null) return false;
                        if(teamData == null) return false;
                        if(npcData.teamId != teamData.getId()) return false;
                        List<Integer> hasRoles = teamData.getRolesOf(npcData.npcId);
                        if(hasRoles.size() == teamData.getRoles().size()) return false;
                        return true;
                    });
                if(teamData == null) return tasksNode;
                if(npcData == null) return tasksNode;
                List<Integer> hasRoles = teamData.getRolesOf(npcData.npcId);
                for(NpcRole role : teamData.getRoles()) {
                    if(hasRoles.contains(role.getId())) continue;
                    tasksNode.withTransition(role.getName(), 
                        createNode("Okay, I'll do " + role.getName() + ".")
                            .onEnter(() -> {
                                Messages.sendToServer(new ToggleNpcHasRoleMsg(teamData.getId(), role.getId(), npcData.npcId));
                            })
                            .withTransition("Great!", root)
                    );
                }
                tasksNode.withTransition("Nevermind.", root);
                return tasksNode;
            }
        );

        root.withTransition("Unassign role...", 
            () -> {
                var tasksNode = createNode("")
                    .canEnterIf(() -> {
                        if(npcData == null) return false;
                        if(teamData == null) return false;
                        if(npcData.teamId != teamData.getId()) return false;
                        List<Integer> hasRoles = teamData.getRolesOf(npcData.npcId);
                        if(hasRoles.size() == 0) return false;
                        return true;
                    });
                if(teamData == null) return tasksNode;
                if(npcData == null) return tasksNode;
                List<Integer> hasRoles = teamData.getRolesOf(npcData.npcId);
                for(NpcRole role : teamData.getRoles()) {
                    if(!hasRoles.contains(role.getId())) continue;
                    tasksNode.withTransition(role.getName(), 
                        createNode("Okay, I wont do " + role.getName() + " anymore.")
                            .onEnter(() -> {
                                Messages.sendToServer(new ToggleNpcHasRoleMsg(teamData.getId(), role.getId(), npcData.npcId));
                            })
                            .withTransition("Great!", root)
                    );
                }
                tasksNode.withTransition("Nevermind.", root);
                return tasksNode;
            }
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
