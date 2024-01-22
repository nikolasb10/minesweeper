import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class FinishedPopup extends Stage {
    private Text message;
    public FinishedPopup(Game game,Stage owner, Scene scene1, BorderPane layout, Text message, int g_col, int g_rows, int g_mines, int g_maxtime, int g_hyper,int whowins) {
        this.message = message;
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        VBox pop = new VBox(10);
        // layout.setAlignment(Pos.CENTER);
        Label label = new Label(this.message.getText());
        label.setTextFill(Color.web("#1E0043"));
        label.setFont(Font.font((20)));
        
        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        Button restart = new Button("Restart");
        restart.getStyleClass().add("round-restart");


        if(whowins==1) scene1.getRoot().setStyle("-fx-background-color: #00F719;");

        restart.setOnAction(closeEvent -> {
            game.flags = 0;
            game.tries = 0;
            game.canstart = true;
            game.open=0;
            game.maxflags = g_mines;
            game.size = g_col==9 ? 70 : 40;
            Pane c = game.getPane();
            layout.setCenter(c);
            scene1.getRoot().setStyle("-fx-background-color: #A3BDE4;");
            close();
        });
        
        closeButtonContainer.getChildren().addAll(restart);

        pop.getChildren().addAll(label, closeButtonContainer);
        setOnCloseRequest(event -> event.consume());

        VBox.setMargin(label, new Insets(10, 10, 10, 10));
        HBox.setMargin(restart, new Insets(0, 0, 0, 0));

        Scene scene = new Scene(pop, 400, 100);
        setScene(scene);
        scene.getRoot().setStyle("-fx-background-color: #A3BDE4;");
        scene.getStylesheets().add("./style.css");
        scene.setRoot(pop);
    }
}
