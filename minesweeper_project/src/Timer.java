import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class Timer {
    private Game game;
    private Label timerLabel;
    private Button startButton;
    private Button stopButton;
    private Timeline timeline;
    public int timeRemaining;
    
    public Timer(Game game, Stage stage, BorderPane layout, int maxtime) {
        this.game = game;
        timeRemaining = maxtime;
    }

    public HBox Time() {
        
        //System.out.println(timeRemaining);

        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;

        timerLabel = new Label();
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setFont(Font.font((18)));
        startButton = new Button("Start");
        stopButton = new Button("Stop");
        startButton.getStyleClass().add("round-blue");
        stopButton.getStyleClass().add("round-red");

        //stopButton.setStyle("-fx-background-color: #F95B5B; -fx-text-fill: #ffffff; -fx-font-size: 14px;");
        
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!game.start) return;
                timeRemaining--;
                //System.out.println(timeRemaining);
                updateTimerLabel();
                if (timeRemaining == 0) {
                    game.timerunout();
                }
            }
        }));

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                start();
            }
        });

        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stop();
            }
        });

        VBox box2 = new VBox();
        box2.getChildren().add(box);
        VBox.setMargin(box, new Insets(0, 0, 20, 0));

        box.getChildren().addAll(timerLabel, startButton, stopButton);
        HBox.setMargin((Node)timerLabel, new Insets(5, 0, 5, 15));
        HBox.setMargin((Node)stopButton, new Insets(5, 15, 5, 0));
        return box;
    } 

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void start() {
        if(!game.canstart) return;
        game.start = true;
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        game.start = false;
        timeline.stop();
        //timeRemaining = 60; // Reset time to initial value
        updateTimerLabel();
    }
}
