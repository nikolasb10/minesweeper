import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    public boolean start1=false;
    // characteristics of the game that is loaded
    public int columns = 9, rows = 9, mines = 10, difficulty = 1, maxtime = 150, hyper=0;

    public void start(Stage stage) throws IOException {

        BorderPane layout = new BorderPane();
        MenuBar menuBar = new MenuBar();
        Menu application = new Menu("Application");
        Menu details = new Menu("Details");
        layout.setTop(menuBar);
        Scene scene = new Scene(layout, 800, 780);

        // Game area
        Game game = new Game(stage,scene,layout,columns,rows,mines,maxtime,hyper);
        layout.setCenter(game.getPane());

        menuBar.getMenus().addAll(application,details);
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, 
            CycleMethod.NO_CYCLE, new Stop[] {
                new Stop(0, Color.web("#00C2FF")),
                new Stop(1, Color.web("#1E0043"))
            });

        // Set the background fill of the menu bar to the gradient
        menuBar.setBackground(new Background(new BackgroundFill(gradient, null, null)));

        // Application Menu items
        MenuItem create = new MenuItem("Create");
        MenuItem load = new MenuItem("Load");
        MenuItem start = new MenuItem("Start");
        MenuItem exit = new MenuItem("Exit");

        create.setOnAction(event -> {
            CreatePopup cpopup = new CreatePopup(stage);
            cpopup.setTitle("Create a new scenario");
            cpopup.setMaxHeight(340);
            cpopup.setMaxWidth(410);
            cpopup.showAndWait();

        });
        load.setOnAction(event -> {
            LoadPopup lpopup = new LoadPopup(stage,layout,this,game);
            lpopup.setTitle("Load a scenario");
            lpopup.showAndWait();
            
        });
        start.setOnAction(event -> {
            game.starttimer();
        });
        exit.setOnAction(event -> {
            System.exit(0);
        });

        // Details Menu items
        MenuItem rounds = new MenuItem("Rounds");
        MenuItem solution = new MenuItem("Solution");
        
        rounds.setOnAction(event -> {
            RoundsPopup rpopup = new RoundsPopup(stage);
            rpopup.setTitle("View Previous Rounds");
            rpopup.setMaxHeight(690);
            rpopup.setMinHeight(690);
            rpopup.setMaxWidth(470);
            rpopup.showAndWait();
        });
        solution.setOnAction(event -> {
            game.solution();
        });

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        application.getItems().addAll(create, load, start, separatorMenuItem, exit);
        details.getItems().addAll(rounds,solution);


        scene.getRoot().setStyle("-fx-background-color: #A3BDE4;");
        scene.getStylesheets().add("./style.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("MediaLab Minesweeper");
        stage.show();
    }

 public static void main(String[] args) {
        launch(args);
    }
}