package knight.nameless;

import com.badlogic.gdx.Game;

public class Learning extends Game {

    public static Learning INSTANCE;


    public Learning() {
        INSTANCE = this;
    }

    @Override
    public void create() {
        setScreen(new MainMenuScreen(0));
    }
}
