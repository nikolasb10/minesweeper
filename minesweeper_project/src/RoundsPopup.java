import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

/**
* The RoundsPopup class represents a custom popup window displaying statistics
* for 5 previous rounds. It extends the Stage class and is initialized with a
* reference to the main application window.
*/

public class RoundsPopup extends Stage {
    private VBox[] prev_games = new VBox[5];

    int counter = 0;

    /**
     * Constructs a new RoundsPopup object with a given owner stage.
     * The popup window is initialized as a modal window with the
     * specified owner stage.
     *
     * @param owner the owner stage of the popup window.
     */

    public RoundsPopup(Stage owner) {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);

        VBox pop = new VBox(10);

        Label label = new Label("Stats for 5 previous rounds");
        label.setTextFill(Color.web("#1E0043"));
        label.setFont(Font.font((24)));

        VBox statsContainer = new VBox();
        statsContainer.setAlignment(Pos.CENTER);
        statsContainer.getChildren().addAll(label);

        // Create 5 VBox elements to display stats for each of the previous 5 rounds.
        for(int i = 0; i < 5; i++) {
            prev_games[i] = new VBox();
            prev_games[i].setAlignment(Pos.CENTER);
            Stop[] stops = new Stop[] { new Stop(0, Color.web("#1E0043")), new Stop(1, Color.web("#00C2FF")) };
            LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null, stops);
            BackgroundFill background_fill = new BackgroundFill(gradient, new CornerRadii(15), Insets.EMPTY);
            Background background = new Background(background_fill);
            prev_games[i].setBackground(background);
            VBox.setMargin(prev_games[i], new Insets(5, 15, 10, 15));
        }

        readPrevGames();

        // Add the 5 VBox elements to the statsContainer VBox.
        for(int i = 0; i < 5; i++) {
            statsContainer.getChildren().add(prev_games[i]);
        }
        
        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(closeEvent -> close());
        closeButton.getStyleClass().add("round-blue");
        closeButtonContainer.getChildren().addAll(closeButton);

        pop.getChildren().addAll(statsContainer, closeButtonContainer);

        VBox.setMargin(label, new Insets(10, 10, 10, 10));
        
        HBox.setMargin(closeButton, new Insets(0, 10, 0, 0));

        Scene scene = new Scene(pop, 450, 670);
        setScene(scene);
        scene.getRoot().setStyle("-fx-background-color: #A3BDE4;");
        scene.getStylesheets().add("./style.css");
        scene.setRoot(pop);
    }

    
    /**
     * Reads the statistics for the previous rounds from "previous_games.txt" file
     * and populates the 5 VBox elements with this information.
     */
    public void readPrevGames() {
        String filePath = "./src/medialab/previous_games.txt";
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            List<String> lines = Files.readAllLines(file.toPath());

            counter = lines.size() / 4;
            int imax = counter<5 ? counter : 5;
            for(int i=0; i < imax; i++) {
                Text numbermines = new Text();
                Text numbertries = new Text();
                Text time = new Text();
                Text winner = new Text();
                numbermines.setText("Number of mines: "+lines.get(4*i));
                numbermines.setFill(Color.WHITE);
                numbermines.setFont(Font.font((18)));
                numbertries.setText("Number of tries: "+lines.get(4*i+1));
                numbertries.setFill(Color.WHITE);
                numbertries.setFont(Font.font((18)));
                time.setText("Time played: "+lines.get(4*i+2)+" seconds");
                time.setFill(Color.WHITE);
                time.setFont(Font.font((18)));
                winner.setText("Winner: "+lines.get(4*i+3));
                winner.setFill(Color.WHITE);
                winner.setFont(Font.font((18)));
                prev_games[i].getChildren().addAll(numbermines,numbertries,time,winner);
            }
            
            System.err.println(counter);
            scanner.close();
         
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
