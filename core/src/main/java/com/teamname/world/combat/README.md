com.teamname.world.combat
│
├── core                <-- 戦闘のルールや管理を行う中心部分
│   ├── CombatManager.java    (変更: スキル引数に対応)
│   ├── ICombatant.java       (変更: getSkills追加)
│   ├── TurnOrder.java
│   └── CombatAction.java
│
├── model               <-- データ定義 (技、職業の基本など)
│   ├── Skill.java            (新規: 技データ)
│   └── BaseCharacter.java    (新規: キャラクター共通処理)
│
├── characters          <-- 具体的なキャラクターたち
│   ├── jobs            <-- 味方の職業
│   │   ├── Fighter.java      (新規)
│   │   ├── Wizard.java       (新規)
│   │   └── ...
│   └── enemies         <-- 敵の種類
│       ├── Slime.java        (新規)
│       ├── Dragon.java       (新規)
│       └── ...
│
└── ui                  <-- 画面表示・操作
└── VisualCombatScreen.java (変更: メニュー選択の実装)
