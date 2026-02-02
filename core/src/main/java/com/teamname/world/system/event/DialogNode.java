package com.teamname.world.system.event;

import java.util.ArrayList;

public class DialogNode {
    public String id;
    public String text;
    public String speakerName;
    public ArrayList<DialogOption> options;
    
    // Optional: Event ID to trigger when this node is reached or finished
    public String triggerEventId; 
}
