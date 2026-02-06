# クラス間関係図 (Class Diagram)

```mermaid
classDiagram
    %% Main Game Class
    class AdventureRPG {
        -SpriteBatch batch
        -DataLoader dataLoader
        -Inventory inventory
        -GameState gameState
        -UIManager uiManager
        -AudioManager audioManager
        +create()
        +render()
        +dispose()
    }

    %% Screens
    class GameScreen {
        -PlayerEntity player
        -List~NPCEntity~ npcs
        -float mapPixelWidth
        -float mapPixelHeight
        +render(float delta)
        +update(float delta)
    }

    class VisualCombatScreen {
        -CombatManager combatManager
        -List~ICombatant~ party
        -List~ICombatant~ enemies
        +startBattle()
        +render(float delta)
    }

    class TitleScreen {
        +render(float delta)
    }

    %% System / Data
    class GameState {
        +List~Character~ partyMembers
        +int gold
        +Map~String, Integer~ flags
        +QuestManager questManager
        +EventManager eventManager
    }

    class DataLoader {
        +loadItems()
        +loadQuests()
    }

    class UIManager {
        -InventoryUI inventoryUI
        -StatusUI statusUI
        -MenuTab menuTab
        +updateAndRender(float delta)
    }

    class GameInitializer {
        +initialize(AdventureRPG game)
    }

    %% Entities (World)
    class Entity {
        #float x, y
        #float width, height
        +render(SpriteBatch batch)
    }

    class PlayerEntity {
        +update(float delta)
        +handleInput()
    }

    class NPCEntity {
        +interact()
    }

    class MonsterEntity {
        -List~ICombatant~ enemies
    }

    %% Combat System
    class CombatManager {
        -List~ICombatant~ party
        -List~ICombatant~ enemies
        +startBattle()
        +processTurn()
    }

    class ICombatant {
        <<interface>>
        +getName()
        +getCurrentHP()
        +attack(ICombatant target)
    }

    class Character {
        +String name
        +int level
        +int hp, mp
        +equip(Item item)
    }

    class Monster {
        +String name
        +int hp
    }

    class DemonKing {
        +specialAttack()
    }

    %% Relationships
    AdventureRPG "1" *-- "1" GameState
    AdventureRPG "1" *-- "1" DataLoader
    AdventureRPG "1" *-- "1" UIManager
    AdventureRPG "1" *-- "1" AudioManager
    AdventureRPG "1" o-- "1" GameScreen
    AdventureRPG "1" o-- "1" VisualCombatScreen
    AdventureRPG "1" o-- "1" TitleScreen

    GameScreen *-- "1" PlayerEntity
    GameScreen o-- "*" NPCEntity
    GameScreen o-- "*" MonsterEntity

    GameState "1" o-- "*" Character : partyMembers
    GameState "1" *-- "1" QuestManager
    GameState "1" *-- "1" EventManager

    ICombatant <|.. Character
    ICombatant <|.. Monster
    Monster <|-- DemonKing

    Entity <|-- PlayerEntity
    Entity <|-- NPCEntity
    Entity <|-- MonsterEntity

    VisualCombatScreen *-- "1" CombatManager
    VisualCombatScreen o-- "*" ICombatant

    UIManager o-- GameState
```

## 解説
- **AdventureRPG**: ゲームの中心となるクラスです。画面（Screen）の切り替えや、主要なマネージャー（UI, Audio, Data, State）を保持します。
- **GameState**: ゲームの進行状況（現在のパーティ、フラグ、クエスト状況など）を一元管理します。
- **GameScreen**: ワールドマップ探索画面を担当します。プレイヤーやNPC、敵シンボル（Entity）の更新・描画を行います。
- **Entity**: マップ上のオブジェクトの基底クラスです。
- **VisualCombatScreen**: 戦闘画面を担当します。`CombatManager` を使用して戦闘のロジックを処理し、`ICombatant` インターフェースを通してキャラクターやモンスターを操作します。
- **UIManager**: インベントリやステータス画面などのオーバーレイUIを管理します。
