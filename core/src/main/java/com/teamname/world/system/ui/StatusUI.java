package com.teamname.world.system.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teamname.world.AdventureRPG;
import com.teamname.world.system.FontSystem;
import com.teamname.world.system.GameState;
import com.teamname.world.system.Character; // 追加

public class StatusUI {
    private AdventureRPG game;
    private Stage stage;
    private Skin skin;
    private boolean isVisible = false;

    public StatusUI(AdventureRPG game) {
        this.game = game;
        createUI();
    }

    private void createUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin();
        createBasicSkin();
    }

    private void createBasicSkin() {
        // 共通のスキン作成ロジック（本来は共通クラス化すべきですが、今回はコピーします）
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", texture);

        BitmapFont font = FontSystem.createJapaneseFont(24);
        skin.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.9f);
        skin.add("default", windowStyle);
    }

    private void rebuildWindow() {
        stage.clear();
        Window window = new Window("Status (C)", skin);

        // 画面サイズの90%幅、80%高さ
        float w = Math.max(600, Gdx.graphics.getWidth() * 0.9f);
        float h = Math.max(500, Gdx.graphics.getHeight() * 0.8f);
        window.setSize(w, h);
        window.setPosition((Gdx.graphics.getWidth() - w) / 2f, (Gdx.graphics.getHeight() - h) / 2f);

        TextButton closeBtn = new TextButton("X", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        window.getTitleTable().add(closeBtn).size(40, 40).padRight(10);

        Table content = new Table();
        GameState state = game.getGameState();

        if (state.partyMembers != null) {
            for (com.teamname.world.system.Character c : state.partyMembers) {
                Table charTable = new Table();
                charTable.add(new Label(c.name, skin)).colspan(2).center().row();

                charTable.add(new Label("LV: " + c.level, skin)).left();
                charTable.add(new Label(" EXP: " + c.exp, skin)).left().row();

                charTable.add(new Label("HP: " + c.currentHp + "/" + c.maxHp, skin)).left();
                charTable.add(new Label(" MP: " + c.currentMp + "/" + c.maxMp, skin)).left().row();

                charTable.add(new Label("Str: " + c.str, skin)).left();
                charTable.add(new Label(" Def: " + c.def, skin)).left().row();

                charTable.add(new Label("ATK: " + c.getAttack(game.getDataLoader()), skin)).left();
                charTable.add(new Label(" DEF: " + c.getDefense(game.getDataLoader()), skin)).left().row();

                // 装備ボタン
                final com.teamname.world.system.Character character = c;

                String weaponName = "None";
                if (c.equippedWeaponId != -1) {
                    com.teamname.world.system.ItemData d = game.getDataLoader().getItemDataById(c.equippedWeaponId);
                    if (d != null)
                        weaponName = d.name;
                }
                TextButton weaponBtn = new TextButton("Wpn: " + weaponName, skin);
                weaponBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showEquipmentSelectDialog(character, "WEAPON");
                    }
                });

                String armorName = "None";
                if (c.equippedArmorId != -1) {
                    com.teamname.world.system.ItemData d = game.getDataLoader().getItemDataById(c.equippedArmorId);
                    if (d != null)
                        armorName = d.name;
                }
                TextButton armorBtn = new TextButton("Arm: " + armorName, skin);
                armorBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showEquipmentSelectDialog(character, "ARMOR");
                    }
                });

                charTable.add(weaponBtn).fillX().pad(2);
                charTable.add(armorBtn).fillX().pad(2).row();

                charTable.setBackground(skin.newDrawable("white", 0.2f, 0.2f, 0.2f, 0.5f));

                // 動的幅調整
                float colWidth = (w - 100) / 2;
                content.add(charTable).pad(10).width(colWidth);

                // 2列で折り返し
                if (content.getCells().size % 2 == 0)
                    content.row();
            }
        }
        window.add(content).expand().top().pad(20);
        stage.addActor(window);
    }

    private void showEquipmentSelectDialog(final com.teamname.world.system.Character character, final String type) {
        final com.badlogic.gdx.scenes.scene2d.ui.Dialog dialog = new com.badlogic.gdx.scenes.scene2d.ui.Dialog(
                "Select " + type, skin) {
            @Override
            protected void result(Object object) {
                // キャンセルなどの処理
            }
        };

        Table content = dialog.getContentTable();

        // 装備解除ボタン
        TextButton unequipBtn = new TextButton("Unequip", skin);
        unequipBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ("WEAPON".equals(type))
                    character.equippedWeaponId = -1;
                else
                    character.equippedArmorId = -1;
                dialog.hide();
                rebuildWindow(); // 更新
            }
        });
        content.add(unequipBtn).fillX().pad(5).row();

        // インベントリから該当タイプのアイテムを検索
        boolean found = false;
        for (final com.teamname.world.system.Item item : game.getInventory().getItems()) {
            if (item.data.type.equalsIgnoreCase(type)) {
                found = true;
                TextButton itemBtn = new TextButton(
                        item.data.name + " (" + item.data.power + "/" + item.data.defense + ")", skin);
                itemBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if ("WEAPON".equals(type))
                            character.equippedWeaponId = item.data.id;
                        else
                            character.equippedArmorId = item.data.id;
                        dialog.hide();
                        rebuildWindow();
                    }
                });
                content.add(itemBtn).fillX().pad(5).row();
            }
        }

        if (!found) {
            content.add(new Label("No items found", skin)).pad(10);
        }

        TextButton cancelBtn = new TextButton("Cancel", skin);
        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });
        dialog.getButtonTable().add(cancelBtn).pad(10);

        dialog.show(stage);
    }

    public void show() {
        isVisible = true;
        rebuildWindow();
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        isVisible = false;
        Gdx.input.setInputProcessor(null);
    }

    public void updateAndRender(float delta) {
        if (isVisible) {
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (isVisible) {
            rebuildWindow();
        }
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public boolean isVisible() {
        return isVisible;
    }
}
