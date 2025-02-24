package knight.nameless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameDataHelper {

    public static void saveHighScore(int score){

        Preferences preferences = Gdx.app.getPreferences("learning-path");

        if (score < loadHighScore())
            return;

        preferences.putInteger("high-score", score);

        preferences.flush();
    }

    public static int loadHighScore(){

        Preferences preferences = Gdx.app.getPreferences("learning-path");

        return preferences.getInteger("high-score");
    }
}
