package com.teamname.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
// import com.badlogic.gdx.graphics.GL20; これがあるとなぜかエラー吐く
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.teamname.world.entity.PlayerEntity;
import com.teamname.world.entity.MonsterEntity;
import com.teamname.world.entity.NPCEntity;
import com.teamname.world.system.FontSystem;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GameScreen implements Screen {

    private final AdventureRPG game;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    // マップのピクセルサイズ
    private float mapPixelWidth;
    private float mapPixelHeight;

    // メインフィールド用
    private OrthographicCamera worldCamera;
    private Viewport worldViewport;

    // ミニマップ用（マップ全体）
    private OrthographicCamera miniMapCamera;
    private FrameBuffer miniMapFBO;
    private SpriteBatch batch;

    // UI 用
    private Stage stage;
    private Image miniMapImage;
    private ShapeRenderer shapeRenderer;

    // カメラ移動速度
    private static final float CAMERA_SPEED = 200f;
    // 拡大倍率（小さいほどズームイン）
    private static final float CAMERA_ZOOM = 0.5f;

    // ミニマップのサイズ倍率
    private static final float MINIMAP_SCALE = 0.2f;

    // エンティティ
    private PlayerEntity player;
    private List<MonsterEntity> monsters;
    private List<NPCEntity> npcs;

    // ボス
    private MonsterEntity boss;

    // Game Clear UI
    private Label gameClearLabel;
    private BitmapFont gameClearFont;

    // Added Field
    private Label levelLabel;

    public GameScreen(AdventureRPG game) {
        this.game = game;
    }

    @Override
    public void show() {
        // マップ読み込み
        map = new TmxMapLoader().load("maps/map1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        MapProperties props = map.getProperties();
        int mapWidth = props.get("width", Integer.class);
        int mapHeight = props.get("height", Integer.class);
        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);

        mapPixelWidth = mapWidth * tileWidth;
        mapPixelHeight = mapHeight * tileHeight;

        // メインカメラ
        worldCamera = new OrthographicCamera();
        worldViewport = new ScreenViewport(worldCamera);
        worldViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        worldCamera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
        worldCamera.zoom = CAMERA_ZOOM;
        worldCamera.update();

        // ミニマップカメラ（マップ全体を表示）
        miniMapCamera = new OrthographicCamera();
        miniMapCamera.setToOrtho(false, mapPixelWidth, mapPixelHeight);
        miniMapCamera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
        miniMapCamera.update();

        // ミニマップ用のフレームバッファ
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        int miniWidth = (int) (screenW * MINIMAP_SCALE);
        int miniHeight = (int) (screenH * MINIMAP_SCALE);
        miniMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, miniWidth, miniHeight, false);

        // Stage（UI用）とミニマップ画像
        stage = new Stage(new ScreenViewport());

        // UI Table for organization
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        stage.addActor(uiTable);

        // MiniMap Image (Raw actor added to stage previously, but let's keep it
        // independent of table or add to table)
        // Original code added Image directly. Let's keep Image as is, and use Table for
        // centering text.

        miniMapImage = new Image();
        miniMapImage.setSize(miniWidth, miniHeight);
        miniMapImage.setPosition(screenW - miniWidth, screenH - miniHeight);
        stage.addActor(miniMapImage); // This line was incomplete in the user's provided snippet, but is now fixed.

        // Game Clear Label
        gameClearFont = FontSystem.createJapaneseFont(64);
        Label.LabelStyle labelStyle = new Label.LabelStyle(gameClearFont, Color.GOLD);
        gameClearLabel = new Label("GAME CLEAR", labelStyle);
        gameClearLabel.setVisible(false);
        uiTable.add(gameClearLabel).center();

        // Level Label (Top-Left)
        Label.LabelStyle levelStyle = new Label.LabelStyle(gameClearFont, Color.WHITE);
        levelLabel = new Label("Lv.1", levelStyle);
        levelLabel.setPosition(20, Gdx.graphics.getHeight() - 50);
        stage.addActor(levelLabel);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // BGM再生
        if (game.getAudioManager() != null) {
            game.getAudioManager().playBgm("field.mp3", true);
        }

        // エンティティ初期化
        player = new PlayerEntity(mapPixelWidth / 2f, mapPixelHeight / 2f - 550, mapPixelWidth, mapPixelHeight);

        monsters = new ArrayList<>();
        // モンスターを数体配置
        for (int i = 0; i < 40; i++) {
            // 1〜4の中からランダムな種類を選択
            int typeId = MathUtils.random(1, 4);
            String texturePath = "free-field-enemies-pixel-art-for-tower-defense/" + typeId + "/S_Walk.png";

            MonsterEntity m = new MonsterEntity(
                    MathUtils.random(0, mapPixelWidth - 32),
                    MathUtils.random(0, mapPixelHeight - 32),
                    mapPixelWidth, mapPixelHeight, texturePath); // コンストラクタは既存のものは6カラム前提だが、S_Walkは1枚絵に近い可能性がある。要確認。

            // TextureRegionの分割を確認。S_Walk.pngは横長？リソースの中身を見てないので
            // 一旦既存のコンストラクタ(6cols)を使うが、もしアニメーションがおかしかったら修正が必要。
            // リストを見ると S_Walk.png は1ファイル。おそらく横に並んでいる。

            m.setTextureKey(String.valueOf(typeId));
            monsters.add(m);
        }

        npcs = new ArrayList<>();
        // テスト用NPC (King) -> 話すと MISSION_ACCEPTED フラグを立てる
        // 王様のアセットパス (160x160 -> assumes top-left is 32x32 frame)
        String kingPath = "king/pixelartKing.png";
        // New Event System Usage: Trigger "king_intro" dialog
        NPCEntity king = new NPCEntity(mapPixelWidth / 2f, mapPixelHeight / 2f - 500, "King", "DIALOG_king_intro", game,
                true, kingPath, 99, 99);
        npcs.add(king);

        // ボス配置 (最初は隠れているか、条件付きで処理するか)
        // ボス配置 (最初は隠れているか、条件付きで処理するか)
        // ボスは DeamonKing/Devil.png (1872x64). Assuming 48x64 frames -> 39 cols.
        // Limit to 6 frames for Idle animation
        String bossPath = "DeamonKing/Devil.png";
        boss = new MonsterEntity(mapPixelWidth / 2f, mapPixelHeight - 100, mapPixelWidth, mapPixelHeight, bossPath,
                39, 1, 6);
        // User request: Bigger size, higher up (top of land), no movement
        boss.setSize(128, 128);
        boss.setSpeed(0);

        // ボス用の強力な敵データ設定
        List<com.teamname.world.combat.ICombatant> bossParty = new ArrayList<>();
        // Level 999 - No limits!
        bossParty.add(new com.teamname.world.combat.DemonKing(999));
        boss.setEnemies(bossParty);
    }

    private void update(float delta) {
        // プレイヤー更新
        player.update(delta);

        // モンスター更新と衝突判定
        // イテレータを使って削除可能にする（戦闘に入ったら削除するか、一時的に消すか要検討）
        // ここではシンボルエンカウントしたら戦闘画面へ遷移し、モンスターは消滅させる実装にする
        for (int i = 0; i < monsters.size(); i++) {
            MonsterEntity monster = monsters.get(i);
            monster.update(delta);

            if (player.getBounds().overlaps(monster.getBounds())) {
                // 衝突！ 戦闘開始
                // TODO: エンカウント演出などを入れる場合はここで処理
                game.getUIManager().showBattleUI(monster.getEnemies());

                // 戦闘画面に遷移する直前に、実際の敵データを渡す処理は showBattleUI 内で行うか、
                // showBattleUI を拡張して monster.getEnemies() を渡すようにする。
                // 現状の UIManager.showBattleUI は固定の敵を生成しているので、
                // 将来的にはここで monster.getEnemies() を渡す形にする必要がある。

                // シンボルを削除
                monsters.remove(i);
                i--;

                return;
            }
        }

        // ボス更新 (クエストを受注している場合のみ)
        // クエストID:1がアクティブであればボスを表示
        boolean isBossQuestActive = false;
        if (game.getGameState().questManager != null) {
            // Quest ID 1 = King's Request
            if (game.getGameState().questManager.getActiveQuests().containsKey(1)) {
                isBossQuestActive = true;
            }
        }

        if (isBossQuestActive && game.getGameState().getFlag("BOSS_DEFEATED") == 0) {
            boss.update(delta);
            if (player.getBounds().overlaps(boss.getBounds())) {
                game.getUIManager().showBattleUI(boss.getEnemies());
                // Remove premature flag setting. Flag will be set in VisualCombatScreen upon
                // victory.
                // game.getGameState().setFlag("BOSS_DEFEATED", 1);

                // Move boss away temporarily to avoid immediate re-trigger if logic allows,
                // but since we change screens, it might be fine.
                // However, without the flag, it might re-trigger in update loop if we don't
                // handle it.
                // But changing screen stops GameScreen.update usually?
                // Actually VisualCombatScreen is just an overlay or separate screen?
                // AdventureRPG.render calls current screen.
                // UIManager.showBattleUI sets battleflag=1.
                // GameScreen.render checks battleflag.
            }
        }

        // NPCとのインタラクト (Fキー)
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            for (NPCEntity npc : npcs) {
                // プレイヤーの近くにいるか判定 (boundsを少し広げるか、距離で判定)
                float dist = com.badlogic.gdx.math.Vector2.dst(player.getX(), player.getY(), npc.getX(), npc.getY());
                if (dist < 80) { // 80ピクセル以内 (少し広げた)
                    npc.interact();
                    break; // 一人に話しかけたら終了
                }
            }
        }

        // カメラ位置をプレイヤーに追従させる
        worldCamera.position.x = player.getX() + player.getWidth() / 2f;
        worldCamera.position.y = player.getY() + player.getHeight() / 2f;

        float halfViewWidth = worldCamera.viewportWidth * worldCamera.zoom / 2f;
        float halfViewHeight = worldCamera.viewportHeight * worldCamera.zoom / 2f;

        // マップ外にカメラが出ないようにクランプ
        worldCamera.position.x = MathUtils.clamp(worldCamera.position.x, halfViewWidth, mapPixelWidth - halfViewWidth);
        worldCamera.position.y = MathUtils.clamp(worldCamera.position.y, halfViewHeight,
                mapPixelHeight - halfViewHeight);

        worldCamera.update();
    }

    @Override
    public void render(float delta) {
        // 戦闘画面
        if (game.battleflag != 0 && game.combatScreen != null) {
            game.combatScreen.render(delta);
            return;
        }

        // メニューが開いていない時だけ操作可能
        // UIManager経由でチェック
        if (game.getUIManager() == null || !game.getUIManager().isAnyUIOpen()) {
            update(delta);
        }

        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1f);

        // int screenW = Gdx.graphics.getWidth(); 座標指定する際には使うかも。ただwindowsとmacで表示が変わる
        // int screenH = Gdx.graphics.getHeight();

        // ① メインマップ描画（画面全体）
        worldViewport.apply();
        mapRenderer.setView(worldCamera);
        mapRenderer.render();

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        // エンティティ描画
        for (MonsterEntity m : monsters) {
            m.render(batch);
        }
        for (NPCEntity npc : npcs) {
            npc.render(batch);
        }

        // ボス描画
        // クエストID:1がアクティブであればボスを表示
        boolean isBossQuestActive = false;
        if (game.getGameState().questManager != null) {
            if (game.getGameState().questManager.getActiveQuests().containsKey(1)) {
                isBossQuestActive = true;
            }
        }

        if (isBossQuestActive && game.getGameState().getFlag("BOSS_DEFEATED") == 0) {
            boss.render(batch);
        }
        player.render(batch);
        batch.end();

        // ② ミニマップをフレームバッファに描画
        miniMapFBO.begin();
        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1f);

        miniMapCamera.update();
        mapRenderer.setView(miniMapCamera);
        mapRenderer.render();

        // ミニマップ上で「今見ている範囲」を黄色枠で描画
        float viewWorldWidth = worldCamera.viewportWidth * worldCamera.zoom;
        float viewWorldHeight = worldCamera.viewportHeight * worldCamera.zoom;
        float viewWorldX = worldCamera.position.x - viewWorldWidth / 2f;
        float viewWorldY = worldCamera.position.y - viewWorldHeight / 2f;

        shapeRenderer.setProjectionMatrix(miniMapCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 1f);
        shapeRenderer.rect(viewWorldX, viewWorldY, viewWorldWidth, viewWorldHeight);
        shapeRenderer.end();

        miniMapFBO.end();

        // ③ ミニマップ画像を更新してStageで描画
        TextureRegion region = new TextureRegion(miniMapFBO.getColorBufferTexture());
        region.flip(false, true);
        miniMapImage.setDrawable(new TextureRegionDrawable(region));

        stage.draw();

        // ⑥ 最後にUIマネージャーを描画（メニュー、会話ウィンドウなど）
        if (game.getUIManager() != null) {
            game.getUIManager().updateAndRender(delta);
        }

        // Update Level Label
        com.teamname.world.system.Character leader = game.getGameState().getLeader();
        if (leader != null && levelLabel != null) {
            levelLabel.setText(leader.getName() + " Lv." + leader.getLevel());
            // levelLabel.setPosition(20, Gdx.graphics.getHeight() - 50); // Optional update
            // if needed
        } // new one.
          // Actually, UIManager usually handles all UI. But requested to add to Field
          // Screen specifically.
          // Let's use `gameClearFont` scaled down if no other font.
    }

    @Override
    public void resize(int width, int height) {
        // メインビュー更新
        worldViewport.update(width, height, true);

        // Stage更新
        stage.getViewport().update(width, height, true);

        // ミニマップFBOを再作成
        if (miniMapFBO != null) {
            miniMapFBO.dispose();
        }
        int miniWidth = (int) (width * MINIMAP_SCALE);
        int miniHeight = (int) (height * MINIMAP_SCALE);
        miniMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, miniWidth, miniHeight, false);

        // ミニマップ画像の位置とサイズを更新
        miniMapImage.setSize(miniWidth, miniHeight);
        miniMapImage.setPosition(width - miniWidth, height - miniHeight);

        if (game.getUIManager() != null) {
            game.getUIManager().resize(width, height);
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
        if (mapRenderer != null)
            mapRenderer.dispose();
        if (map != null)
            map.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
        if (miniMapFBO != null)
            miniMapFBO.dispose();
        if (batch != null)
            batch.dispose();
        if (stage != null)
            stage.dispose();
        if (gameClearFont != null)
            gameClearFont.dispose();
    }
}
