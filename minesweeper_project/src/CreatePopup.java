import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.*;

public class CreatePopup extends Stage {
    public CreatePopup(Stage owner) {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        VBox pop = new VBox(10);
        // layout.setAlignment(Pos.CENTER);
        Label label = new Label("Choose the data for your new SCENARIO");

        VBox textFieldContainer = new VBox();
        TextField scenario = new TextField();
        scenario.setPromptText("Enter your SCENARIO-ID here");
        scenario.setMaxWidth(240);

        TextField difficulty = new TextField();
        difficulty.setPromptText("Enter the difficulty here");
        difficulty.setMaxWidth(240);

        TextField amount = new TextField();
        amount.setPromptText("Enter the amount of mines here");
        amount.setMaxWidth(240);

        TextField hypermine = new TextField();
        hypermine.setPromptText("Enter if there will be a hypermine here");
        hypermine.setMaxWidth(240);

        TextField maxtime = new TextField();
        maxtime.setPromptText("Enter the maximum seconds available here");
        maxtime.setMaxWidth(240);

        textFieldContainer.setAlignment(Pos.CENTER);
        textFieldContainer.getChildren().addAll(label, scenario, difficulty, amount, hypermine, maxtime);

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        Button closeButton = new Button("Close");
        Button createButton = new Button("Create");

        createButton.setOnAction(closeEvent -> {
            saveTextToFile(scenario.getText(), difficulty.getText(), amount.getText(), hypermine.getText(), maxtime.getText());
        });
        closeButton.setOnAction(closeEvent -> close());
        closeButtonContainer.getChildren().addAll(closeButton, createButton);

        pop.getChildren().addAll(textFieldContainer, closeButtonContainer);

        VBox.setMargin(label, new Insets(10, 10, 10, 10));
        VBox.setMargin(scenario, new Insets(0, 10, 10, 10));
        VBox.setMargin(difficulty, new Insets(0, 10, 10, 10));
        VBox.setMargin(amount, new Insets(0, 10, 10, 10));
        VBox.setMargin(hypermine, new Insets(0, 10, 10, 10));
        VBox.setMargin(maxtime, new Insets(0, 10, 10, 10));
        HBox.setMargin(closeButton, new Insets(0, 10, 0, 0));

        Scene scene = new Scene(pop, 400, 300);
        setScene(scene);
        scene.getRoot().setStyle("-fx-background-color: #A3BDE4;");
        scene.setRoot(pop);
    }

    public void saveTextToFile(String scenario, String difficulty, String amount, String hypermine, String maxtime) {
        String filePath = "./src/medialab/" + scenario + ".txt";
        try {
            System.out.println(filePath);
            File file = new File(filePath);
            if (file.exists())  {
                alert("SCENARIO-ID","Wrong SCENARIO-ID","The name you selected for the SCENARIO-ID already exists!");
            } else {
                if(Integer.parseInt(hypermine)!=1 && Integer.parseInt(hypermine)!=0) alert("value for hypermine","Wrong hypermine value","The value of the hypermine must be 0 (no-hypermine) or 1 (hypermine)!");
                else {
                    file.createNewFile();
                    FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(difficulty + "\n" + amount + "\n" + maxtime + "\n" + hypermine);
                    bw.newLine();
                    bw.close();
                    close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            alert("hypermine value","Wrong hypermine value","The value of the hypermine must be 0 (no-hypermine) or 1 (hypermine)!");
        }
    }

    private void alert(String scenario, String message1, String message2){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(message1);
        alert.setHeaderText(message2);
        alert.setContentText("Please select a different "+scenario);
        alert.show();
    }
}

