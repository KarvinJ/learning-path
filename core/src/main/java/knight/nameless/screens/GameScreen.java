package knight.nameless.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static knight.nameless.GameDataHelper.saveHighScore;

import knight.nameless.Kana;
import knight.nameless.Learning;

public class GameScreen extends ScreenAdapter {

    private final Learning game;
    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;
    private final OrthographicCamera camera;
    private final ExtendViewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Array<Kana> kanas;
    private final Array<Kana> selectedKanas;
    private final Array<Kana> correctKanas;
    private final Array<String> correctKanaNames;
    private final Array<Kana> questions;
    private final int TOTAL_ROWS = 9;
    private final int TOTAL_COLUMNS = 8;
    private final int[][] grid;
    private int questionIndex;
    private int selectedIndex;
    private int completeQuestionQuantity;
    private float timer = 60;
    private float score;
    private float attempts;
    private final Music music;

    public GameScreen() {

        game = Learning.INSTANCE;

        font = new BitmapFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        grid = new int[TOTAL_ROWS][TOTAL_COLUMNS];

        initializeGrid(grid);

        //if we want to make a game that use touch we need to have a camera and set this camera to ortho if we aren't using viewports
        camera = new OrthographicCamera();
        //if we set viewport of the camera to SCREEN_WIDTH, SCREEN_HEIGHT, then we don't need to use .setProjectionMatrix in our batch.
//        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);

        viewport = new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);

        correctKanaNames = new Array<>();

        questions = new Array<>();
        loadQuestionsTexture(questions);
        questionIndex = MathUtils.random(0, questions.size - 1);

        kanas = new Array<>();
        loadKanasTexture(kanas);

        correctKanas = new Array<>();
        selectedKanas = new Array<>();
        music = Gdx.audio.newMusic(Gdx.files.internal("music/peaceful.wav"));

