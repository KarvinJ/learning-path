package knight.nameless;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Kana {

    public final String kanaName;
    public final Texture texture;
    public final Sound sound;

    public Kana(String kanaName, Texture texture, Sound sound) {
        this.kanaName = kanaName;
        this.texture = texture;
        this.sound = sound;
    }

    public void dispose() {

        texture.dispose();
        sound.dispose();
    }
}
