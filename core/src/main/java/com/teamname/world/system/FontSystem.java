package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontSystem {

    // 日本語フォントを生成する
    public static BitmapFont createJapaneseFont(int size) {
        FreeTypeFontGenerator generator = null;

        // 1. assetsフォルダ内のフォントを探す
        // システムフォントを assets/fonts/ipam.ttf にコピーしてあることを想定
        FileHandle fontFile = Gdx.files.internal("fonts/ipam.ttf");

        // 2. なければWindowsのシステムフォントを使用する (バックアップ)
        if (!fontFile.exists()) {
            fontFile = Gdx.files.absolute("C:/Windows/Fonts/msgothic.ttc");
            if (!fontFile.exists()) {
                fontFile = Gdx.files.absolute("C:/Windows/Fonts/meiryo.ttc");
            }
        }

        if (fontFile.exists()) {
            System.out.println("Using font: " + fontFile.path());
            generator = new FreeTypeFontGenerator(fontFile);
        } else {
            // 見つからない場合はデフォルト（文字化けするがエラー落ちよりマシ）
            System.err.println("日本語フォントが見つかりませんでした。");
            return new BitmapFont();
        }

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 1;

        // 収録する文字セット（日本語を含む）
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS +
                "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん" +
                "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン" +
                "がぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽ" +
                "ガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ" +
                "っゃゅょッャュョー、。！？" +
                "漢字剣盾鎧兜薬草火水風土光闇魔王城村人勇者旅冒険経験値金力守早賢購入売" +
                "開始終了閉鎖会話聞戦闘逃攻撃防御道具魔法装備外交換選択" + // UI actions
                "所持現在進行状況保存完了失敗壊復元" + // System messages
                "主人公戦士僧侶" + // Classes
                "武器防具" + // Item types
                "HPMPLvExG" + // Stats
                "参北地巣食任準備頼期待来倒今依頼"; // Dialog specific ("倒" Added)

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose(); // ジェネレーターはもう不要
        return font;
    }
}

