package knight.nameless;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Kana {

    public final String name;
    public final Texture texture;
    public Sound sound;
    public int kanaIndex;
    public Rectangle bounds;
    public int touchTiming;

    public Kana(String name, Texture texture, Sound sound) {
        this.name = name;
        this.texture = texture;
        this.sound = sound;
    }

    public void dispose() {
        texture.dispose();
        sound.dispose();
    }
}
