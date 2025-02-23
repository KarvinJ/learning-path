package knight.nameless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {

    private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final Rectangle mouseBounds;
    private Rectangle selectedCellBounds;
    private int selectedIndex = 0;
    private boolean shouldDrawBigKana;
    private final int TOTAL_ROWS = 9;
    private final int TOTAL_COLUMNS = 8;
    private final int[][] grid;
    private final Array<Kana> kanas;
    private final Array<Kana> questions;
    private int questionIndex;

    public GameScreen() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        grid = new int[TOTAL_ROWS][TOTAL_COLUMNS];

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        selectedCellBounds = new Rectangle(SCREEN_WIDTH, 0, 0, 0);
        mouseBounds = new Rectangle(SCREEN_WIDTH, 0, 2, 2);

        initializeGrid();

        questions = new Array<>();
        loadQuestionsTexture(questions);
        questionIndex = MathUtils.random(0, questions.size - 1);

        kanas = new Array<>();
        loadKanasTexture(kanas);
    }

    private void loadQuestionsTexture(Array<Kana> questionsTexture) {

        String baseImagePath = "img/questions/";
        String imageExtension = ".jpg";

        //use a map where I'm going to have the question normal name and the name separate by ,
        String[] questions = new String[]{
            "chikatetsu", "hinomaru", "houki", "kendou", "kimono",
            "kuruma", "miko", "noren", "sentou", "shiro", "taiko", "tora"
        };

        for (String kanaName : questions) {

            String actualImagePath = baseImagePath + kanaName + imageExtension;
            questionsTexture.add(new Kana(kanaName, new Texture(actualImagePath), null));
        }
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

    private void initializeGrid() {

        int index = 0;
        for (int row = 0; row < TOTAL_ROWS; row++) {

            for (int column = 0; column < TOTAL_COLUMNS; column++) {

                grid[row][column] = index;
                index++;
            }
        }
    }

    private void drawGrid(ShapeRenderer shapeRenderer) {

        int HORIZONTAL_OFFSET = 3;
        int CELL_SIZE = 60;
        int VERTICAL_OFFSET = 3;
        int CELL_OFFSET = 3;

        for (int row = 0; row < TOTAL_ROWS; row++) {

            for (int column = 0; column < TOTAL_COLUMNS; column++) {

                Rectangle actualCell = new Rectangle(
                    column * CELL_SIZE + HORIZONTAL_OFFSET,
                    row * CELL_SIZE + VERTICAL_OFFSET,
                    CELL_SIZE - CELL_OFFSET,
                    CELL_SIZE - CELL_OFFSET
                );

                if (Gdx.input.justTouched()) {

                    Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

                    mouseBounds.x = worldCoordinates.x;
                    mouseBounds.y = worldCoordinates.y;

                    if (mouseBounds.overlaps(actualCell)) {

                        shouldDrawBigKana = true;

                        selectedCellBounds = actualCell;
                        selectedIndex = grid[row][column];

                        if (selectedIndex == kanas.size)
                            selectedIndex = kanas.size - 1;

                        kanas.get(selectedIndex).sound.play();
                    }
                }

                shapeRenderer.setColor(Color.BLACK);
//                shapeRenderer.setColor(0.11f, 0.11f, 0.11f, 1);
                shapeRenderer.rect(actualCell.x, actualCell.y, actualCell.width, actualCell.height);
            }
        }
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.LIGHT_GRAY);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawGrid(shapeRenderer);

        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(selectedCellBounds.x, selectedCellBounds.y, selectedCellBounds.width, selectedCellBounds.height);

        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Kana ActualQuestion = questions.get(questionIndex);

        batch.draw(ActualQuestion.texture, (float) SCREEN_WIDTH / 2 + 125, (float) SCREEN_HEIGHT / 2 - 50, 228, 320);

        Texture actualKanaTexture = kanas.get(selectedIndex).texture;
        Rectangle kanaBounds = new Rectangle((float) SCREEN_WIDTH / 2 + 150, 50, 180, 134);

        if (shouldDrawBigKana)
            batch.draw(actualKanaTexture, kanaBounds.x, kanaBounds.y, kanaBounds.width, kanaBounds.height);

        batch.draw(actualKanaTexture, selectedCellBounds.x, selectedCellBounds.y, selectedCellBounds.width, selectedCellBounds.height);

        batch.end();
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
