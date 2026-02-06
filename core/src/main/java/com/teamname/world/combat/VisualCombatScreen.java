package com.teamname.world.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion; // Added import
import com.badlogic.gdx.audio.Music; // Added for BGM
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.teamname.world.combat.core.CombatManager;
import com.teamname.world.combat.core.ICombatant;
import com.teamname.world.combat.core.TurnOrder;
import com.teamname.world.combat.core.CombatAction;
import com.teamname.world.combat.TestCharacter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ビジュアル戦闘画面
 * 修正版: 名前横にHP数値表示・ダメージポップアップ追加
 */
public class VisualCombatScreen implements Screen {

    // --- 定数定義 ---
    private static final float TURN_DURATION = 3.0f;
    private static final int MAX_LOG_LINES = 8;
    private static final float CHAR_SIZE = 80f;

    private static final float Y_OFFSET = 120f;
    private static final float HP_BAR_HEIGHT = 10f;
    private static final float HP_BAR_OFFSET = 15f;

    // --- 描画・システム ---
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    // --- データ ---
    private final CombatManager combatManager;
    private List<ICombatant> party;
    private List<ICombatant> enemies;
    private final List<String> combatLog;

    // --- マネージャー/ヘルパー ---
    private Map<String, TextureRegion[]> textureMap = new HashMap<>();
    private Map<String, CharacterAnimator> animatorMap = new HashMap<>();
    private Map<String, Texture> rawTextures = new HashMap<>(); // 生のTextureを保持してdispose用
    private final CutInManager cutInManager;
    private final FloatingTextManager floatingTextManager; // ダメージポップアップ管理

    // --- 状態変数 ---
    private float stateTime;
    private float turnDelay;

    // Lucky Time Fields
    private boolean isLuckyTimeActive = false;
    private float luckyTimer = 0;
    private int[] luckySlots = new int[3];
    private boolean isLuckyJackpot = false;
    private int totalTurnCounter = 0;

    private void startLuckyTime() {
        isLuckyTimeActive = true;
        luckyTimer = 0;
        isLuckyJackpot = Math.random() < 0.5; // 1/3 Chance
        addLog(">>> LUCKY TIME CHANCE! <<<");
    }

    private void updateLuckyTime(float delta) {
        luckyTimer += delta;

        // Slot Animation (Randomize numbers)
        if (luckyTimer < 2.0f) {
            for (int i = 0; i < 3; i++)
                luckySlots[i] = (int) (Math.random() * 9) + 1;
        } else {
            // Result
            if (isLuckyJackpot) {
                luckySlots[0] = 7;
                luckySlots[1] = 7;
                luckySlots[2] = 7;
            } else {
                // Force mismatch if needed, or just leave random (small chance of random match,
                // ok)
                if (luckySlots[0] == luckySlots[1] && luckySlots[1] == luckySlots[2]) {
                    luckySlots[2] = (luckySlots[2] % 9) + 1;
                }
            }

            // Play Music once if Jackpot
            if (isLuckyJackpot && luckyTimer > 2.0f && luckyTimer < 2.1f) {
                if (pachinkoMusic != null && !pachinkoMusic.isPlaying()) {
                    if (battleMusic != null && battleMusic.isPlaying())
                        battleMusic.pause();
                    if (bossMusic != null && bossMusic.isPlaying())
                        bossMusic.pause();
                    pachinkoMusic.play();
                }
            }

            // Wait 1.5s then finish
            if (luckyTimer > 3.5f) {
                isLuckyTimeActive = false;
                if (isLuckyJackpot) {
                    combatManager.setDamageMultiplier(10000000.0f);
                    addLog("JACKPOT! 777! Damage x10000000!");
                } else {
                    addLog("Miss... Normal Damage.");
                }

                // Restore BGM
                if (bossMusic != null && !bossMusic.isPlaying() && game.getGameState().isBossBattle)
                    bossMusic.play();
                else if (battleMusic != null && !battleMusic.isPlaying() && !game.getGameState().isBossBattle)
                    battleMusic.play();

                prepareTurn();
            }
        }
    }

    private void drawLuckyTime() {
        // Rainbow Strobe Background
        float t = luckyTimer * 5.0f; // Speed
        float r = (float) (Math.sin(t) + 1) / 2f;
        float g = (float) (Math.sin(t + 2) + 1) / 2f;
        float b = (float) (Math.sin(t + 4) + 1) / 2f;
        float alpha = (Math.sin(luckyTimer * 20) > 0) ? 0.6f : 0.3f; // Flash intensity

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (isLuckyJackpot && luckyTimer >= 2.0f) {
            shapeRenderer.setColor(r, g, b, alpha); // Rainbow for Jackpot (Confirmation Effect)
        } else {
            shapeRenderer.setColor(1f, 1f, 0f, alpha); // Yellow for Chance (Spinning)
        }

        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw Slots
        batch.begin();
        font.getData().setScale(5.0f);
        font.setColor(Color.RED);
        String slotText = luckySlots[0] + " " + luckySlots[1] + " " + luckySlots[2];
        GlyphLayout layout = new GlyphLayout(font, slotText);
        font.draw(batch, slotText, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() / 2);

        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        String title = "LUCKY TIME!!";
        GlyphLayout titleLayout = new GlyphLayout(font, title);
        font.draw(batch, title, (Gdx.graphics.getWidth() - titleLayout.width) / 2, Gdx.graphics.getHeight() / 2 + 100);

        font.getData().setScale(1.5f);
        batch.end();
    }

