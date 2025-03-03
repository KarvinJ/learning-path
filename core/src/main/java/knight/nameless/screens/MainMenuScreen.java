package knight.nameless.screens;

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

import knight.nameless.GameDataHelper;
import knight.nameless.Learning;

public class MainMenuScreen extends ScreenAdapter {

    private final Learning game;
    private final Skin skin;
    private final Stage stage;
    private final Viewport viewport;
    private final Music music;
    public AssetDescriptor<Skin> uiSkin;

    public MainMenuScreen(int score) {

        game = Learning.INSTANCE;

        uiSkin = new AssetDescriptor<>("ui/uiskin.json", Skin.class, new SkinLoader.SkinParameter("ui/uiskin.atlas"));

        AssetManager assetManager = new AssetManager();
        assetManager.load(uiSkin);
        assetManager.finishLoading();

        skin = assetManager.get(uiSkin);

        viewport = new ExtendViewport(1280, 720);
        stage = new Stage(viewport);

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        Label titleLabel = new Label("Learning Path", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        table.add(titleLabel).expandX().padBottom(15);
        table.row();

        int actualHighScore = Math.max(score, GameDataHelper.loadHighScore());

        Label scoreLabel = new Label("High Score: " + actualHighScore, new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        table.add(scoreLabel).expandX().padBottom(15);
        table.row();

        addButton(table).addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen());
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        Gdx.input.setInputProcessor(stage);

        music = Gdx.audio.newMusic(Gdx.files.internal("music/peaceful.wav"));

        music.play();
        music.setVolume(0.2f);
        music.setLooping(true);
    }

    private TextButton addButton(Table table) {

        TextButton textButton = new TextButton("PLAY", skin);

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
