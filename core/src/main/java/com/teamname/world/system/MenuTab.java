package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color; // 追加
import com.badlogic.gdx.graphics.Pixmap; // 追加
import com.badlogic.gdx.graphics.Texture; // 追加
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teamname.world.AdventureRPG;

//use item
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class MenuTab {
    private AdventureRPG game;
    private Stage stage;
    private Skin skin;
    private boolean isVisible = false;

    public MenuTab(AdventureRPG game) {
        this.game = game;
        createUI();
    }

    private void createUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin();

        // ▼▼▼ 追加：ここで最低限のデザイン（スタイル）を作って登録します ▼▼▼
        createBasicSkin();
    }

    // ▼▼▼ 新規追加メソッド：プログラムで簡易デザインを作成 ▼▼▼
    private void createBasicSkin() {
        // 1. 1x1ピクセルの白い画像を作る（これを引き伸ばしてボタンや背景にする）
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose(); // メモリ解放

        // Skinに "white" という名前でこの画像を登録
        skin.add("white", texture);

        // 2. デフォルトのフォントを登録
        // BitmapFont font = new BitmapFont(); // これだと日本語が出ない
        BitmapFont font = FontSystem.createJapaneseFont(24);
        skin.add("default", font);

        // 3. ラベルのスタイル設定
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // 4. テキストボタンのスタイル設定
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY); // 通常時: 暗いグレー
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY); // 押下時
        textButtonStyle.over = skin.newDrawable("white", Color.GRAY); // ホバー時: 少し明るいグレー
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        // 5. ウィンドウのスタイル設定（これが無いとエラーになります）
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.WHITE;
        // ウィンドウ背景：少し透明な黒
        windowStyle.background = skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.9f);
        skin.add("default", windowStyle);
    }
    // ▲▲▲ 追加ここまで ▲▲▲

    public void toggle() {
        isVisible = !isVisible;
        if (isVisible) {
            rebuildMenu();
            // 入力を受け付けるプロセッサを切り替える（多重登録に注意）
            // ※本格的な実装では InputMultiplexer を使いますが、まずはシンプルに切り替えます
            Gdx.input.setInputProcessor(stage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    // ... (以下の rebuildMenu などは変更なしでOK) ...
    private void rebuildMenu() {
        stage.clear();
        Window window = new Window("Menu", skin);
        window.setSize(500, 400); // 少し大きくしました
        window.setPosition((Gdx.graphics.getWidth() - 500) / 2f, (Gdx.graphics.getHeight() - 400) / 2f);

        // 閉じるボタン
        TextButton closeBtn = new TextButton("X", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle();
            }
        });
        window.getTitleTable().add(closeBtn).size(30, 30).padRight(10);
        ;

        // セーブボタン
        TextButton saveBtn = new TextButton("SAVE", skin);
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // SaveManagerを呼ぶ
                com.teamname.world.system.SaveManager.saveGame(game);
                System.out.println("ゲームをセーブしました！");
            }
        });
        // タイトルバーの右側に追加する例
        window.getTitleTable().add(saveBtn).height(20).padLeft(10);

        // ▼ 追加: ステータス表示エリア
        Table statusTable = new Table();
        // GameStateから最新の情報を取得
        com.teamname.world.system.GameState state = game.getGameState();

        Label hpLabel = new Label("HP: " + state.currentHp + " / " + state.maxHp, skin);
        Label goldLabel = new Label("Gold: " + state.gold + " G", skin);

        statusTable.add(hpLabel).pad(10);
        statusTable.add(goldLabel).pad(10);

        // ウィンドウの上部に追加
        window.add(statusTable).center().row();
        window.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(skin.newDrawable("white", 0.5f, 0.5f, 0.5f, 1)))
                .height(2).fillX().pad(5).row(); // 区切り線

        // --- ここから下はアイテムリスト（前回と同じですが、少し整理） ---
        Table contentTable = new Table();
        contentTable.top();

        Inventory inventory = game.getInventory();
        if (inventory != null && !inventory.getItems().isEmpty()) {
            for (final Item item : new java.util.ArrayList<>(inventory.getItems())) {

                // ※注意: item.data.name が日本語だと文字化けして表示されません。今は英語推奨です。
                TextButton itemButton = new TextButton(item.data.name, skin);
                itemButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        useItem(item);
                    }
                });

                Label qtyLabel = new Label("x " + item.quantity, skin);

                contentTable.add(itemButton).left().fillX().expandX().pad(5);
                contentTable.add(qtyLabel).right().pad(5);
                contentTable.row();
            }
        } else {
            contentTable.add(new Label("No Items", skin));
        }

        // アイテムリストを追加
        window.add(contentTable).expand().top().fillX();
        stage.addActor(window);
    }

    // ▼ アイテム使用のロジック
    private void useItem(Item item) {
        String type = item.data.type; // JSONにある "POTION" や "WEAPON"

        if ("POTION".equals(type)) {
            // 回復アイテムの場合
            System.out.println(item.data.name + " を使った！");

            // GameStateのHPを回復させる
            // item.data.value を回復量として使ってもいいですね
            game.getGameState().heal(item.data.value);

            // アイテムを1つ減らす
            item.quantity--;
            if (item.quantity <= 0) {
                game.getInventory().getItems().remove(item);
            }

            // 画面を再描画して、個数やアイテム一覧を更新する
            rebuildMenu();

        } else if ("WEAPON".equals(type)) {
            // 装備品の場合
            System.out.println(item.data.name + " を装備した！（未実装）");
            // 装備ロジックはまだないので、メッセージだけ
        } else {
            System.out.println("これは使えません。");
        }
    }

    public void updateAndRender(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            toggle();
        }
        if (isVisible) {
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public boolean isVisible() {
        return isVisible;
    }
}