    // ゲーム本体への参照（画面切り替え用）
    private final com.teamname.world.AdventureRPG game;

    // --- デバッグUI ---
    private Stage uiStage;
    private Skin skin;

    private Music battleMusic;
    private Music bossMusic;
    private Music pachinkoMusic; // Added for Jackpot

    public VisualCombatScreen(com.teamname.world.AdventureRPG game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.5f);
        this.shapeRenderer = new ShapeRenderer();

        // this.textureMap = new HashMap<>(); // Replaced by new declaration
        // this.animatorMap = new HashMap<>(); // Replaced by new declaration
        this.combatLog = new ArrayList<>();

        this.cutInManager = new CutInManager();
        this.floatingTextManager = new FloatingTextManager(); // 初期化

        this.combatManager = new CombatManager();
        this.stateTime = 0;
        this.turnDelay = 0;

        // デバッグUIの初期化
        uiStage = new Stage(new ScreenViewport());
        skin = new Skin();
        createBasicSkin();

        TextButton winBtn = new TextButton("Debug: Win", skin);
        winBtn.setPosition(10, Gdx.graphics.getHeight() - 60);
        winBtn.setSize(120, 40);
        winBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 強制勝利
                forceEndBattle(CombatManager.BattleState.VICTORY);
            }
        });

        TextButton loseBtn = new TextButton("Debug: Lose", skin);
        loseBtn.setPosition(140, Gdx.graphics.getHeight() - 60);
        loseBtn.setSize(120, 40);
        loseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 強制敗北
                forceEndBattle(CombatManager.BattleState.DEFEAT);
            }
        });

        uiStage.addActor(winBtn);
        uiStage.addActor(loseBtn);

        loadAssets();

        try {
            pachinkoMusic = Gdx.audio.newMusic(Gdx.files.internal("music/pachinko.mp3"));
        } catch (Exception e) {
            System.err.println("Failed to load pachinko.mp3: " + e.getMessage());
        }
    }

    private void forceEndBattle(CombatManager.BattleState state) {
        // CombatManagerの状態を強制変更して戦闘終了処理へ
        // 本来はCombatManagerにメソッドを作るべきだが、簡易的に
        // ここではBattleStateを変更できない（CombatManagerの所有物）ので
        // CombatManagerにforceFinishを追加するか、あるいは…
        // ここで直接結果を描画するフラグを立てる手もあるが、
        // 次のrender呼び出しでManagerの状態が変わっていないとループする可能性がある。
        // なのでManagerにメソッドを追加するのが正しい。
        combatManager.forceFinish(state);
    }

    private void createBasicSkin() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", texture);

        skin.add("default", this.font); // 既存のフォントを使用

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = this.font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = this.font;
        textButtonStyle.up = skin.newDrawable("white", Color.GRAY); // 通常時: 灰色
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY); // 押下時: 濃い灰色
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY); // ホバー時: 薄い灰色
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = this.font;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.8f);
        skin.add("default", windowStyle);
    }

    // ========================================================================
    // 初期化・ロード処理
    // ========================================================================

    private void loadAssets() {
        // 味方 (Warrior)
        // Idle (1.png - 4.png)
        loadTextureSequence("warrior_idle", "evil Mage Pack/warriar/idle/", 4);
        loadTextureSafe("warrior_attack1", "evil Mage Pack/warriar/tile000.png");
        loadTextureSafe("warrior_attack2", "evil Mage Pack/warriar/tile001.png");
        loadTextureSafe("warrior_attack3", "evil Mage Pack/warriar/tile002.png");
        loadTextureSafe("warrior_walk1", "evil Mage Pack/warriar/runandjump/1.png");
        loadTextureSafe("warrior_walk2", "evil Mage Pack/warriar/runandjump/2.png");

        // 敵 (Evil Mage)
        loadTextureSafe("evilmage_idle", "face/tanabe.png");
        loadTextureSafe("evilmage_attack1", "evil Mage Pack/evilmage/attack/tile024.png");
        loadTextureSafe("evilmage_attack2", "evil Mage Pack/evilmage/attack/tile025.png");
        loadTextureSafe("evilmage_attack3", "evil Mage Pack/evilmage/attack/tile026.png");
        loadTextureSafe("evilmage_walk1", "evil Mage Pack/evilmage/walk/tile020.png");
        loadTextureSafe("evilmage_walk2", "evil Mage Pack/evilmage/walk/tile021.png");

        // 敵 (Archer)
        loadTextureSafe("archer_idle", "face/iwasaki.png");
        loadTextureSafe("archer_attack1", "evil Mage Pack/archer/tile030.png");
        loadTextureSafe("archer_attack2", "evil Mage Pack/archer/tile031.png");
        loadTextureSafe("archer_attack3", "evil Mage Pack/archer/tile032.png");
        loadTextureSafe("archer_walk1", "evil Mage Pack/archer/tile033.png");
        loadTextureSafe("archer_walk1", "evil Mage Pack/archer/tile033.png");
        loadTextureSafe("archer_walk2", "evil Mage Pack/archer/tile034.png");

        // フィールドモンスター (1~4)
        for (int i = 1; i <= 4; i++) {
            String prefix = String.valueOf(i);
            String folder = "free-field-enemies-pixel-art-for-tower-defense/" + i + "/";
            // 6分割して読み込む
            loadTextureSplit(prefix + "_idle", folder + "S_Walk.png", 6, 1);
            loadTextureSplit(prefix + "_walk1", folder + "S_Walk.png", 6, 1);
            loadTextureSplit(prefix + "_walk2", folder + "S_Walk.png", 6, 1);

            // 攻撃
            if (i == 1) {
                // S_Special.png もおそらく分割が必要なアニメーション。一旦6分割と仮定。
                // 1.png かもしれないが、ファイル名確認できてないので S_Special.png を使う
                // リストを見ると S_Special.png (1424B) なので S_Walk (1345B) と近い。分割されてそう。
                loadTextureSplit(prefix + "_attack1", folder + "S_Special.png", 6, 1);
            } else if (i == 2 || i == 3) {
                loadTextureSplit(prefix + "_attack1", folder + "S_Attack.png", 6, 1);
                loadTextureSplit(prefix + "_attack1", folder + "S_Walk.png", 6, 1);
            }
        }

        // Demon King
        // DeamonKing/Devil.png: 39 cols, 1 row (assumed). Using first 6 for Idle.
        loadTextureSplit("demon_king_idle", "DeamonKing/Devil.png", 6, 1);
        loadTextureSplit("demon_king_walk1", "DeamonKing/Devil.png", 6, 1);
        loadTextureSplit("demon_king_attack1", "DeamonKing/Devil.png", 6, 1);
    }

    private void loadTextureSafe(String key, String path) {
        try {
            Texture texture = new Texture(path);
            rawTextures.put(key, texture);
            // 1枚絵として登録 (1x1の配列)
            textureMap.put(key, new TextureRegion[] { new TextureRegion(texture) });
        } catch (Exception e) {
            System.err.println("Warning: Failed to load " + path);
        }
    }

    private void loadTextureSplit(String key, String path, int cols, int rows) {
        try {
            Texture texture = new Texture(path);
            rawTextures.put(key, texture);
            TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / cols, texture.getHeight() / rows);
            // 1次元配列に変換
            TextureRegion[] frames = new TextureRegion[cols * rows];
            int index = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    frames[index++] = tmp[i][j];
                }
            }
            textureMap.put(key, frames);
        } catch (Exception e) {
            System.err.println("Warning: Failed to load split texture " + path);
        }
    }

    private void loadTextureSequence(String key, String folderPath, int count) {
        try {
            TextureRegion[] frames = new TextureRegion[count];
            for (int i = 1; i <= count; i++) {
                String path = folderPath + i + ".png";
                Texture texture = new Texture(path);
                // rawTexturesに保存してdispose対象にする (キーを一意にするため連番付与)
                rawTextures.put(key + "_seq_" + i, texture);
                frames[i - 1] = new TextureRegion(texture);
            }
            textureMap.put(key, frames);
        } catch (Exception e) {
            System.err.println("Warning: Failed to load texture sequence for " + key + " in " + folderPath);
            e.printStackTrace();
        }
    }

    public void startBattle(List<ICombatant> party, List<ICombatant> enemies,
            com.teamname.world.system.GameState gameState) {
        System.out
                .println("DEBUG: startBattle called. Party size: " + party.size() + ", Enemy size: " + enemies.size());
        this.party = new ArrayList<>(party);
        this.enemies = new ArrayList<>(enemies);
        this.combatLog.clear();
        this.animatorMap.clear();

        // 手前左（Y座標を低く） -> 画面の幅10%, 高さ30%, スケール1.5倍
        // プレイヤー側は "forceType" を指定せず、getTextureKey() に任せることも可能だが、
        // 現状 Character.java が "warrior" を返すよう実装したので、forceType = null にして任せる形に変更する
        // User Request: Heroもフィールドと同じように、戦闘画面の右下(敵の近く)で表示してください。
        // Changing X from 0.1f to 0.6f
        setupAnimators(this.party, 0.6f, 0.3f, 1.5f, "party_", null);

        // 奥右（Y座標を高く） -> 画面の幅70%, 高さ60%, スケール0.8倍
        setupAnimators(this.enemies, 0.7f, 0.6f, 0.8f, "enemy_", null);

        addLog("=== Battle Start! ===");

        // BGM再生
        boolean isBoss = false;
        for (ICombatant e : enemies) {
            if ("demon_king".equals(e.getTextureKey()) || "Demon King".equals(e.getName())) {
                isBoss = true;
                break;
            }
        }

        if (isBoss) {
            game.getAudioManager().playBgm("maou_game_lastboss04.mp3", true);
        } else {
            game.getAudioManager().playBgm("maou_game_battle19.mp3", true);
        }

        combatManager.startBattle(this.party, this.enemies, gameState);

        // 入力を受け付ける
        Gdx.input.setInputProcessor(uiStage);
    }

    private void setupAnimators(List<ICombatant> combatants, float startXRatio, float startYRatio, float scale,
            String keyPrefix,
            String forceType) {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        for (int i = 0; i < combatants.size(); i++) {
            String key = keyPrefix + i;
            String type = forceType;
            if (type == null) {
                // ICombatantからキーを取得
                type = combatants.get(i).getTextureKey();
            }

            // シンプルに: 比率で配置
            float baseX = width * startXRatio;
            float baseY = height * startYRatio - i * Y_OFFSET;

            TextureRegion[] idleFrames = textureMap.get(type + "_idle");
            if (idleFrames == null)
                idleFrames = createPlaceholderTexture();

            CharacterAnimator animator = new CharacterAnimator(
                    idleFrames, baseX, baseY, CHAR_SIZE, CHAR_SIZE);

            // Special scaling for Boss
            if ("demon_king".equals(type)) {
                animator.setScale(3.0f); // 3x scale for Demon King
            } else {
                animator.setScale(scale);
            }

            // リサイズ用に比率を保存
            animator.setPositionRatios(startXRatio, startYRatio, i);

            addFramesToAnimator(animator, type, "attack", 3); // 既存メソッドの中身も変える必要あり
            addFramesToAnimator(animator, type, "walk", 2);
            animatorMap.put(key, animator);
        }
    }

    private void addFramesToAnimator(CharacterAnimator animator, String type, String motionType, int count) {
        // 先に textureMap から配列を取得して一括追加する形に変更
        // キー生成: type + "_" + motionType + "1" とかではなく、type + "_" + motionTypeX を登録済みと仮定
        // しかし loadTextureSafe/Split で登録したのは "warrior_walk1", "warrior_walk2" のように連番

        // 既存のassets (warrior/evilmage) は 連番キーで登録されている: key_1, key_2...
        // 新規のassets (1~4) も: key_walk1, key_walk2... (中身は同じ配列かもしれないが)

        // そこで、count分だけループして addFrame する
        for (int i = 1; i <= count; i++) {
            String key = type + "_" + motionType + i;
            TextureRegion[] frames = textureMap.get(key);
            if (frames != null) {
                animator.addFrame(motionType, frames);
            }
        }
    }

    // ========================================================================
    // メインループ (Update & Render)
    // ========================================================================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;
        for (CharacterAnimator animator : animatorMap.values()) {
            animator.update(delta);
        }

        // ダメージポップアップの更新
        floatingTextManager.update(delta);

        drawBattleScene(delta);

        if (cutInManager.isActive()) {
            cutInManager.update(delta);
            cutInManager.draw(batch, shapeRenderer, font);
            if (!cutInManager.isActive()) {
                executeTurnAction(cutInManager.getPendingActor());
            }
            return;
        }

        // Lucky Time Logic
        if (isLuckyTimeActive) {
            updateLuckyTime(delta);
            drawLuckyTime();
            return; // Skip normal update while lucky time is showing
        }

        if (combatManager.isBattleActive()) {
            turnDelay += delta;
            // Check for Lucky Time Trigger every 3 turns (approx, logic needs refine)
            // Better: Check at start of new turn before prepareTurn.
            // Using a simple turn counter in render might be sporadic.
            // Ideally trigger it when turnDelay is finished, BEFORE prepareTurn.
            if (turnDelay >= TURN_DURATION) {

                // Trigger Lucky Time? (Once every 3 moves for demonstration logic)
                totalTurnCounter++;
                // "Sometimes" -> 1/3 ~ 1/4 turns. Let's use % 4 == 0.
                if (totalTurnCounter % 1 == 0) {
                    startLuckyTime();
                    turnDelay = 0; // consumed by lucky time start
                    return; // Wait for next loop
                }

                turnDelay = 0;
                prepareTurn();
            }
        }

        stateTime += delta;
        for (CharacterAnimator animator : animatorMap.values()) {
            animator.update(delta);
        }
    }

    private void prepareTurn() {
        ICombatant actor = combatManager.getCurrentActor();
        if (actor == null)
            return;
        String actorKey;
        if (party.contains(actor)) {
            actorKey = "party_" + party.indexOf(actor);
        } else {
            actorKey = "enemy_" + enemies.indexOf(actor);
        }
        TextureRegion actorTextureRegion = getAnimatorTexture(actorKey);
        cutInManager.start(actor, actorTextureRegion);
    }

    private void executeTurnAction(ICombatant actor) {
        if (actor == null || !actor.isAlive())
            return;

        boolean isPartyMember = party.contains(actor);
        List<ICombatant> targets = isPartyMember ? combatManager.getEnemies() : combatManager.getParty();
        ICombatant target = targets.stream().filter(ICombatant::isAlive).findFirst().orElse(null);

        if (target != null) {
            int hpBefore = target.getCurrentHP();
            playAnimation(actor, CharacterAnimator.AnimationState.ATTACKING);
            combatManager.applyAction(actor, CombatAction.ATTACK, Arrays.asList(target));

            playAnimation(target, CharacterAnimator.AnimationState.HURT);

            int damage = hpBefore - target.getCurrentHP();
            addLog(actor.getName() + " -> " + target.getName() + " (" + damage + " dmg)");

            // --- ダメージポップアップの生成 ---
            String targetKey = isPartyMember ? "enemy_" + enemies.indexOf(target) : "party_" + party.indexOf(target);
            CharacterAnimator targetAnim = animatorMap.get(targetKey);
            if (targetAnim != null) {
                // キャラクターの頭上（少し右寄り）にポップアップ
                float popupX = targetAnim.displayX + 20;
                float popupY = targetAnim.displayY + targetAnim.height + 50;
                floatingTextManager.add(popupX, popupY, String.valueOf(damage), Color.RED);
            }
            // ---------------------------------

            // 死亡判定とクエスト通知
            if (!target.isAlive()) {
                addLog(target.getName() + " is defeated!");
                if (game.getGameState() != null && game.getGameState().questManager != null) {
                    // IDや種別判定があればベストだが、名前で判定する
                    // "KILL_" + Name
                    String condition = "KILL_" + target.getName();
                    System.out.println("Quest Condition Trigger: " + condition);
                    game.getGameState().questManager.checkProgress(condition);
                }
            }
        }
    }

    // ========================================================================
    // 描画ヘルパー
    // ========================================================================

    private void drawBattleScene(float delta) {
        batch.begin();

        // ヘッダー
        font.getData().setScale(2.0f);
        font.draw(batch, "BATTLE", 20, Gdx.graphics.getHeight() - 30);
        font.getData().setScale(1.5f);

        // キャラクター描画
        drawGroup(party, "party_");
        drawGroup(enemies, "enemy_");

        // 名前と数値の表示
        drawCharacterLabels();

        // ダメージポップアップの描画
        floatingTextManager.draw(batch, font);

        // リザルト
        // リザルト
        if (!combatManager.isBattleActive()) {
            drawBattleResult();

            // Click handling
            // If VICTORY, allow click to end battle
            if (combatManager.getBattleState() == CombatManager.BattleState.VICTORY) {
                if (Gdx.input.justTouched()) {
                    endBattle();
                }
            }
            // If DEFEAT, do NOT allow exit (Stay in battle screen as requested)
            else if (combatManager.getBattleState() == CombatManager.BattleState.DEFEAT) {
                // Do nothing, effectively trapping the user
            }
        }

        batch.end();

        // HPバーの描画
        drawOverheadHPBars();

        // デバッグUI
        uiStage.act(delta);
        uiStage.draw();
    }

    private void endBattle() {
        // Check if Demon King was defeated
        boolean isBossBattle = false;
        for (ICombatant e : enemies) {
            if ("demon_king".equals(e.getTextureKey())) {
                isBossBattle = true;
                break;
            }
        }

        if (isBossBattle
                && combatManager.getBattleState() == CombatManager.BattleState.VICTORY) {
            if (game.getGameState() != null) {
                game.getGameState().setFlag("BOSS_DEFEATED", 1);
            }
        }

        // バトルフラグを降ろす
        if (game != null) {
            game.battleflag = 0;
            // BGMをフィールドに戻す
            game.getAudioManager().playBgm("field.mp3", true);
        }
    }

    private void drawGroup(List<ICombatant> group, String keyPrefix) {
        for (int i = 0; i < group.size(); i++) {
            ICombatant member = group.get(i);
            CharacterAnimator animator = animatorMap.get(keyPrefix + i);
            if (animator != null) {
                if (member.isAlive()) {
                    // System.out.println("DEBUG: Drawing " + keyPrefix + i + " at " +
                    // animator.displayX + "," + animator.displayY);
                    animator.draw(batch);
                } else {
                    // System.out.println("DEBUG: " + keyPrefix + i + " is dead.");
                }
            } else {
                System.out.println("DEBUG: No animator for " + keyPrefix + i);
            }
        }
    }

    /**
     * キャラクターの名前とHP数値を頭上に表示
     */
    private void drawCharacterLabels() {
        font.getData().setScale(1.0f); // 名前は少し小さめに

        drawLabelsForGroup(party, "party_");
        drawLabelsForGroup(enemies, "enemy_");

        font.getData().setScale(1.5f); // 戻す
    }

    private void drawLabelsForGroup(List<ICombatant> group, String keyPrefix) {
        for (int i = 0; i < group.size(); i++) {
            ICombatant member = group.get(i);
            if (!member.isAlive())
                continue;

            CharacterAnimator animator = animatorMap.get(keyPrefix + i);
            if (animator != null) {
                // 名前 Lv (HP/MaxHP) MP: (MP/MaxMP) の形式に変更
                String labelText = member.getName() + " Lv." + member.getLevel() + " HP " + member.getCurrentHP() + "/"
                        + member.getMaxHP() + " MP " + member.getCurrentMP();

                float textX = animator.displayX;
                float textY = animator.displayY + animator.height + HP_BAR_OFFSET + HP_BAR_HEIGHT + 20;
                font.draw(batch, labelText, textX, textY);
            }
        }
    }

    private void drawOverheadHPBars() {
        drawHPBarsForGroup(party, "party_");
        drawHPBarsForGroup(enemies, "enemy_");
    }

    private void drawHPBarsForGroup(List<ICombatant> group, String keyPrefix) {
        for (int i = 0; i < group.size(); i++) {
            ICombatant member = group.get(i);
            if (!member.isAlive())
                continue;

            CharacterAnimator animator = animatorMap.get(keyPrefix + i);
            if (animator != null) {
                float barX = animator.displayX;
                float barY = animator.displayY + animator.height + HP_BAR_OFFSET;
                float barWidth = animator.width;

                drawSingleHPBar(barX, barY, barWidth, HP_BAR_HEIGHT, member);
            }
        }
    }

    private void drawSingleHPBar(float x, float y, float width, float height, ICombatant combatant) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x, y, width, height);

        float hpPercent = (float) combatant.getCurrentHP() / combatant.getMaxHP();
        if (hpPercent > 0.5f)
            shapeRenderer.setColor(Color.GREEN);
        else if (hpPercent > 0.25f)
            shapeRenderer.setColor(Color.YELLOW);
        else
            shapeRenderer.setColor(Color.RED);

        shapeRenderer.rect(x, y, width * hpPercent, height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }

    private void drawBattleResult() {
        font.getData().setScale(3.0f);

        CombatManager.BattleState state = combatManager.getBattleState();
        String result = state.toString();

        if (state == CombatManager.BattleState.DEFEAT) {
            font.setColor(Color.RED);
            result = "GAME OVER";
        } else {
            font.setColor(Color.GOLD);
            result = "VICTORY";
        }

        GlyphLayout layout = new GlyphLayout(font, result);
        font.draw(batch, result, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() / 2);
        font.getData().setScale(1.5f);
    }

    // ========================================================================
    // ユーティリティ
    // ========================================================================

    private TextureRegion[] createPlaceholderTexture() {
        if (!textureMap.containsKey("placeholder")) {
            Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.MAGENTA);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            rawTextures.put("placeholder_raw", texture);
            pixmap.dispose();
            textureMap.put("placeholder", new TextureRegion[] { new TextureRegion(texture) });
        }
        return textureMap.get("placeholder");
    }

    private void addLog(String message) {
        combatLog.add(message);
        if (combatLog.size() > MAX_LOG_LINES) {
            combatLog.remove(0);
        }
    }

    private TextureRegion getAnimatorTexture(String key) {
        CharacterAnimator animator = animatorMap.get(key);
        return (animator != null) ? animator.getIdleTexture() : null;
    }

    private void playAnimation(ICombatant combatant, CharacterAnimator.AnimationState state) {
        String key;
        if (party.contains(combatant)) {
            key = "party_" + party.indexOf(combatant);
        } else {
            key = "enemy_" + enemies.indexOf(combatant);
        }
        CharacterAnimator animator = animatorMap.get(key);
        if (animator != null) {
            if (state == CharacterAnimator.AnimationState.ATTACKING)
                animator.playAttackMotion();
            else if (state == CharacterAnimator.AnimationState.HURT)
                animator.playHurtMotion();
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
        }

        // キャラクター位置の再計算
        for (CharacterAnimator animator : animatorMap.values()) {
            animator.updatePositionOnResize(width, height);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        if (rawTextures != null) {
            for (Texture t : rawTextures.values()) {
                t.dispose();
            }
            rawTextures.clear();
        }
        if (uiStage != null)
            uiStage.dispose();
        if (skin != null)
            skin.dispose();
        if (pachinkoMusic != null)
            pachinkoMusic.dispose();
    }

    // ========================================================================
    // 内部クラス: ダメージポップアップ管理 (FloatingTextManager)
    // ========================================================================

    private class FloatingTextManager {
        private List<FloatingText> texts = new ArrayList<>();

        // 内部クラス: 1つの浮遊テキスト
        private class FloatingText {
            float x, y;
            String text;
            Color color;
            float lifeTime = 1.0f; // 1秒間表示
            float timer = 0;
            float velocityY = 50f; // 上昇速度

            FloatingText(float x, float y, String text, Color color) {
                this.x = x;
                this.y = y;
                this.text = text;
                this.color = color;
            }

            boolean update(float delta) {
                timer += delta;
                y += velocityY * delta; // 上へ移動
                return timer < lifeTime;
            }
        }

        public void add(float x, float y, String text, Color color) {
            texts.add(new FloatingText(x, y, text, color));
        }

        public void update(float delta) {
            Iterator<FloatingText> it = texts.iterator();
            while (it.hasNext()) {
                FloatingText ft = it.next();
                if (!ft.update(delta)) {
                    it.remove();
                }
            }
        }

        public void draw(SpriteBatch batch, BitmapFont font) {
            float originalScale = font.getData().scaleX;
            font.getData().setScale(2.0f); // ダメージは見やすく大きく

            for (FloatingText ft : texts) {
                // フェードアウト効果などを入れたい場合はsetColorを変更する
                font.setColor(ft.color);
                font.draw(batch, ft.text, ft.x, ft.y);
            }

            font.setColor(Color.WHITE); // リセット
            font.getData().setScale(originalScale);
        }
    }

    // ========================================================================
    // 内部クラス: カットイン管理
    // ========================================================================

    private class CutInManager {
        private boolean active = false;
        private float timer = 0;
        private static final float DURATION = 1.5f;
        private ICombatant pendingActor;
        private TextureRegion currentTexture;
        private String currentText;

        public void start(ICombatant actor, TextureRegion texture) {
            this.pendingActor = actor;
            this.currentTexture = texture;
            this.currentText = actor.getName() + "\nATTACK!";
            this.active = true;
            this.timer = 0;
        }

        public void update(float delta) {
            if (!active)
                return;
            timer += delta;
            if (timer >= DURATION) {
                active = false;
            }
        }

        public void draw(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font) {
            if (!active)
                return;

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0, 0, 0, 0.8f);
            shapes.rect(0, Gdx.graphics.getHeight() / 2f - 150, Gdx.graphics.getWidth(), 300);
            shapes.end();

            batch.begin();

            if (currentTexture != null) {
                float scale = 4.0f;
                float w = currentTexture.getRegionWidth() * scale;
                float h = currentTexture.getRegionHeight() * scale;

                boolean isParty = party.contains(pendingActor);
                float startX = isParty ? -200 : Gdx.graphics.getWidth() + 200;
                float endX = isParty ? 100 : Gdx.graphics.getWidth() - w - 100;

                float progress = Math.min(timer * 2.0f, 1.0f);
                float x = Interpolation.pow2Out.apply(startX, endX, progress);
                float y = Gdx.graphics.getHeight() / 2f - h / 2f;

                batch.draw(currentTexture, x, y, w, h);
            }

            font.getData().setScale(3.0f);
            font.setColor(Color.CYAN);

            GlyphLayout layout = new GlyphLayout(font, currentText);
            float textX = Gdx.graphics.getWidth() / 2f - layout.width / 2f;
            float textY = Gdx.graphics.getHeight() / 2f + layout.height / 2f;

            if (timer < 0.2f) {
                font.getData().setScale(5.0f - (timer * 10));
            }
            font.draw(batch, currentText, textX, textY);

            font.getData().setScale(1.5f);
            font.setColor(Color.WHITE);
            batch.end();
        }

        public boolean isActive() {
            return active;
        }

        public ICombatant getPendingActor() {
            return pendingActor;
        }
    }

    // ========================================================================
    // 内部クラス: キャラクターアニメーター
    // ========================================================================

    // ========================================================================
    // 内部クラス: キャラクターアニメーター
    // ========================================================================

    private static class CharacterAnimator {
        List<TextureRegion> idleFrames = new ArrayList<>();
        List<TextureRegion> attackFrames = new ArrayList<>();
        List<TextureRegion> walkFrames = new ArrayList<>();
        List<TextureRegion> hurtFrames = new ArrayList<>();
        List<TextureRegion> jumpFrames = new ArrayList<>();

        TextureRegion currentTexture;
        float baseX, baseY;
        float displayX, displayY;
        float width, height;

        AnimationState animState = AnimationState.IDLE;
        float frameTime = 0;
        int currentFrame = 0;
        static final float FRAME_DURATION = 0.1f;

        boolean isMoving = false;
        float moveTimer, moveDuration;
        float moveStartX, moveStartY, moveTargetX, moveTargetY;

        float rotationAngle, rotationTimer;

        enum AnimationState {
            IDLE, ATTACKING, WALKING, HURT, JUMPING
        }

        float scale = 1.0f;

        // リサイズ用データ
        float ratioX, ratioY;
        int index;

        CharacterAnimator(TextureRegion[] idleFramesArray, float x, float y, float w, float h) {
            if (idleFramesArray != null) {
                for (TextureRegion r : idleFramesArray) {
                    this.idleFrames.add(r);
                }
            }
            this.currentTexture = !idleFrames.isEmpty() ? idleFrames.get(0) : null;
            this.baseX = this.displayX = x;
            this.baseY = this.displayY = y;
            this.width = w;
            this.height = h;
        }

        void setScale(float scale) {
            this.scale = scale;
        }

        void setPositionRatios(float rx, float ry, int idx) {
            this.ratioX = rx;
            this.ratioY = ry;
            this.index = idx;
        }

        void updatePositionOnResize(int width, int height) {
            // 基本位置を再計算
            this.baseX = width * ratioX;
            this.baseY = height * ratioY - index * Y_OFFSET; // Y_OFFSETは固定値のままだが、必要ならここも比率に

            // アニメーションなどで動いていなければdisplayも更新
            if (!isMoving) {
                this.displayX = this.baseX;
                this.displayY = this.baseY;
            }
        }

        void addFrame(String type, TextureRegion[] frames) {
            if (frames == null)
                return;
            switch (type.toLowerCase()) {
                case "idle":
                    for (TextureRegion r : frames)
                        idleFrames.add(r);
                    break;
                case "attack":
                    for (TextureRegion r : frames)
                        attackFrames.add(r);
                    break;
                case "walk":
                    for (TextureRegion r : frames)
                        walkFrames.add(r);
                    break;
                case "hurt":
                    for (TextureRegion r : frames)
                        hurtFrames.add(r);
                    break;
                case "jump":
                    for (TextureRegion r : frames)
                        jumpFrames.add(r);
                    break;
            }
        }

        TextureRegion getIdleTexture() {
            return !idleFrames.isEmpty() ? idleFrames.get(0) : currentTexture;
        }

        void draw(SpriteBatch batch) {
            if (currentTexture == null)
                return;
            // TextureRegionを正しく描画するために width/height を参照しつつ、Regionのサイズも考慮
            batch.draw(currentTexture, displayX, displayY, width / 2, height / 2, width, height, scale, scale,
                    rotationAngle);
        }

        void update(float delta) {
            updateMovement(delta);
            updateRotation(delta);
            updateFrames(delta);
        }

        private void updateMovement(float delta) {
            if (!isMoving)
                return;
            moveTimer += delta;
            float progress = Math.min(moveTimer / moveDuration, 1.0f);
            float ease = Interpolation.sine.apply(progress);

            displayX = moveStartX + (moveTargetX - moveStartX) * ease;
            displayY = moveStartY + (moveTargetY - moveStartY) * ease;

            if (progress >= 1.0f) {
                isMoving = false;
                displayX = baseX;
                displayY = baseY;
                if (animState == AnimationState.ATTACKING || animState == AnimationState.HURT) {
                    setAnimationState(AnimationState.IDLE);
                }
            }
        }

        private void updateRotation(float delta) {
            if (rotationTimer > 0) {
                rotationTimer -= delta;
                rotationAngle = (float) Math.sin(rotationTimer * 20) * 5;
            } else {
                rotationAngle = 0;
            }
        }

        private void updateFrames(float delta) {
            frameTime += delta;
            List<TextureRegion> frames = getFramesForState(animState);
            if (frames.isEmpty())
                return;

            if (frameTime >= FRAME_DURATION) {
                frameTime -= FRAME_DURATION;
                currentFrame++;
                if (currentFrame >= frames.size()) {
                    currentFrame = 0;
                    if (animState == AnimationState.ATTACKING || animState == AnimationState.HURT) {
                        returnToBase();
                    }
                }
            }
            currentTexture = frames.get(currentFrame);
        }

        private List<TextureRegion> getFramesForState(AnimationState state) {
            switch (state) {
                case ATTACKING:
                    return attackFrames.isEmpty() ? idleFrames : attackFrames;
                case WALKING:
                    return walkFrames.isEmpty() ? idleFrames : walkFrames;
                case HURT:
                    return hurtFrames.isEmpty() ? idleFrames : hurtFrames;
                default:
                    return idleFrames;
            }
        }

        void setAnimationState(AnimationState state) {
            if (this.animState != state) {
                this.animState = state;
                this.frameTime = 0;
                this.currentFrame = 0;
            }
        }

        void moveTo(float tx, float ty, float dur) {
            this.moveStartX = displayX;
            this.moveStartY = displayY;
            this.moveTargetX = tx;
            this.moveTargetY = ty;
            this.moveDuration = dur;
            this.moveTimer = 0;
            this.isMoving = true;
        }

        void playAttackMotion() {
            setAnimationState(AnimationState.ATTACKING);
            moveTo(baseX + 60, baseY, 0.3f);
        }

        void playHurtMotion() {
            setAnimationState(AnimationState.HURT);
            moveTo(baseX - 40, baseY, 0.2f);
            rotationTimer = 0.3f;
        }

        void returnToBase() {
            moveTo(baseX, baseY, 0.4f);
        }
    }
}
