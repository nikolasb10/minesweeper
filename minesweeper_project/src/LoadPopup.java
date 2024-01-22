import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.*;
import java.util.Scanner;

public class LoadPopup extends Stage {
    public int columns = 9, rows = 9, mines = 10, difficulty = 1, maxtime = 150, hyper=1;
    private Game game;
    private BorderPane layout;

    public LoadPopup(Stage owner, BorderPane layout, App app,Game game) {
        this.layout = layout;
        this.game = game;
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        VBox pop = new VBox(10);
        // layout.setAlignment(Pos.CENTER);
        Label label = new Label("Choose the SCENARIO to load");

        VBox textFieldContainer = new VBox();
        TextField scenario = new TextField();
        scenario.setPromptText("Enter your SCENARIO-ID here");
        scenario.setMaxWidth(200);

        textFieldContainer.setAlignment(Pos.CENTER);
        textFieldContainer.getChildren().addAll(label, scenario);

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        Button closeButton = new Button("Close");
        Button loadButton = new Button("Load");

        loadButton.setOnAction(closeEvent -> {
            readFile(scenario.getText());
            app.rows = rows;
            app.columns = columns;
            app.mines = mines;
            app.maxtime = maxtime;
            app.hyper = hyper;
            //Game next = new Game(owner,layout,columns,rows,mines,maxtime,hyper);
            close();
        });
        closeButton.setOnAction(closeEvent -> close());
        closeButtonContainer.getChildren().addAll(closeButton, loadButton);

        pop.getChildren().addAll(textFieldContainer, closeButtonContainer);

        VBox.setMargin(label, new Insets(10, 10, 10, 10));
        VBox.setMargin(scenario, new Insets(0, 10, 10, 10));
        HBox.setMargin(closeButton, new Insets(0, 10, 0, 0));

        Scene scene = new Scene(pop, 400, 150);
        setScene(scene);
        scene.getRoot().setStyle("-fx-background-color: #A3BDE4;");
        scene.setRoot(pop);
    }

    public void readFile(String scenario) {
        String filePath = "./src/medialab/" + scenario + ".txt";
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            String line1="", line2="", line3="", line4="";
            int counter = 0,emptylines=0;
            while (scanner.hasNextLine()) {
                if(counter==0) {
                    line1 = scanner.nextLine();
                    if(line1=="") emptylines++;
                }
                else if(counter==1) {
                    line2 = scanner.nextLine();
                    if(line2=="") emptylines++;
                }
                else if(counter==2) {
                    line3 = scanner.nextLine();
                    if(line3=="") emptylines++;
                }
                else if(counter==3) {
                    line4 = scanner.nextLine();
                    if(line4=="") emptylines++;
                }
                counter++;
            }
            //System.err.println(counter );
            scanner.close();
            // check if there were 4 lines written in the file, else throw exception
            if(emptylines != 0) throw new InvalidDescriptionException("The decription doesn't have 4 lines",file);
            
            // check if the values written on the file were correct, else throw exception
            difficulty = Integer.parseInt(line1);
            if(difficulty == 1) {
                    columns = 9;
                    rows = 9;
            } else if(difficulty == 2) {
                columns = 16;
                rows = 16;
            } else throw new InvalidValueException("The decription's difficulty has to be 1 or 2",file);
            
            mines = Integer.parseInt(line2);
            if((difficulty==1 && (mines < 9 || mines > 11)) || (difficulty==2 && (mines < 35 || mines > 45))) 
                throw new InvalidValueException("The decription's amount of mines aren't correct for difficulty "+ difficulty,file);

            maxtime = Integer.parseInt(line3);
            if((difficulty==1 && (maxtime < 120 || maxtime > 180)) || (difficulty==2 && (maxtime < 240 || maxtime > 360))) 
                throw new InvalidValueException("The decription's avalable time isn't correct for difficulty "+ difficulty,file);

            hyper = Integer.parseInt(line4);
            if((difficulty==1 && hyper != 0) || (difficulty==2 && (hyper < 0 || hyper > 1)))
                throw new InvalidValueException("The decription's amount of hypermines isn't correct for difficulty "+ difficulty,file);

            game.timer.stop();
            game.canstart = true;
            game.rows = rows;
            game.columns = columns;
            game.mines = mines;
            game.open = 0;
            game.maxtime = maxtime;
            game.flags = 0;
            game.tries = 0;
            if(hyper==1) game.hyper = true; else game.hyper = false;
            game.grid = new Tile[rows][columns];
            game.size = columns==9 ? 70 : 40;
            game.maxflags = mines;
            game.array = new int[rows][columns];
            layout.setCenter(game.getPane());

        } catch (FileNotFoundException e) {
            alert(scenario,"Scenario with SCENARIO-ID of '"+scenario+"' doesn't exist","Error SCENARIO-ID not found");
            System.out.println("File not found: " + e.getMessage());
        } catch (InvalidDescriptionException e) {
            System.err.println("Invalid description: " + e.getMessage());
            alert(scenario,"Scenario with SCENARIO-ID: '"+scenario+"' has an Invalid Description:\n"+e.getMessage(),"Invalid Description");      
        } catch (InvalidValueException e) {
            System.err.println("Invalid description: " + e.getMessage());
            alert(scenario,"Scenario with SCENARIO-ID: '"+scenario+"' has Invalid Values:\n"+e.getMessage(),"Invalid Values");
        } catch (NumberFormatException e) {
            System.err.println("Invalid description: " + e.getMessage());
            alert(scenario,"Scenario with SCENARIO-ID: '"+scenario+"' has Invalid Values:\nThe parameters of the Scenario must be integers.\n"+e.getMessage(),"Invalid Values");
        }
    }

    private void alert(String scenario, String message1, String message2){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(message2);
        alert.setHeaderText(message1);
        alert.setContentText("Please select a different SCENARIO-ID");
        alert.show();
    }
}
