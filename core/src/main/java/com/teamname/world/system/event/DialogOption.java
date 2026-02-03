package com.teamname.world.system.event;

public class DialogOption {
    public String text;
    public String nextNodeId; // The ID of the next dialog node to jump to. Null if end of dialog.

    public DialogOption() {}

    public DialogOption(String text, String nextNodeId) {
        this.text = text;
        this.nextNodeId = nextNodeId;
    }
}
