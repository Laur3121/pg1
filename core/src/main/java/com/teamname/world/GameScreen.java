package com.teamname.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

    // UI 用（ミニマップ外枠など）
    private OrthographicCamera uiCamera;
    private ShapeRenderer shapeRenderer;

    // カメラ移動速度
    private static final float CAMERA_SPEED = 200f;
    // 拡大倍率（小さいほどズームイン）
    private static final float CAMERA_ZOOM = 0.5f;   // 0.5f で 2倍拡大くらい

    public GameScreen(AdventureRPG game) {
        this.game = game;
    }

    @Override
    public void show() {
        // マップ読み込み
        map = new TmxMapLoader().load("maps/map1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        // マップサイズ取得
        MapProperties props = map.getProperties();
        int mapWidth = props.get("width", Integer.class);
        int mapHeight = props.get("height", Integer.class);
        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);

        mapPixelWidth = mapWidth * tileWidth;
        mapPixelHeight = mapHeight * tileHeight;

        // ==== メイン用カメラ ====
        worldCamera = new OrthographicCamera();
        worldViewport = new ScreenViewport(worldCamera); // ウィンドウ全部を使う
        worldViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        worldCamera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
        worldCamera.zoom = CAMERA_ZOOM;  // 拡大
        worldCamera.update();

        // ==== ミニマップ用カメラ（マップ全体） ====
        miniMapCamera = new OrthographicCamera();
        miniMapCamera.setToOrtho(false, mapPixelWidth, mapPixelHeight);
        miniMapCamera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
        miniMapCamera.update();

        // ==== UI 用カメラ（画面座標描画用） ====
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();

        shapeRenderer = new ShapeRenderer();
    }

    private void update(float delta) {
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  dx -= CAMERA_SPEED * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += CAMERA_SPEED * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  dy -= CAMERA_SPEED * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    dy += CAMERA_SPEED * delta;

        worldCamera.position.x += dx;
        worldCamera.position.y += dy;

        // マップ外を見ないように clamp
        float halfViewWidth  = worldCamera.viewportWidth  * worldCamera.zoom / 2f;
        float halfViewHeight = worldCamera.viewportHeight * worldCamera.zoom / 2f;

        worldCamera.position.x = MathUtils.clamp(
            worldCamera.position.x,
            halfViewWidth,
            mapPixelWidth - halfViewWidth
        );
        worldCamera.position.y = MathUtils.clamp(
            worldCamera.position.y,
            halfViewHeight,
            mapPixelHeight - halfViewHeight
        );

        worldCamera.update();
    }

    @Override
    public void render(float delta) {
        if (game.battleflag != 0 && game.combatScreen != null) {
            game.combatScreen.render(delta);
            return;
        }

        update(delta);

        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1f);

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();

        // ===== 1. メインフィールド（ウィンドウ全体） =====
        worldViewport.update(screenW, screenH, true);
        worldViewport.apply(); // glViewport(0,0,screenW,screenH)
        mapRenderer.setView(worldCamera);
        mapRenderer.render();

        // ===== 2. ミニマップ（画面右上） =====
        int miniWidth  = screenW / 5;
        int miniHeight = screenH / 5;
        int miniX = screenW - miniWidth - 16;   // 右端から 16px 内側
        int miniY = screenH - miniHeight - 16;  // 上端から 16px 内側

        // (a) ミニマップ用ビューポートに切り替え
        Gdx.gl.glViewport(miniX, miniY, miniWidth, miniHeight);

        // (b) マップ全体をミニマップカメラで描画
        miniMapCamera.update();
        mapRenderer.setView(miniMapCamera);
        mapRenderer.render();

        // (c) 黄色の「現在の表示範囲」をミニマップ上に描画
        float viewWorldWidth  = worldCamera.viewportWidth  * worldCamera.zoom;
        float viewWorldHeight = worldCamera.viewportHeight * worldCamera.zoom;
        float viewWorldX = worldCamera.position.x - viewWorldWidth  / 2f;
        float viewWorldY = worldCamera.position.y - viewWorldHeight / 2f;

        shapeRenderer.setProjectionMatrix(miniMapCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 1f); // 黄色
        shapeRenderer.rect(viewWorldX, viewWorldY, viewWorldWidth, viewWorldHeight);
        shapeRenderer.end();

        // ===== 3. ビューポートを全画面に戻し、白い外枠を描く（画面座標） =====
        Gdx.gl.glViewport(0, 0, screenW, screenH);

        uiCamera.setToOrtho(false, screenW, screenH);
        uiCamera.update();
        shapeRenderer.setProjectionMatrix(uiCamera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(miniX - 2, miniY - 2,
            miniWidth + 4, miniHeight + 4);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        uiCamera.setToOrtho(false, width, height);
        uiCamera.update();
        // ミニマップは毎フレーム glViewport で位置を決めているのでここはこれでOK
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
