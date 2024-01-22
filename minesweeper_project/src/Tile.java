import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A Tile represents a square on the Minesweeper game grid.
 * It is a subclass of StackPane and it contains an image or text
 * to display on the tile, as well as functionality for opening
 * and marking the tile. The possible images that can be shown on
 * the tile are a mine, a hypermine and a flag, with the first two
 * shown while the tile has color gray (opened), and the flag shown
 * while the tile has color blue (not opened).
 * The text object is displayed on the tile only when it's opened
 * and it is not a mine or a hypermine. It represents the number of
 * adjacent mines to the tile.
 * Each Tile object has a row and column index that represent its
 * position in the game grid. It also has booleans so it can be
 * classified as a mine, hypermine, hidden and marked (flagged).
 * When a Tile object is created, it takes in the column and row
 * indices, whether it is a mine or hypermine, the Game object, the
 * Stage object, the Scene object, the layout, and the size of the
 * tile. The constructor sets the various properties of the Tile,
 * including its appearance, position, and behavior when clicked.
 */

public class Tile extends StackPane {
    private Game game;
    private Scene scene;
    public int col, row;
    public boolean mine, hypermine, hidden = true, marked = false;
    public Text text = new Text();
    public Rectangle rect, alarmrect;
    public List<Tile> tilesClose;
    public List<Tile> tiles_hypermine;

    Image flag = new Image(getClass().getResourceAsStream("./images/flag.png"));
    ImageView flagimageView = new ImageView(flag);
    Image mineicon = new Image(getClass().getResourceAsStream("./images/mine.png"));
    ImageView mineimageView = new ImageView(mineicon);
    Image hypermineicon = new Image(getClass().getResourceAsStream("./images/hypermine.png"));
    ImageView hypermineimageView = new ImageView(hypermineicon);

    public Tile(int col, int row, boolean mine, boolean hypermine, Game game, Stage stage, Scene scene,
            BorderPane layout, int size) {
        this.scene = scene;
        this.game = game;
        this.col = col;
        this.row = row;
        this.mine = mine;
        this.hypermine = hypermine;
        rect = new Rectangle(size, size, Color.rgb(20, 100, 200));

        Stop[] stops = new Stop[] { new Stop(0, Color.web("#1E0043")), new Stop(1, Color.web("#00C2FF")) };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null, stops);

        rect.setFill(gradient);

        // set the mine image if mine else the text
        if (mine && hypermine == false) {
            mineimageView.setFitWidth(size > 40 ? 30 : 20);
            mineimageView.setFitHeight(size > 40 ? 30 : 20);
            mineimageView.setVisible(!hidden);
        } else if (hypermine == true) {
            hypermineimageView.setFitWidth(size > 40 ? 45 : 30);
            hypermineimageView.setFitHeight(size > 40 ? 45 : 30);
            hypermineimageView.setVisible(false);
        } else {
            text.setText("");
            text.setFont(Font.font((size > 40 ? 24 : 18)));
            text.setVisible(!hidden);
        }

        flagimageView.setFitWidth(size > 40 ? 30 : 20);
        flagimageView.setFitHeight(size > 40 ? 30 : 20);
        flagimageView.setVisible(marked);

