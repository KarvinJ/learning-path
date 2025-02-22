package knight.nameless;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Kana {

    private final int kanaIndex;
    private final String kanaName;
    private final Texture texture;
    private final Rectangle bounds;
    private final Sound sound;

    public Kana(int kanaIndex, String kanaName, Texture texture, Rectangle bounds, Sound sound) {
        this.kanaIndex = kanaIndex;
        this.kanaName = kanaName;
        this.texture = texture;
        this.bounds = bounds;
        this.sound = sound;
    }
}
