package com.teamname.world.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;

/**
 * BGMとSE（効果音）を管理するクラス。
 */
public class AudioManager {

    private Music currentMusic;
    private HashMap<String, Sound> soundCache;
    private float masterVolume = 0.5f;

    public AudioManager() {
        soundCache = new HashMap<>();
    }

    /**
     * BGMを再生します。
     * 
     * @param fileName assets/music/ 以下のファイル名（例: "field.mp3"）
     * @param loop     ループするかどうか
     */
    public void playBgm(String fileName, boolean loop) {
        // 同じ曲が鳴っているなら何もしない
        // （本来はファイルパス比較などをしますが、簡略化のため停止して再生）
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose(); // メモリ解放
        }

        try {
            FileHandle file = Gdx.files.internal("music/" + fileName);
            if (file.exists()) {
                currentMusic = Gdx.audio.newMusic(file);
                currentMusic.setLooping(loop);
                currentMusic.setVolume(masterVolume);
                currentMusic.play();
                System.out.println("BGM再生: " + fileName);
            } else {
                System.out.println("BGMファイルが見つかりません: " + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBgm() {
        if (currentMusic != null) {
            currentMusic.stop();
            // disposeは次の曲をロードする時か、ゲーム終了時で良いが、
            // ここでは管理を一つだけにするのでdisposeしてしまう
            currentMusic.dispose();
            currentMusic = null;
        }
    }

    /**
     * 効果音（SE）を再生します。
     * 
     * @param fileName assets/sounds/ 以下のファイル名（例: "hit.wav"）
     */
    public void playSe(String fileName) {
        Sound sound = soundCache.get(fileName);

        if (sound == null) {
            try {
                FileHandle file = Gdx.files.internal("sounds/" + fileName);
                if (file.exists()) {
                    sound = Gdx.audio.newSound(file);
                    soundCache.put(fileName, sound);
                } else {
                    System.out.println("SEファイルが見つかりません: " + fileName);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        sound.play(masterVolume);
    }

    public void dispose() {
        if (currentMusic != null) {
            currentMusic.dispose();
        }
        for (Sound sound : soundCache.values()) {
            sound.dispose();
        }
        soundCache.clear();
    }
}
