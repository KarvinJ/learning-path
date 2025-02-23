package knight.nameless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private final int TOTAL_ROWS = 9;
    private final int TOTAL_COLUMNS = 8;
    private final int CELL_SIZE = 60;
    private final int VERTICAL_OFFSET = 3;
    private final int HORIZONTAL_OFFSET = 0;
    private final int CELL_OFFSET = 3;
    private final Rectangle mouseBounds;
    private Rectangle selectedCellBounds;
    private int selectedIndex = 0;
    private boolean shouldDrawBigKana;
    private final Texture questionTexture;

    private final Array<Kana> kanas;

    private final int[][] grid;

    public GameScreen() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        grid = new int[TOTAL_ROWS][TOTAL_COLUMNS];

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        selectedCellBounds = new Rectangle(SCREEN_WIDTH, 0, 0, 0);

        mouseBounds = new Rectangle(SCREEN_WIDTH, 0, 2, 2);

        initializeGrid();

        kanas = new Array<>();
        loadKanasTexture(kanas);
        questionTexture = new Texture("img/questions/sentou.png");
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

        for (int row = 0; row < TOTAL_ROWS; row++) {

            for (int column = 0; column < TOTAL_COLUMNS; column++) {

                Rectangle actualCell = new Rectangle(column * CELL_SIZE + HORIZONTAL_OFFSET, row * CELL_SIZE + VERTICAL_OFFSET, CELL_SIZE - CELL_OFFSET, CELL_SIZE - CELL_OFFSET);

                if (Gdx.input.justTouched()) {

                    Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

                    mouseBounds.x = worldCoordinates.x;
                    mouseBounds.y = worldCoordinates.y;

                    if (mouseBounds.overlaps(actualCell)) {

                        shouldDrawBigKana = true;

                        shapeRenderer.setColor(Color.WHITE);
                        selectedCellBounds = actualCell;

                        selectedIndex = grid[row][column];

                        if (selectedIndex > 70)
                            selectedIndex = 70;

                        kanas.get(selectedIndex).sound.play();
                    }
                }

                shapeRenderer.setColor(0.17f, 0.17f, 0.49f, 0);
                shapeRenderer.rect(actualCell.x, actualCell.y, actualCell.width, actualCell.height);
            }
        }
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0.78f, 0.78f, 0.78f, 1);

        Texture actualKanaTexture = kanas.get(selectedIndex).texture;
        Rectangle kanaBounds = new Rectangle((float) SCREEN_WIDTH / 2 + 120, 50, 180, 125);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawGrid(shapeRenderer);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(selectedCellBounds.x, selectedCellBounds.y, selectedCellBounds.width, selectedCellBounds.height);

        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(questionTexture, (float) SCREEN_WIDTH / 2 + 125, (float) SCREEN_HEIGHT / 2 - 50, questionTexture.getWidth(), questionTexture.getHeight());

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

        for (Kana kana : kanas) {
            kana.dispose();
        }
    }
}
