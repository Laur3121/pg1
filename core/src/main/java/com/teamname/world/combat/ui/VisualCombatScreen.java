package com.teamname.world.combat.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;

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
    private static final float PARTY_START_X = 100f;
    private static final float ENEMY_START_X_OFFSET = 250f;
    private static final float Y_START = 300f;
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
    private final Map<String, Texture> textureMap;
    private final Map<String, CharacterAnimator> animatorMap;
    private final CutInManager cutInManager;
    private final FloatingTextManager floatingTextManager; // ダメージポップアップ管理

    // --- 状態変数 ---
    private float stateTime;
    private float turnDelay;

    public VisualCombatScreen() {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.5f);
        this.shapeRenderer = new ShapeRenderer();

        this.textureMap = new HashMap<>();
        this.animatorMap = new HashMap<>();
        this.combatLog = new ArrayList<>();

        this.cutInManager = new CutInManager();
        this.floatingTextManager = new FloatingTextManager(); // 初期化

        this.combatManager = new CombatManager();
        this.stateTime = 0;
        this.turnDelay = 0;

        loadAssets();
        initializeBattle();
    }

    // ========================================================================
    //  初期化・ロード処理
    // ========================================================================

    private void loadAssets() {
        // 味方 (Warrior)
        loadTextureSafe("warrior_idle", "face/fuji.png");
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
        loadTextureSafe("archer_walk2", "evil Mage Pack/archer/tile034.png");
    }

    private void loadTextureSafe(String key, String path) {
        try {
            Texture texture = new Texture(path);
            textureMap.put(key, texture);
        } catch (Exception e) {
            System.err.println("Warning: Failed to load " + path);
        }
    }

    private void initializeBattle() {
        party = new ArrayList<>();
        party.add(new TestCharacter("Hero", 100, 20, 10, 15));
        party.add(new TestCharacter("Warrior", 120, 25, 15, 10));

        enemies = new ArrayList<>();
        enemies.add(new TestCharacter("Evil Mage", 80, 18, 8, 14));
        enemies.add(new TestCharacter("Archer", 60, 16, 5, 16));

        setupAnimators(party, PARTY_START_X, "party_", "warrior");
        setupAnimators(enemies, Gdx.graphics.getWidth() - ENEMY_START_X_OFFSET, "enemy_", null);

        addLog("=== Battle Start! ===");
        combatManager.startBattle(party, enemies);
    }

    private void setupAnimators(List<ICombatant> combatants, float startX, String keyPrefix, String forceType) {
        for (int i = 0; i < combatants.size(); i++) {
            String key = keyPrefix + i;
            String type = forceType;
            if (type == null) {
                type = (i == 0) ? "evilmage" : "archer";
            }

            Texture idleTexture = textureMap.getOrDefault(type + "_idle", createPlaceholderTexture());
            CharacterAnimator animator = new CharacterAnimator(
                idleTexture, startX, Y_START - i * Y_OFFSET, CHAR_SIZE, CHAR_SIZE
            );

            addFramesToAnimator(animator, type, "attack", 3);
            addFramesToAnimator(animator, type, "walk", 2);

            animatorMap.put(key, animator);
        }
    }

    private void addFramesToAnimator(CharacterAnimator animator, String charPrefix, String actionType, int count) {
        for (int j = 1; j <= count; j++) {
            String texKey = charPrefix + "_" + actionType + j;
            if (textureMap.containsKey(texKey)) {
                animator.addFrame(actionType, textureMap.get(texKey));
            }
        }
    }

    // ========================================================================
    //  メインループ (Update & Render)
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

        drawBattleScene();

        if (cutInManager.isActive()) {
            cutInManager.update(delta);
            cutInManager.draw(batch, shapeRenderer, font);
            if (!cutInManager.isActive()) {
                executeTurnAction(cutInManager.getPendingActor());
            }
            return;
        }

        if (combatManager.isBattleActive()) {
            turnDelay += delta;
            if (turnDelay >= TURN_DURATION) {
                turnDelay = 0;
                prepareTurn();
            }
        }
    }

    private void prepareTurn() {
        ICombatant actor = combatManager.getCurrentActor();
        if (actor == null) return;
        Texture actorTexture = getCombatantTexture(actor);
        cutInManager.start(actor, actorTexture);
    }

    private void executeTurnAction(ICombatant actor) {
        if (actor == null || !actor.isAlive()) return;

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
        }
    }

    // ========================================================================
    //  描画ヘルパー
    // ========================================================================

    private void drawBattleScene() {
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
        if (!combatManager.isBattleActive()) {
            drawBattleResult();
        }

        batch.end();

        // HPバーの描画
        drawOverheadHPBars();
    }

    private void drawGroup(List<ICombatant> group, String keyPrefix) {
        for (int i = 0; i < group.size(); i++) {
            ICombatant member = group.get(i);
            CharacterAnimator animator = animatorMap.get(keyPrefix + i);
            if (animator != null && member.isAlive()) {
                animator.draw(batch);
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
            if (!member.isAlive()) continue;

            CharacterAnimator animator = animatorMap.get(keyPrefix + i);
            if (animator != null) {
                // 名前 (HP/MaxHP) の形式に変更
                String labelText = member.getName() + " (" + member.getCurrentHP() + "/" + member.getMaxHP() + ")";

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
            if (!member.isAlive()) continue;

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
        if (hpPercent > 0.5f) shapeRenderer.setColor(Color.GREEN);
        else if (hpPercent > 0.25f) shapeRenderer.setColor(Color.YELLOW);
        else shapeRenderer.setColor(Color.RED);

        shapeRenderer.rect(x, y, width * hpPercent, height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }

    private void drawBattleResult() {
        font.getData().setScale(3.0f);
        font.setColor(Color.GOLD);
        String result = combatManager.getBattleState().toString();
        GlyphLayout layout = new GlyphLayout(font, result);
        font.draw(batch, result, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() / 2);
        font.getData().setScale(1.5f);
    }

    // ========================================================================
    //  ユーティリティ
    // ========================================================================

    private Texture createPlaceholderTexture() {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fillRectangle(0, 0, 64, 64);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void addLog(String message) {
        combatLog.add(message);
        if (combatLog.size() > MAX_LOG_LINES) {
            combatLog.remove(0);
        }
    }

    private Texture getCombatantTexture(ICombatant combatant) {
        String key;
        if (party.contains(combatant)) {
            key = "party_" + party.indexOf(combatant);
        } else {
            key = "enemy_" + enemies.indexOf(combatant);
        }
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
            if (state == CharacterAnimator.AnimationState.ATTACKING) animator.playAttackMotion();
            else if (state == CharacterAnimator.AnimationState.HURT) animator.playHurtMotion();
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        for (Texture t : textureMap.values()) t.dispose();
    }

    // ========================================================================
    //  内部クラス: ダメージポップアップ管理 (FloatingTextManager)
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
    //  内部クラス: カットイン管理
    // ========================================================================

    private class CutInManager {
        private boolean active = false;
        private float timer = 0;
        private static final float DURATION = 1.5f;
        private ICombatant pendingActor;
        private Texture currentTexture;
        private String currentText;

        public void start(ICombatant actor, Texture texture) {
            this.pendingActor = actor;
            this.currentTexture = texture;
            this.currentText = actor.getName() + "\nATTACK!";
            this.active = true;
            this.timer = 0;
        }

        public void update(float delta) {
            if (!active) return;
            timer += delta;
            if (timer >= DURATION) {
                active = false;
            }
        }

        public void draw(SpriteBatch batch, ShapeRenderer shapes, BitmapFont font) {
            if (!active) return;

            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0, 0, 0, 0.8f);
            shapes.rect(0, Gdx.graphics.getHeight() / 2f - 150, Gdx.graphics.getWidth(), 300);
            shapes.end();

            batch.begin();

            if (currentTexture != null) {
                float scale = 4.0f;
                float w = currentTexture.getWidth() * scale;
                float h = currentTexture.getHeight() * scale;

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

        public boolean isActive() { return active; }
        public ICombatant getPendingActor() { return pendingActor; }
    }

    // ========================================================================
    //  内部クラス: キャラクターアニメーター
    // ========================================================================

    private static class CharacterAnimator {
        List<Texture> idleFrames = new ArrayList<>();
        List<Texture> attackFrames = new ArrayList<>();
        List<Texture> walkFrames = new ArrayList<>();
        List<Texture> hurtFrames = new ArrayList<>();
        List<Texture> jumpFrames = new ArrayList<>();

        Texture currentTexture;
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

        enum AnimationState { IDLE, ATTACKING, WALKING, HURT, JUMPING }

        CharacterAnimator(Texture idleTexture, float x, float y, float w, float h) {
            this.idleFrames.add(idleTexture);
            this.currentTexture = idleTexture;
            this.baseX = this.displayX = x;
            this.baseY = this.displayY = y;
            this.width = w;
            this.height = h;
        }

        void addFrame(String type, Texture texture) {
            switch (type.toLowerCase()) {
                case "idle": idleFrames.add(texture); break;
                case "attack": attackFrames.add(texture); break;
                case "walk": walkFrames.add(texture); break;
                case "hurt": hurtFrames.add(texture); break;
                case "jump": jumpFrames.add(texture); break;
            }
        }

        Texture getIdleTexture() {
            return !idleFrames.isEmpty() ? idleFrames.get(0) : currentTexture;
        }

        void draw(SpriteBatch batch) {
            batch.draw(currentTexture, displayX, displayY, width / 2, height / 2, width, height, 1, 1, rotationAngle, 0, 0, currentTexture.getWidth(), currentTexture.getHeight(), false, false);
        }

        void update(float delta) {
            updateMovement(delta);
            updateRotation(delta);
            updateFrames(delta);
        }

        private void updateMovement(float delta) {
            if (!isMoving) return;
            moveTimer += delta;
            float progress = Math.min(moveTimer / moveDuration, 1.0f);
            float ease = Interpolation.sine.apply(progress);

            displayX = moveStartX + (moveTargetX - moveStartX) * ease;
            displayY = moveStartY + (moveTargetY - moveStartY) * ease;

            if (progress >= 1.0f) {
                isMoving = false;
                displayX = baseX; displayY = baseY;
                if (animState == AnimationState.ATTACKING || animState == AnimationState.HURT) {
                    setAnimationState(AnimationState.IDLE);
                }
            }
        }

        private void updateRotation(float delta) {
            if (rotationTimer > 0) {
                rotationTimer -= delta;
                rotationAngle = (float)Math.sin(rotationTimer * 20) * 5;
            } else {
                rotationAngle = 0;
            }
        }

        private void updateFrames(float delta) {
            frameTime += delta;
            List<Texture> frames = getFramesForState(animState);
            if (frames.isEmpty()) return;

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

        private List<Texture> getFramesForState(AnimationState state) {
            switch (state) {
                case ATTACKING: return attackFrames.isEmpty() ? idleFrames : attackFrames;
                case WALKING: return walkFrames.isEmpty() ? idleFrames : walkFrames;
                case HURT: return hurtFrames.isEmpty() ? idleFrames : hurtFrames;
                default: return idleFrames;
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
            this.moveStartX = displayX; this.moveStartY = displayY;
            this.moveTargetX = tx; this.moveTargetY = ty;
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