        music.play();
        music.setVolume(0.2f);
        music.setLooping(true);
    }

    private void loadQuestionsTexture(Array<Kana> questionsTexture) {

        String[] questionsName = new String[]{
            "chikatetsu", "hinomaru", "houki", "kendou", "kimono",
            "kuruma", "miko", "noren", "sentou", "shiro", "taiko", "tora"
        };

        String[] separatedKanas = new String[]{
            "chi,ka,te,tsu", "hi,no,ma,ru", "ho,u,ki", "ke,n,do,u", "ki,mo,no",
            "ku,ru,ma", "mi,ko", "no,re,n", "se,n,to,u", "shi,ro", "ta,i,ko", "to,ra"
        };

        for (int i = 0; i < questionsName.length; i++) {

            String actualImagePath = "img/questions/" + questionsName[i] + ".jpg";

            var questionTexture = new Texture(actualImagePath);

            var questionBounds = new Rectangle(
                (float) SCREEN_WIDTH / 2 + questionTexture.getWidth() / 2f + 20,
                (float) SCREEN_HEIGHT / 2 - 80,
                questionTexture.getWidth(),
                questionTexture.getHeight()
            );

            var nameSeparatedInKanas = separatedKanas[i];
            var actualQuestion = new Kana(nameSeparatedInKanas, new Texture(actualImagePath), null);
            actualQuestion.bounds = questionBounds;
            questionsTexture.add(actualQuestion);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void loadKanasTexture(Array<Kana> kanas) {

        String baseAudioPath = "sounds/";
        String baseImagePath = "img/kanas/";
        String audioExtension = ".mp3";
        String imageExtension = ".png";

        String[] kanaNames = new String[]{
            "a", "e", "i", "o", "u",
            "ka", "ga", "ki", "gi", "ku",
            "gu", "ke", "ge", "ko", "go",
            "sa", "za", "shi", "ji", "su",
            "zu", "se", "ze", "so", "zo",
            "ta", "da", "chi", "di", "tsu",
            "du", "te", "de", "to", "do",
            "na", "ni", "nu", "ne", "no",
            "ha", "ba", "pa", "hi", "bi",
            "pi", "fu", "bu", "pu", "he",
            "be", "pe", "ho", "bo", "po",
            "ma", "mi", "mu", "me", "mo",
            "ya", "yu", "yo",
            "ra", "ri", "ru", "re", "ro",
            "wa", "wo", "n"
        };

        for (String kanaName : kanaNames) {

            String actualImagePath = baseImagePath + kanaName + imageExtension;

            String actualAudioPath = baseAudioPath + kanaName + audioExtension;
            Sound actualSound = Gdx.audio.newSound(Gdx.files.internal(actualAudioPath));

            kanas.add(new Kana(kanaName, new Texture(actualImagePath), actualSound));
        }
    }

    private void initializeGrid(int[][] grid) {

        int index = 0;
        for (int row = 0; row < TOTAL_ROWS; row++) {

            for (int column = 0; column < TOTAL_COLUMNS; column++) {

                grid[row][column] = index;
                index++;
            }
        }
    }

    private void drawGrid(ShapeRenderer shapeRenderer, Kana actualQuestion) {

        Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        var mouseBounds = new Rectangle(worldCoordinates.x, worldCoordinates.y, 2, 2);

        final int HORIZONTAL_OFFSET = 3;
        final int CELL_SIZE = 80;
        final int VERTICAL_OFFSET = 3;
        final int CELL_OFFSET = 3;

        for (int row = 0; row < TOTAL_ROWS; row++) {

            for (int column = 0; column < TOTAL_COLUMNS; column++) {

                Rectangle actualCell = new Rectangle(
                    column * CELL_SIZE + HORIZONTAL_OFFSET,
                    row * CELL_SIZE + VERTICAL_OFFSET,
                    CELL_SIZE - CELL_OFFSET,
                    CELL_SIZE - CELL_OFFSET
                );

                if (Gdx.input.justTouched() && mouseBounds.overlaps(actualCell)) {

                    selectedIndex = grid[row][column];

                    if (selectedIndex == kanas.size)
                        selectedIndex = kanas.size - 1;

                    var selectedKana = kanas.get(selectedIndex);
                    selectedKana.sound.play();

                    selectedKana.bounds = actualCell;

                    boolean kanaIsAlreadyAdded = false;
                    for (var kana : selectedKanas) {

                        if (kana.name.equals(selectedKana.name)) {
                            kanaIsAlreadyAdded = true;
                            break;
                        }
                    }

                    if (!kanaIsAlreadyAdded)
                        selectedKanas.add(selectedKana);

                    checkIfSelectedKanaIsCorrect(actualQuestion, selectedKana);
                }

                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.rect(actualCell.x, actualCell.y, actualCell.width, actualCell.height);
            }
        }

        for (var kana : correctKanas) {

            if (Gdx.input.isTouched() && mouseBounds.overlaps(kana.bounds))
                kana.touchTiming++;
        }
    }

    private void checkIfSelectedKanaIsCorrect(Kana actualQuestion, Kana selectedKana) {

        String[] kanasOfTheQuestion = actualQuestion.name.split(",");

        int actualKanaIndex = 0;

        for (var kana : kanasOfTheQuestion) {

            if (!correctKanaNames.contains(kana, false) && kana.equals(selectedKana.name)) {

                correctKanaNames.add(kana);

                selectedKana.kanaIndex = actualKanaIndex;
                selectedKana.touchTiming++;
                correctKanas.add(selectedKana);
            }

            actualKanaIndex++;
        }

        boolean allKanasAreFound = true;
        for (var correctKana : correctKanas) {

            if (correctKana.touchTiming < 150) {

                allKanasAreFound = false;
                break;
            }
        }

        if (allKanasAreFound && correctKanaNames.size == kanasOfTheQuestion.length) {

            questionIndex = MathUtils.random(0, questions.size - 1);
            completeQuestionQuantity++;
            resetSelectedKanas();

            score += timer;
            score -= attempts;

            attempts = 0;
            timer = 60;
        }
    }

    private void resetSelectedKanas() {

        //resetting all touchTiming to 0 to avoid 1 click reveal in next rounds.
        for (var kana : correctKanas)
            kana.touchTiming = 0;

        correctKanas.clear();
        correctKanaNames.clear();
        selectedKanas.clear();
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.LIGHT_GRAY);

        //the .setProjectionMatrix is EXTREMELY necessary, when working with viewports, without this any viewport is useless.
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Kana actualQuestion = questions.get(questionIndex);
        drawGrid(shapeRenderer, actualQuestion);

        shapeRenderer.setColor(Color.LIGHT_GRAY);
        for (var kana : selectedKanas) {

            shapeRenderer.rect(kana.bounds.x, kana.bounds.y, kana.bounds.width, kana.bounds.height);
        }

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect((float) SCREEN_WIDTH / 2 + 72, SCREEN_HEIGHT - 39, 32, 24);
        shapeRenderer.rect((float) SCREEN_WIDTH - 95, SCREEN_HEIGHT - 39, 32, 24);

        shapeRenderer.end();

        if (timer > 0)
            timer -= delta;
        else {

            attempts++;
            timer = 60;
            resetSelectedKanas();
        }

        //To every batch and shapeRenderer that I have I also need to set setProjectionMatrix,
        // it feels unnecessary, but if I don't set the ProjectionMatrix with every batch or shapeRenderer the viewport won't work.
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.draw(batch, String.valueOf((int) timer), (float) SCREEN_WIDTH / 2 + 80, SCREEN_HEIGHT - 20);
        font.draw(batch, String.valueOf((int) score), (float) SCREEN_WIDTH - 90, SCREEN_HEIGHT - 20);

        batch.draw(
            actualQuestion.texture,
            actualQuestion.bounds.x, actualQuestion.bounds.y,
            actualQuestion.bounds.width, actualQuestion.bounds.height
        );

        Kana selectedKana = kanas.get(selectedIndex);
        var kanaBounds = new Rectangle((float) SCREEN_WIDTH / 2 + 250, 134, 160, 134);

        if (!selectedKanas.isEmpty())
            batch.draw(selectedKana.texture, kanaBounds.x, kanaBounds.y, kanaBounds.width, kanaBounds.height);

        if (selectedKanas.size == 17) {

            selectedKanas.clear();

            for (var correctKana : correctKanas) {

                if (correctKana.touchTiming > 150)
                    selectedKanas.add(correctKana);
            }
        }
        for (var kana : selectedKanas) {

            batch.draw(kana.texture, kana.bounds.x, kana.bounds.y, kana.bounds.width, kana.bounds.height);
        }

        var alreadyCheckBounds = new Rectangle((float) SCREEN_WIDTH / 2, 0, kanaBounds.width, kanaBounds.height);

        for (Kana kana : correctKanas) {

            if (kana.touchTiming > 150) {

                float kanaPosition = kana.kanaIndex * alreadyCheckBounds.width;
                kanaPosition -= 5;

                batch.draw(kana.texture, alreadyCheckBounds.x + kanaPosition, alreadyCheckBounds.y, alreadyCheckBounds.width, alreadyCheckBounds.height);
            }
        }

        batch.end();

        if (completeQuestionQuantity == 5) {

            saveHighScore((int) score);
            game.setScreen(new MainMenuScreen((int) score));
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        music.dispose();

        for (Kana kana : kanas)
            kana.dispose();

        for (Kana question : questions)
            question.dispose();
    }
}
