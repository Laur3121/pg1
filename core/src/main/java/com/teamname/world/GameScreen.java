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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
        int miniWidth = (int)(screenW * MINIMAP_SCALE);
        int miniHeight = (int)(screenH * MINIMAP_SCALE);
        miniMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, miniWidth, miniHeight, false);

        // Stage（UI用）とミニマップ画像
        stage = new Stage(new ScreenViewport());
        miniMapImage = new Image();
        miniMapImage.setSize(miniWidth, miniHeight);
        miniMapImage.setPosition(screenW - miniWidth, screenH - miniHeight);
        stage.addActor(miniMapImage);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    private void update(float delta) {
        float dx = 0;
        float dy = 0;

        // 十字キー（←→↑↓）で移動
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  dx -= CAMERA_SPEED * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += CAMERA_SPEED * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  dy -= CAMERA_SPEED * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    dy += CAMERA_SPEED * delta;

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) { // Bキーで戦闘へ
            game.setScreen(game.combatScreen);
            game.battleflag = 1;
        }   // いずれ絶対に消す！！！！！！！！！！！！！１デバッグ用戦闘システム

        worldCamera.position.x += dx;
        worldCamera.position.y += dy;

        float halfViewWidth  = worldCamera.viewportWidth  * worldCamera.zoom / 2f;
        float halfViewHeight = worldCamera.viewportHeight * worldCamera.zoom / 2f;

        // マップ外にカメラが出ないようにクランプ
        worldCamera.position.x = MathUtils.clamp(worldCamera.position.x, halfViewWidth, mapPixelWidth - halfViewWidth);
        worldCamera.position.y = MathUtils.clamp(worldCamera.position.y, halfViewHeight, mapPixelHeight - halfViewHeight);

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
        if (game.getMenuTab() == null || !game.getMenuTab().isVisible()) {
            update(delta);
        }

        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1f);

        // int screenW = Gdx.graphics.getWidth();  座標指定する際には使うかも。ただwindowsとmacで表示が変わる
        // int screenH = Gdx.graphics.getHeight();

        // ① メインマップ描画（画面全体）
        worldViewport.apply();
        mapRenderer.setView(worldCamera);
        mapRenderer.render();

        // ② ミニマップをフレームバッファに描画
        miniMapFBO.begin();
        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1f);

        miniMapCamera.update();
        mapRenderer.setView(miniMapCamera);
        mapRenderer.render();

        // ミニマップ上で「今見ている範囲」を黄色枠で描画
        float viewWorldWidth  = worldCamera.viewportWidth  * worldCamera.zoom;
        float viewWorldHeight = worldCamera.viewportHeight * worldCamera.zoom;
        float viewWorldX = worldCamera.position.x - viewWorldWidth  / 2f;
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

        // ⑥ 最後にメニューを重ねて描画
        if (game.getMenuTab() != null) {
            game.getMenuTab().updateAndRender(delta);
        }
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
        int miniWidth = (int)(width * MINIMAP_SCALE);
        int miniHeight = (int)(height * MINIMAP_SCALE);
        miniMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, miniWidth, miniHeight, false);

        // ミニマップ画像の位置とサイズを更新
        miniMapImage.setSize(miniWidth, miniHeight);
        miniMapImage.setPosition(width - miniWidth, height - miniHeight);

        if (game.getMenuTab() != null) {
            game.getMenuTab().resize(width, height);
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (miniMapFBO != null) miniMapFBO.dispose();
        if (batch != null) batch.dispose();
        if (stage != null) stage.dispose();
    }
}
