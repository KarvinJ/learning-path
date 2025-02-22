package knight.nameless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {

    private final int SCREEN_WIDTH = Gdx.graphics.getWidth();;
    private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final int TOTAL_ROWS = 10;
    private final int TOTAL_COLUMNS = 7;
    private final int CELL_SIZE = 55;
    private final int VERTICAL_OFFSET = 0;
    private final int HORIZONTAL_OFFSET = 0;
    private final int CELL_OFFSET = 3;
    private final Rectangle mouseBounds;
    private Rectangle selectedCellBounds;

    private final int[][] grid;

    public GameScreen() {

        shapeRenderer = new ShapeRenderer();
        grid = new int[TOTAL_ROWS][TOTAL_COLUMNS];

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        mouseBounds = new Rectangle(SCREEN_WIDTH, 0, 2, 2);

        initializeGrid();
    }

    private void initializeGrid()
    {
        for (int row = 0; row < TOTAL_ROWS; row++) {

            for (int column = 0; column < TOTAL_COLUMNS; column++) {

                grid[row][column] = 0;
            }
        }
    }

    void drawGrid(ShapeRenderer shapeRenderer)
    {
        for (int row = 0; row < TOTAL_ROWS; row++) {

            for (int column = 0; column < TOTAL_COLUMNS; column++) {

                Rectangle actualCell = new Rectangle(column * CELL_SIZE + HORIZONTAL_OFFSET, row * CELL_SIZE + VERTICAL_OFFSET, CELL_SIZE - CELL_OFFSET, CELL_SIZE - CELL_OFFSET);

                if (mouseBounds.overlaps(actualCell)) {

                    shapeRenderer.setColor(Color.WHITE);
                    selectedCellBounds = actualCell;
                }
                else
                    shapeRenderer.setColor(0.17f, 0.17f, 0.49f, 0);

                shapeRenderer.rect(actualCell.x, actualCell.y, actualCell.width, actualCell.height);
            }
        }
    }

    @Override
    public void render(float delta) {

        camera.update();

        ScreenUtils.clear(0.11f,0.11f,0.10f,0);

        if (Gdx.input.justTouched()) {

            Vector3 worldCoordinates = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(),0));

            mouseBounds.x = worldCoordinates.x;
            mouseBounds.y = worldCoordinates.y;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawGrid(shapeRenderer);

        shapeRenderer.setColor(Color.WHITE);

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
