package knight.nameless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends ScreenAdapter {

    private final Learning game;
    private final Skin skin;
    private final Stage stage;
    private final Viewport viewport;
    private final Music music;
    public AssetDescriptor<Skin> uiSkin;

    public MainMenuScreen(int score) {

        this.game = Learning.INSTANCE;

        uiSkin = new AssetDescriptor<>("ui/uiskin.json", Skin.class, new SkinLoader.SkinParameter("ui/uiskin.atlas"));

        AssetManager assetManager = new AssetManager();

        assetManager.load(uiSkin);

        assetManager.finishLoading();

        skin = assetManager.get(uiSkin);

        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(viewport);

        Table table = new Table();

        table.setFillParent(true);

        stage.addActor(table);

        int actualHighScore = score;

        if (score < GameDataHelper.loadHighScore())
            actualHighScore = GameDataHelper.loadHighScore();

        Label titleLabel = new Label("Learning Path", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        Label scoreLabel = new Label("High Score: " + actualHighScore, new Label.LabelStyle(new BitmapFont(), Color.BLACK));

        table.add(titleLabel).expandX().padBottom(15);
        table.row();

        table.add(scoreLabel).expandX().padBottom(15);
        table.row();

        addButton(table,"Play").addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen());
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        Gdx.input.setInputProcessor(stage);

        music = Gdx.audio.newMusic(Gdx.files.internal("music/peaceful.wav"));

        music.play();
        music.setVolume(0.4f);
        music.setLooping(true);
    }

    private TextButton addButton(Table table, String buttonName) {

        TextButton textButton = new TextButton(buttonName, skin);

        table.add(textButton).width(400).height(60).padBottom(15);
        table.row();

        return textButton;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.LIGHT_GRAY);

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

        stage.dispose();
        skin.dispose();
        music.dispose();
    }
}
