import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.animation.*;

public class Game {
    public int columns, rows; 
    public Tile[][] grid; 
    private Pane pane;
    int mines,flags=0,maxflags,maxtime,size,first;
    public int open = 0,tries=0;
    public boolean hyper;
    public boolean start=false,canstart=true;
    Text numberflags = new Text(), numbertries = new Text();
    Timer timer; 
    int[][] array;
    Stage stage;
    BorderPane layout;
    Random random = new Random();
    GridPane gridPane = new GridPane();
    Scene scene;

    Image flag = new Image(getClass().getResourceAsStream("./images/flag.png"));
    ImageView flagimageView = new ImageView(flag);
    Image mineicon = new Image(getClass().getResourceAsStream("./images/mine.png"));
    ImageView mineimageView = new ImageView(mineicon);
    Image hypermineicon = new Image(getClass().getResourceAsStream("./images/hypermine.png"));
    ImageView hypermineimageView = new ImageView(hypermineicon);
    Image triesicon = new Image(getClass().getResourceAsStream("./images/tries.png"));
    ImageView triesimageView = new ImageView(triesicon);

    public Game(Stage stage,Scene scene, BorderPane layout, int g_col, int g_rows, int g_mines, int g_maxtime, int g_hyper) {
        // Initialize
        this.stage = stage;
        this.layout = layout;
        this.scene = scene;
        rows = g_rows;
        columns = g_col;
        mines = g_mines;
        maxtime = g_maxtime;
        if(g_hyper==1) hyper = true; else hyper = false;
        grid = new Tile[rows][columns];
        size = g_col==9 ? 70 : 40;
        maxflags = mines;
        first = 0;
        array = new int[rows][columns];
        gridPane.setPrefSize(640, 640);
        gridPane.setAlignment(Pos.TOP_LEFT);
    }

