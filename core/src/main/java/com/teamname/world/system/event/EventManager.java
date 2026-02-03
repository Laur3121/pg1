package com.teamname.world.system.event;

import com.teamname.world.AdventureRPG;
import com.teamname.world.system.GameState;
import com.teamname.world.system.UIManager;
import java.util.Map;

public class EventManager {
    private AdventureRPG game;
    private GameState gameState;
    private UIManager uiManager;
    private Map<String, DialogNode> dialogData;

    // Current state
    private DialogNode currentDialogNode;

    public EventManager(AdventureRPG game) {
        this.game = game;
    }

    public void initialize(GameState gameState, UIManager uiManager, Map<String, DialogNode> dialogData) {
        this.gameState = gameState;
        this.uiManager = uiManager;
        this.dialogData = dialogData;
    }

    public void startEvent(String eventId) {
        // Simple event parser:
        // if starts with "DIALOG_", show dialog
        // if starts with "QUEST_START_", start quest
        // etc.
        
        System.out.println("Event Trigger: " + eventId);

        if (eventId.startsWith("DIALOG_")) {
            String dialogId = eventId.substring(7); // remove "DIALOG_"
            startDialog(dialogId);
        } else if (eventId.startsWith("QUEST_START_")) {
            int questId = Integer.parseInt(eventId.substring(12));
            if (gameState.questManager != null) {
                gameState.questManager.startQuest(questId);
                // Notification?
                uiManager.addBattleLog("Quest Started!"); // Reuse battle log or add notification system
            }
        } else if (eventId.startsWith("FLAG_SET_")) {
            // Format: FLAG_SET_KEY_VALUE (Simple parser or split)
            // Let's assume just a flag key for now or splits
            // "FLAG_SET_MISSION_ACCEPTED_1"
            String[] parts = eventId.split("_");
            if (parts.length >= 4) {
                String key = parts[2]; // Simplified assumption
                // This parsing is too fragile, better to rely on dedicated methods or structured event data.
                // For now, let's just support simple flag setting via method call if needed, this is string trigger.
            }
        }
    }

    public void startDialog(String dialogId) {
        if (dialogData == null || !dialogData.containsKey(dialogId)) {
            System.err.println("Dialog ID " + dialogId + " not found.");
            return;
        }

        DialogNode node = dialogData.get(dialogId);
        showDialogNode(node);
    }

    private void showDialogNode(DialogNode node) {
        this.currentDialogNode = node;

        // Pass to UIManager to display
        // We need to update UIManager/DialogUI to support options
        if (node.options != null && !node.options.isEmpty()) {
            uiManager.showDialogWithOptions(node.speakerName, node.text, node.options, this);
        } else {
            // Even for linear dialogs, we must pass 'this' (EventManager) so that
            // onDialogFinished() is called when the dialog closes, allowing triggerEventId to fire.
            uiManager.showDialogWithOptions(node.speakerName, node.text, null, this);
            // If no options, it's a linear dialog. 
            // We usually wait for user input to close or advance.
            // If there's a triggerEventId, we should trigger it AFTER this dialog closes.
            // But DialogUI currently handles closing. We might need a callback or change how DialogUI works.
            // For now, let's assume specific "Next" behavior is handled by DialogUI calling back to EventManager or just closing.
            
            // To support "Trigger Event after this text", we could use a transparent option "Next" or handle it on close.
        }
        
        // Immediate trigger? Or after close? usually after close if it's a story event.
        // If triggerEventId is present and there are NO options, we might want to trigger it when dialog closes.
    }

    // Called by DialogUI when an option is selected
    public void onDialogOptionSelected(int optionIndex) {
        if (currentDialogNode == null || currentDialogNode.options == null) return;

        if (optionIndex >= 0 && optionIndex < currentDialogNode.options.size()) {
            DialogOption option = currentDialogNode.options.get(optionIndex);
            
            // Allow option to trigger next node
            if (option.nextNodeId != null) {
                startDialog(option.nextNodeId);
            } else {
                // End of conversation
                uiManager.getDialogUI().hide();
            }
        }
    }
    
    // Called when standard dialog is closed/advanced
    public void onDialogFinished() {
        if (currentDialogNode != null && currentDialogNode.triggerEventId != null) {
             startEvent(currentDialogNode.triggerEventId);
        }
        currentDialogNode = null;
    }
}
