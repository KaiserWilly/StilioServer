package fx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.Node;
import network.Query;

/**
 * Created by JD Isenhart on 11/19/2016.
 * Testing RMI creation in Java 8
 */
public class FXMenu extends Application {
    private static Stage main;

    public static void run(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        main = new Stage();
        main.setTitle("Stilio: Server Distribution");
        main.getIcons().add(new Image(FXMenu.class.getClassLoader().getResourceAsStream("rsc/StilioServerIcon-01.png")));
        main.setScene(menuScene());
        main.setMinWidth(325);
        main.setMinHeight(500);
        main.show();
    }

    private static Scene menuScene() {
        ChoiceBox cb = new ChoiceBox<>(FXCollections.observableArrayList("QueryServer Server", new Separator(), "Resource Node", "Client Node"));

        VBox base = new VBox();
        base.setPadding(new Insets(8));
        base.setSpacing(10);
        base.setPrefSize(300, 500);
        base.setAlignment(Pos.TOP_CENTER);

        VBox labels = new VBox(), textF = new VBox();
        labels.setAlignment(Pos.CENTER_RIGHT);
        labels.setSpacing(10);
        textF.setAlignment(Pos.CENTER_LEFT);
        textF.setSpacing(10);

        Label logo = new Label();
        ImageView logoImage = new ImageView(new Image(FXMenu.class.getClassLoader().getResourceAsStream("rsc/StilioServerLogo-01.png")));
        logoImage.setPreserveRatio(true);
        logoImage.setFitWidth(300);
        logo.setGraphic(logoImage);
        logo.setPrefSize(300, 150);

        Label cbLabel = new Label("Server Type:");
        cbLabel.setAlignment(Pos.CENTER);

        Label pLabel = new Label("Port:");
        pLabel.setPrefHeight(25);
        pLabel.setAlignment(Pos.CENTER_RIGHT);
        TextField pText = new TextField("1180");
        pText.setPrefSize(100, 25);


        Label ipLabel = new Label("QueryServer IP:");
        ipLabel.setPrefHeight(25);
        ipLabel.setAlignment(Pos.CENTER_RIGHT);
        TextField ipText = new TextField("0.0.0.0");
        ipText.setPrefSize(100, 25);

        Label passLabel = new Label("Password:");
        passLabel.setPrefHeight(25);
        passLabel.setAlignment(Pos.CENTER);
        PasswordField passText = new PasswordField();
        passText.setPrefSize(100, 25);

        ImageView launchImage = new ImageView(new Image(FXMenu.class.getClassLoader().getResourceAsStream("rsc/StilioServerLaunch-01.png")));
        launchImage.setPreserveRatio(true);
        launchImage.setFitHeight(20);
        Button launch = new Button("Launch", launchImage);
        launch.setContentDisplay(ContentDisplay.RIGHT);
        launch.setPrefSize(100, 25);
        launch.setOnAction(event -> {
            switch (String.valueOf(cb.getSelectionModel().getSelectedItem())) {
                case "Query Server":
                    Query server = new Query();
                    server.startQuery(Integer.parseInt(pText.getText()));
                    break;
                case "Resource Node":
                case "Client Node":
                    new Node(ipText.getText(), Integer.parseInt(pText.getText()));
                    break;
            }
            main.close();
        });

        cb.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            base.getChildren().clear();
            base.getChildren().addAll(logo, cbLabel, cb);
            labels.getChildren().clear();
            textF.getChildren().clear();
            switch (Integer.parseInt(String.valueOf(newValue))) {
                case 3: // Client Node
                case 2: // Resource Node
                    labels.getChildren().addAll(ipLabel, pLabel, passLabel);
                    textF.getChildren().addAll(ipText, pText, passText);
                    break;
                case 0: // QueryServer Server
                    labels.getChildren().add(pLabel);
                    textF.getChildren().add(pText);
                    break;
            }
            HBox vbox = new HBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.getChildren().addAll(labels, textF);
            base.getChildren().addAll(vbox, launch);
        });
        cb.getSelectionModel().select(0);
        cb.setPrefSize(150, 25);

        Scene scene = new Scene(base);
        scene.getStylesheets().add("rsc/menuScene.css");
        return scene;
    }
}