    public void initialize() {
        gridPane.getChildren().clear();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                array[i][j] = 0;
            }
        }
        first = 0;
        int min = 0, randomNumber_row, randomNumber_col,help=mines;

        // find which tile will be the hypermine first if we need one
        if(hyper){
            randomNumber_row = random.nextInt(rows - min) + min;
            randomNumber_col = random.nextInt(columns - min) + min;
            System.out.println("Hypermine position:");
            System.out.println(randomNumber_col+" "+randomNumber_row); // the rows and columns are opposite
            minestxt(randomNumber_col,randomNumber_row,1);
            array[randomNumber_row][randomNumber_col] = 2;
            help = help - 1; 
        }
        
        //System.out.println("Rest of mines positions:");
        for (int i = 0; i < help; i++) {
            randomNumber_row = random.nextInt(rows - min) + min;
            randomNumber_col = random.nextInt(columns - min) + min;
            if(array[randomNumber_row][randomNumber_col] == 0) {
                //System.out.println(randomNumber_row+" "+randomNumber_col+" "+ i);
                minestxt(randomNumber_col,randomNumber_row,0);
                array[randomNumber_row][randomNumber_col] = 1;
            }
            else {
                //System.out.println("Here"+randomNumber_row+" "+randomNumber_col+" "+ i); 
                help = help + 1;
            }
        }

        if(hyper) help++;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                boolean hasmine = array[row][col]==1 ? true : false;
                boolean hashypermine=false;
                if(array[row][col]==2) {
                    hasmine = true;
                    hashypermine = true;
                }
                Tile tile = new Tile(col,row,hasmine,hashypermine,this,stage,scene,layout,size);
                grid[row][col] = tile;
                gridPane.getChildren().add(tile);
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Tile tile = grid[row][col];
                List<Tile> tilesclose = closeTiles(tile);
                tile.tilesClose = tilesclose;
                if(tile.hypermine) {
                    List<Tile> tileshyper = forhypermine(tile);
                    tile.tiles_hypermine = tileshyper;
                    continue;
                }
                if(tile.mine) continue;
                long mines = tilesclose.stream().filter(t -> t.mine).count();
                if(mines>0) tile.text.setText(String.valueOf(mines));               
            }
        }
        
        // Set the info for the game at the top
        timer = new Timer(this,stage,layout,maxtime);
        HBox timerbox = timer.Time();

        //number of mines
        mineimageView.setFitWidth(25);
        mineimageView.setFitHeight(25);
        Text numbermines = new Text();
        numbermines.setText(hyper ? String.valueOf(mines-1) : String.valueOf(mines));
        numbermines.setFill(Color.WHITE);
        numbermines.setFont(Font.font((18)));

        //number of hypermines
        hypermineimageView.setFitWidth(27);
        hypermineimageView.setFitHeight(27);
        Text numberhypermines = new Text();
        numberhypermines.setText(hyper? "1" : "0");
        numberhypermines.setFill(Color.WHITE);
        numberhypermines.setFont(Font.font((18)));

        //number of flags 
        flagimageView.setFitWidth(22);
        flagimageView.setFitHeight(22);
        numberflags.setText(String.valueOf(flags));
        numberflags.setFill(Color.WHITE);
        numberflags.setFont(Font.font((18)));

        //number of tries 
        triesimageView.setFitWidth(22);
        triesimageView.setFitHeight(22);
        numbertries.setText(String.valueOf(tries));
        numbertries.setFill(Color.WHITE);
        numbertries.setFont(Font.font((18)));

        HBox top = new HBox();
        HBox stats = new HBox();
        stats.getChildren().addAll(mineimageView, numbermines, hypermineimageView, numberhypermines, flagimageView, numberflags, triesimageView, numbertries);
        top.getChildren().addAll(stats, timerbox);
        top.setAlignment(Pos.CENTER_LEFT);

        Stop[] stops = new Stop[] { new Stop(0, Color.web("#1E0043")), new Stop(1, Color.web("#00C2FF")) };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null, stops);
        BackgroundFill background_fill = new BackgroundFill(gradient, new CornerRadii(15), Insets.EMPTY);
        Background background = new Background(background_fill);
        stats.setBackground(background);
        timerbox.setBackground(background);

        HBox.setMargin((Node)mineimageView, new Insets(5, 0, 5, 15));
        HBox.setMargin((Node)numbermines, new Insets(5, 20, 5, 5));
        HBox.setMargin((Node)hypermineimageView, new Insets(5, 0, 5, 0));
        HBox.setMargin((Node)numberhypermines, new Insets(5, 20, 5, 5));
        HBox.setMargin((Node)flagimageView, new Insets(5, 0, 5, 0));
        HBox.setMargin((Node)numberflags, new Insets(5, 20, 5, 5));
        HBox.setMargin((Node)triesimageView, new Insets(5, 0, 5, 0));
        HBox.setMargin((Node)numbertries, new Insets(5, 15, 5, 5));

        HBox.setMargin((Node)stats, new Insets(0, 45, 0, 85));
        HBox.setMargin((Node)timerbox, new Insets(0, 0, 0, 120));

        // Add the top and the gridPane
        VBox box2 = new VBox();
        box2.getChildren().add(gridPane);

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(top,box2);

        VBox.setMargin((Node)top, new Insets(0, 0, 20, 0));
        VBox.setMargin((Node)box2, new Insets(0, 0, 0, 80));
        pane = box;
    }

    public List<Tile> closeTiles(Tile tile) {
        List<Tile> close = new ArrayList<>();

        int[] points = new int[] {
            -1,-1,
            -1, 0,
            -1, 1,
             0,-1,
             0, 0,
             0, 1,
             1,-1,
             1, 0,
             1, 1,
        };

        for(int i = 0; i < points.length; i++) {
            int dx = points[i];
            int dy = points[++i];

            int newX = tile.row + dx;
            int newY = tile.col + dy;

            if(newX >=0 && newX < columns && newY >=0 && newY < columns ) {
                close.add(grid[newX][newY]);
            }
        }
        return close;
    }

    public List<Tile> forhypermine(Tile tile) {
        List<Tile> hypermine_tiles = new ArrayList<>();
        //System.out.println("For hypermine:");
        for(int i = 0; i < columns; i++) {
            int newX = i;
            int newY = tile.col;
            //System.out.println(newX+" "+newY);
            if(newX >=0 && newX < columns && newY >=0 && newY < columns ) {
                hypermine_tiles.add(grid[newX][newY]);
            }
        }
        for(int i = 0; i < rows; i++) {
            int newX = tile.row;
            int newY = i;
            //System.out.println(newX+" "+newY);
            if(newX >=0 && newX < columns && newY >=0 && newY < columns ) {
                hypermine_tiles.add(grid[newX][newY]);
            }
        }

        return hypermine_tiles;
    }

    public void starttimer(){
        timer.start();
    }

    public void timerunout() {
        start = false;
        timer.stop();
        System.out.println("Game Over");
        saveStatsToFile(mines, tries, maxtime, "Computer won (Player run out of time)",0);
        Text message = new Text();
        message.setText("Time run out.");
        FinishedPopup cpopup = new FinishedPopup(this,stage,scene,layout,message,columns,rows,mines,maxtime,hyper?1:0,0);
        cpopup.setTitle("Game Over");
        cpopup.show();
    }

    public void solution() {
        start = false;
        canstart = false;
        int timeleft = timer.timeRemaining;
        timer.stop();
        System.out.println("Game Over");
        saveStatsToFile(mines, tries, maxtime, "Computer won (Player asked for solution)",timeleft);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Tile tile = grid[row][col];
                if(tile.mine) {
                    tile.rect.setFill(Color.GRAY);
                    tile.hidden = false;
                    tile.mineimageView.setVisible(true);
                    tile.hypermineimageView.setVisible(true);
                }
                            
            }
        }
    }

    // function to save the stats of a game to previoys_games.txt for when a game is finished
    public void saveStatsToFile(int mines, int tries, int time, String winner,int timeleft) {
        String filePath = "./src/medialab/previous_games.txt";
        try {
            //System.out.println(filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            
            List<String> lines = Files.readAllLines(file.toPath());

            String smines = String.valueOf(mines);
            String stries = String.valueOf(tries);
            String stime = String.valueOf(time);
            String stimeused = String.valueOf(time-timeleft);
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(smines + "\n" + stries + "\n" + stimeused + "/" + stime + "\n" + winner);
            bw.newLine();
            bw.close();

            FileWriter fw2 = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            for (String line : lines) {
                bw2.write(line);
                bw2.newLine();
            }
            bw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // function to add the mine positions on minetxt
    public void minestxt(int row, int col, int hypermine) {
        String filePath = "./src/medialab/mines.txt";
        try {
            //System.out.println(filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            
            String srow = String.valueOf(row);
            String scol = String.valueOf(col);
            String shypermine = String.valueOf(hypermine);
            if(first == 1) {
                FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(srow + " " + scol + " " + shypermine);
                bw.newLine();
                bw.close();
            } else {
                first = 1;
                FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(srow + " " + scol + " " + shypermine);
                bw.newLine();
                bw.close();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void alarmcolor() {
        Color bgColor = (Color) ((Region) scene.getRoot()).getBackground().getFills().get(0).getFill();
        
        // Create a timeline to change the background color to red with opacity
        Timeline timeline1 = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(((Region) scene.getRoot()).backgroundProperty(),
                        new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(Color.rgb(255, 0, 0, 0.5), null, null)))),
                new KeyFrame(Duration.seconds(0.4), new KeyValue(((Region) scene.getRoot()).backgroundProperty(),
                        ((Region) scene.getRoot()).getBackground())),
                new KeyFrame(Duration.seconds(0.8), new KeyValue(((Region) scene.getRoot()).backgroundProperty(),
                        new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(Color.rgb(255, 0, 0, 0.5), null, null)))),
                new KeyFrame(Duration.seconds(1.2), new KeyValue(((Region) scene.getRoot()).backgroundProperty(),
                        new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(bgColor, null, null))))
                /*new KeyFrame(Duration.seconds(1.6), new KeyValue(((Region) scene.getRoot()).backgroundProperty(),
                        new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(Color.rgb(255, 0, 0, 0.5), null, null)))),
                new KeyFrame(Duration.seconds(2), new KeyValue(((Region) scene.getRoot()).backgroundProperty(),
                        new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(bgColor, null, null))))*/
        );
        timeline1.play();
    }

    public Pane getPane() {
        initialize();
        if(pane==null) System.out.println("null");
        return pane;
    }
    
}
