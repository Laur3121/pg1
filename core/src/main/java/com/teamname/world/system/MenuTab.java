package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

        try {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
            skin.addRegions(atlas);

            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/font.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん"
                + "がぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽッャュョ一二三四五六七八九十百千万薬草毒消し檜の棒、。！？";
            BitmapFont font = generator.generateFont(parameter);
            generator.dispose();
            skin.add("default-font", font);
            skin.load(Gdx.files.internal("skin/uiskin.json"));
        } catch (Exception e) {
            e.printStackTrace();
            // エラー時はデフォルトスキンで続行
            skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        }
    }

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
        window.getTitleTable().add(closeBtn).height(20);

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
        window.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(skin.newDrawable("white", 0.5f, 0.5f, 0.5f, 1))).height(2).fillX().pad(5).row(); // 区切り線

        // --- ここから下はアイテムリスト（前回と同じですが、少し整理） ---
        Table contentTable = new Table();
        contentTable.top();

        Inventory inventory = game.getInventory();
        if (inventory != null && !inventory.getItems().isEmpty()) {
            for (final Item item : new java.util.ArrayList<>(inventory.getItems())) {

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
