package knight.nameless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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

public class GameScreen extends ScreenAdapter {

    private final Learning game;
    private final int SCREEN_WIDTH = 960;
    private final int SCREEN_HEIGHT = 544;
    private final OrthographicCamera camera;
    private final ExtendViewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Rectangle mouseBounds;
    private int selectedIndex;
    private final Array<Kana> selectedKanas;
    private final int TOTAL_ROWS = 9;
    private final int TOTAL_COLUMNS = 8;
    private final int[][] grid;
    private final Array<Kana> kanas;
    private final Array<Kana> questions;
    private final Array<Kana> correctKanas;
    private final Array<String> correctKanaNames;
    private int questionIndex;
    private int completeQuestionQuantity;
    private float timer = 60;
    private float score;
    private float attempts;

    public GameScreen() {

        game = Learning.INSTANCE;

        font = new BitmapFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        grid = new int[TOTAL_ROWS][TOTAL_COLUMNS];

        initializeGrid(grid);

        //if we want to make a game that use touch we need to have a camera and set this camera to ortho if we aren't using viewports
        camera = new OrthographicCamera();
        //if we set viewport of the camera to SCREEN_WIDTH, SCREEN_HEIGHT, then there is no need to add .setProjectionMatrix to our batch.
//        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        viewport = new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);

        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);

        mouseBounds = new Rectangle(SCREEN_WIDTH, 0, 2, 2);

        correctKanaNames = new Array<>();

        questions = new Array<>();
        loadQuestionsTexture(questions);
        questionIndex = MathUtils.random(0, questions.size - 1);

        kanas = new Array<>();
        loadKanasTexture(kanas);

        correctKanas = new Array<>();
        selectedKanas = new Array<>();
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

            var nameSeparatedInKanas = separatedKanas[i];
            String actualImagePath = "img/questions/" + questionsName[i] + ".jpg";
            questionsTexture.add(new Kana(nameSeparatedInKanas, new Texture(actualImagePath), null));
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

        mouseBounds.x = worldCoordinates.x;
        mouseBounds.y = worldCoordinates.y;

        final int HORIZONTAL_OFFSET = 3;
        final int CELL_SIZE = 60;
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

            if (Gdx.input.isTouched() && mouseBounds.overlaps(kana.bounds)) {

                kana.touchTiming++;
            }
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

        Kana selectedKana = kanas.get(selectedIndex);
        Rectangle kanaBounds = new Rectangle((float) SCREEN_WIDTH / 2 + 150, 90, 180, 134);

        Kana actualQuestion = questions.get(questionIndex);

        ScreenUtils.clear(Color.LIGHT_GRAY);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawGrid(shapeRenderer, actualQuestion);

        shapeRenderer.setColor(Color.LIGHT_GRAY);
        for (var kana : selectedKanas) {

            shapeRenderer.rect(kana.bounds.x, kana.bounds.y, kana.bounds.width, kana.bounds.height);
        }

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect((float) SCREEN_WIDTH / 2 + 42, 516, 32, 24);
        shapeRenderer.rect((float) SCREEN_WIDTH - 78, 516, 32, 24);

        shapeRenderer.end();

        if (timer > 0)
            timer -= delta;
        else {

            attempts++;
            timer = 60;
            resetSelectedKanas();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.draw(batch, String.valueOf((int) timer), (float) SCREEN_WIDTH / 2 + 50, 535);
        font.draw(batch, String.valueOf((int) score), (float) SCREEN_WIDTH - 74, 535);

        batch.draw(actualQuestion.texture, (float) SCREEN_WIDTH / 2 + 125, (float) SCREEN_HEIGHT / 2 - 50, 228, 320);

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

        Rectangle alreadyCheckBounds = new Rectangle((float) SCREEN_WIDTH / 2, 0, 128, 107);

        for (Kana kana : correctKanas) {

            if (kana.touchTiming > 150) {

                float kanaPosition = kana.kanaIndex * alreadyCheckBounds.width;
                kanaPosition -= 5;

                batch.draw(kana.texture, alreadyCheckBounds.x + kanaPosition, alreadyCheckBounds.y, alreadyCheckBounds.width, alreadyCheckBounds.height);
            }
        }

        batch.end();

        if (completeQuestionQuantity == 5) {

            saveHighScore((int)score);
            game.setScreen(new MainMenuScreen((int)score));
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

        for (Kana kana : kanas)
            kana.dispose();
    }
}
