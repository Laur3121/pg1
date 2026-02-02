package com.teamname.world.system;

public class GameState {
    // プレイヤーのステータス（本来はもっと詳しく作りますが、まずはHPから）
    // パーティメンバーリスト
    public java.util.List<Character> partyMembers;

    // 所持金（共通）
    public int gold;

    // ゲーム進行フラグ（マップで管理）
    public java.util.Map<String, Integer> flags;

    public GameState() {
        this.partyMembers = new java.util.ArrayList<>();
        this.gold = 100;
        this.flags = new java.util.HashMap<>();
    }

    public void setFlag(String key, int value) {
        flags.put(key, value);
    }

    public int getFlag(String key) {
        return flags.getOrDefault(key, 0);
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
