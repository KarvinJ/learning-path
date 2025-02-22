package knight.nameless;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {

    private final ShapeRenderer shapeRenderer;

    public GameScreen() {

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0,0,0,0);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.WHITE);

        shapeRenderer.rect(32, 32, 32, 32);

        shapeRenderer.end();
    }

    @Override
    public void hide() {
        shapeRenderer.dispose();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
