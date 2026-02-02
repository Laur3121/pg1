package com.teamname.world.system;

public class GameState {
    // プレイヤーのステータス（本来はもっと詳しく作りますが、まずはHPから）
    // パーティメンバーリスト
    public java.util.List<Character> partyMembers;

    // 所持金（共通）
    public int gold;

    // ゲーム進行フラグ（例: イベントIDなど）
    public int eventId;

    public GameState() {
        this.partyMembers = new java.util.ArrayList<>();
        this.gold = 100;
        this.eventId = 0;
    }

    public void addMember(Character character) {
        if (partyMembers == null) {
            partyMembers = new java.util.ArrayList<>();
        }
        partyMembers.add(character);
    }

    public Character getLeader() {
        if (partyMembers == null || partyMembers.isEmpty())
            return null;
        return partyMembers.get(0);
    }
}