        rect.setStroke(Color.BLACK);
        if (!hidden) {
            rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(200, 100, 20)));
            rect.setOnMouseExited(e -> rect.setFill(Color.rgb(20, 100, 200)));
        }

        setOnMouseClicked(e -> {
            if (!game.start || !hidden)
                return;

            if (e.getButton() == MouseButton.SECONDARY) {
                mark();
            } else {
                game.tries++;
                game.numbertries.setText(String.valueOf(game.tries));
                // System.out.println(game.tries);
                open(stage, layout);
            }
        });

        getChildren().addAll(rect, flagimageView);

        if (mine && hypermine == false)
            getChildren().add(mineimageView);
        else if (hypermine)
            getChildren().add(hypermineimageView);
        else
            getChildren().add(text);

        setTranslateX(row * size);
        setTranslateY(col * size);
    }

    /**
     * The open() method is called when the tile is left clicked. If the
     * tile is a mine, the game is over and the player loses (FinishedPopup
     * and game.saveStatsToFile() are called). If it was marked as a mine, the
     * flag is removed and the number of flags is decreased. The number of
     * tries is also incremented, but the player still loses. If the tile
     * is not a mine, the tile is opened and its adjacent tiles are checked
     * to see if they need to be opened as well. Same as before, if the tile
     * was flagged then we remode the flag and decrease the flag counter.
     * Also, the tries counter is increased as well. Finally, for every tile
     * that is opened and it isn't a mine we check if the number of opened
     * tiles is equal to the number of tiles that need to be opened in order
     * for the game to be finished. If this happens, then the player wins and
     * we call the FinishedPopup() class.
     * 
     * @param stage
     * @param layout both of them are passed in the FinishedPopup() class
     *               if the game is finished (player has won or lost)
     */
    public void open(Stage stage, BorderPane layout) {
        if (mine) {
            if (!hidden)
                return;
            if (marked) {
                game.flags--;
                game.numberflags.setText(String.valueOf(game.flags));
                flagimageView.setVisible(false);
                marked = false;
            }
            game.start = false;
            hidden = false;
            if (hypermine)
                hypermineimageView.setVisible(!hidden);
            else
                mineimageView.setVisible(!hidden);
            rect.setFill(Color.GRAY);

            System.out.println("Game Over");

            for (int row = 0; row < game.rows; row++) {
                for (int col = 0; col < game.columns; col++) {
                    Tile tile = game.grid[row][col];
                    if (tile.mine) {
                        tile.rect.setFill(Color.GRAY);
                        tile.hidden = false;
                        tile.mineimageView.setVisible(true);
                        tile.hypermineimageView.setVisible(true);
                        tile.flagimageView.setVisible(false);
                    }

                }
            }
            game.saveStatsToFile(game.mines, game.tries, game.maxtime, "Computer won (Player clicked a mine)",
                    game.timer.timeRemaining);
            Text message = new Text();
            message.setText("You clicked a box with a mine.");
            game.timer.stop();
            game.alarmcolor();
            FinishedPopup cpopup = new FinishedPopup(game, stage, scene, layout, message, game.columns, game.rows,
                    game.mines, game.maxtime, game.hyper ? 1 : 0, 0);
            cpopup.setTitle("Game Over");
            cpopup.showAndWait();
        }
        if (hidden) {
            hidden = false;
            if (marked) {
                game.flags--;
                game.numberflags.setText(String.valueOf(game.flags));
            }
            flagimageView.setVisible(false);
            text.setVisible(true);
            game.open++;
            rect.setFill(Color.GRAY);
            if (text.getText().isEmpty()) {
                for (Tile t : tilesClose) {
                    if (t != null) {
                        t.open(stage, layout);
                    }
                }
            }
            int finish = game.rows * game.columns - game.mines;
            // System.out.println(game.open+" "+finish);

            if (game.open == finish) {
                int timeleft = game.timer.timeRemaining;
                game.start = false;
                game.timer.stop();
                System.out.println("Player won");
                game.saveStatsToFile(game.mines, game.tries, game.maxtime, "Player won", timeleft);
                Text message = new Text();
                message.setText("You clicked all the tiles without a mine!!!");
                FinishedPopup cpopup = new FinishedPopup(game, stage, scene, layout, message, game.columns, game.rows,
                        game.mines, game.maxtime, game.hyper ? 1 : 0, 1);
                cpopup.setTitle("Player won!");
                cpopup.showAndWait();
            }
        }
    }

    /**
     * The mark() method is called when the tile is right-clicked. If the
     * tile isn't hidden (is opened) then it returns, because the specific
     * tile can't be flagged. If the tile is not yet marked, first we check
     * if the maximum number of flags allowed has been reached. If it has
     * then we call the alert() method to let the user know it. If it hasn't
     * then we check if the tries are <= 3 and the tile is a hypermine. If
     * both conditions are met then we open the tile and also all the tiles
     * in the same column and row as it, using the tiles_hypermine list. For
     * every tile we open we change the flags, maxflags and open counters of
     * the game depending onif the tile was marked or not, or if it was a mine
     * or not. Finally, if the conditions weren't met then the tile is marked
     * with a flag and the number of flags is incremented. If the tile is
     * already marked, the flag is removed and the number of flags is
     * decremented.
     */
    public void mark() {
        if (hidden == false)
            return;
        if (!marked) {
            if (game.flags >= game.maxflags) { // If the maximum number of flags has been reached show alert
                alert();
                game.start = true;
                return;
            } else if (hypermine && game.tries <= 3) { // if it's a hypermine and the tries are <= 3
                hypermineimageView.setVisible(true);
                game.maxflags++;
                for (Tile t : tiles_hypermine) {
                    if (t != null) {
                        t.hidden = false;
                        if (t.marked) {
                            game.flags--;
                            game.numberflags.setText(String.valueOf(game.flags));
                            t.flagimageView.setVisible(false);
                            t.marked = false;
                        }
                        if (t.mine) {
                            game.maxflags--;
                            t.mineimageView.setVisible(true);
                        } else {
                            game.open++;
                            t.text.setVisible(true);
                        }
                        t.rect.setFill(Color.GRAY);
                    }
                }
            } else {
                game.flags++;
                game.numberflags.setText(String.valueOf(game.flags));
                marked = true;
                flagimageView.setVisible(true);
            }
        } else {
            game.flags--;
            game.numberflags.setText(String.valueOf(game.flags));
            marked = false;
            flagimageView.setVisible(false);
        }
    }

    private void alert() {
        game.start = false;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Max Flags");
        alert.setHeaderText("Maximum numbers of flags reached!");
        alert.setContentText("Unflag a tile first to flag another one");
        alert.showAndWait();
    }
}
