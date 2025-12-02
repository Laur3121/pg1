package com.teamname.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

//フォント関連
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * メニュー画面を表示するクラス
 */
public class MenuScreen implements Screen {

    private AdventureRPG game; // メインゲームクラスへの参照（画面切り替え用）
    private Stage stage;       // UIパーツ（ボタンや文字）を置く「舞台」
    private Skin skin;         // UIの見た目（フォントや枠のデザイン）

    public MenuScreen(AdventureRPG game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());

        // --- ★ここから変更：日本語フォント生成 ---

        // 1. 空のスキンを作る
        skin = new Skin();

        // uiskin.json を読み込む前に、画像データ(atlas)をスキンに登録する必要があります。
        try {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
            skin.addRegions(atlas); // Atlasの中身をすべてスキンに登録
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Atlasファイルの読み込みに失敗しました: skin/uiskin.atlas");
        }

        // 2. TTFファイルからビットマップフォントを生成する
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/font.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 24; // 文字の大きさ
        // 日本語の文字をすべて含める（これがないと日本語が出ない）
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん"
            + "がぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽ"
            + "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン"
            + "ガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ"
            + "ッャュョ" // 小さい文字
            + "一二三四五六七八九十百千万" // よく使う漢字
            + "薬草毒消し檜の棒" // ★今回使う漢字も忘れずに！
            + "、。！？";

        // ※本来はもっとたくさんの漢字を含めますが、まずはテスト用に必要な文字だけ入れています。

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose(); // ジェネレータはもう要らないので破棄

        // 3. 生成したフォントを "default-font" という名前でスキンに登録
        skin.add("default-font", font);

        // 4. 最後に uiskin.json を読み込む（これでjson内の default-font が今のフォントと紐付く）
        skin.load(Gdx.files.internal("skin/uiskin.json"));
    }
    /**
     * この画面が表示されるたびに呼ばれるメソッド
     * ここで「最新のインベントリ情報」を画面に並べます。
     */
    @Override
    public void show() {
        // 1. 入力をこのステージで受け取るように設定
        Gdx.input.setInputProcessor(stage);

        // 2. 前回の表示が残らないようにステージをクリア
        stage.clear();

        // 3. ウィンドウを作成
        Window window = new Window("Menu", skin);
        window.setSize(400, 300); // サイズ設定
        window.setPosition(Gdx.graphics.getWidth() / 2f - 200, Gdx.graphics.getHeight() / 2f - 150); // 画面中央へ

        // 4. インベントリの中身を表にする
        Table contentTable = new Table();
        contentTable.top(); // 上詰めにする

        // インベントリからアイテムを取り出してリストに追加
        if (game.getInventory() != null) {
            for (Item item : game.getInventory().getItems()) {
                // アイテム名ラベル
                Label nameLabel = new Label(item.data.name, skin);
                // 個数ラベル
                Label quantityLabel = new Label("x " + item.quantity, skin);

                // 表に追加 (addメソッドでセルを追加、rowで改行)
                contentTable.add(nameLabel).left().pad(5);
                contentTable.add(quantityLabel).right().pad(5);
                contentTable.row(); // 次の行へ
            }
        } else {
            contentTable.add(new Label("No Inventory Data", skin));
        }

        // ウィンドウに表を追加
        window.add(contentTable).expand().top();

        // ステージにウィンドウを配置
        stage.addActor(window);
    }

    @Override
    public void render(float delta) {
        // 背景を少し暗く塗りつぶす
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ステージの更新と描画
        stage.act(delta);
        stage.draw();


        // 「M」キーか「ESC」キーで元の画面（マップ）に戻る
        if (Gdx.input.isKeyJustPressed(Input.Keys.M) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // フラグを0（通常画面）に戻す
            game.battleflag = 0;
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        // 画面が隠れるとき、入力の受付を解除する
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
