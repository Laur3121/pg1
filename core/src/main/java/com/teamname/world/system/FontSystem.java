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
        // TODO: ユーザーがフォントを追加したらここを変える
        // FileHandle fontFile = Gdx.files.internal("fonts/myfont.ttf");

        // 2. Windowsのシステムフォントを使用する (開発用)
        // ※Android等にビルドする際はassetsにフォントファイルを含める必要があります
        FileHandle fontFile = Gdx.files.absolute("C:/Windows/Fonts/msgothic.ttc");

        if (!fontFile.exists()) {
            // 別のフォントを試す
            fontFile = Gdx.files.absolute("C:/Windows/Fonts/meiryo.ttc");
        }

        if (fontFile.exists()) {
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
                "漢字剣盾鎧兜薬草火水風土光闇魔王城村人勇者旅冒険経験値金力守早賢" + // 必要な漢字を列挙
                "HPMPLvExG";

        // すべての漢字を含めると重くなるので、使用する文字だけ追加するのが軽量です
        // Google Fontsなどから .ttf をダウンロードして assets に入れるのが一番安全です。

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose(); // ジェネレーターはもう不要
        return font;
    }
}
