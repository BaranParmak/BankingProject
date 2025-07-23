package client.ui.dialogs;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.Optional;
import java.util.function.Consumer;

public class AlertDialog {

    public static void showInfoDialog(String title, String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle(title);

        // Main VBox container
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(30, 40, 30, 40));
        layout.setStyle("-fx-background-color: #181818; -fx-border-color: #333; -fx-border-width: 1px;");
        layout.setMinWidth(500);
        layout.setMaxWidth(500);

        // Siemens logo (for the top section)
        Label logoLabel = new Label("SIEMENS");
        logoLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00ffff;");
        HBox logoBox = new HBox(logoLabel);
        logoBox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ccff00;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new javafx.geometry.Insets(20, 0, 20, 0));

        // Content text
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-wrap-text: true;");
        messageLabel.setMaxWidth(420);
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        VBox messageBox = new VBox(messageLabel);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new javafx.geometry.Insets(10, 0, 30, 0));

        // OK button
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> dialogStage.close());
        closeButton.setStyle(
                "-fx-background-color: #ccff00; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 30px; " +
                        "-fx-padding: 10px 30px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-cursor: hand;"
        );
        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Create main layout
        layout.getChildren().addAll(logoBox, titleBox, messageBox, buttonBox);

        Scene scene = new Scene(layout);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    public static boolean showConfirmDialog(String title, String message) {
        final boolean[] result = {false};

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle(title);

        // Main VBox container
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(30, 40, 30, 40));
        layout.setStyle("-fx-background-color: #181818; -fx-border-color: #333; -fx-border-width: 1px;");
        layout.setMinWidth(500);
        layout.setMaxWidth(500);

        // Siemens logo (for the top section)
        Label logoLabel = new Label("SIEMENS");
        logoLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00ffff;");
        HBox logoBox = new HBox(logoLabel);
        logoBox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ccff00;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new javafx.geometry.Insets(20, 0, 20, 0));

        // Content text
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-wrap-text: true;");
        messageLabel.setMaxWidth(420);
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        VBox messageBox = new VBox(messageLabel);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new javafx.geometry.Insets(10, 0, 30, 0));

        // Buttons
        Button yesButton = new Button("Yes");
        yesButton.setOnAction(e -> {
            result[0] = true;
            dialogStage.close();
        });
        yesButton.setStyle(
                "-fx-background-color: #ccff00; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 30px; " +
                        "-fx-padding: 10px 30px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-cursor: hand;"
        );

        Button noButton = new Button("No");
        noButton.setOnAction(e -> {
            result[0] = false;
            dialogStage.close();
        });
        noButton.setStyle(
                "-fx-background-color: #555555; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 30px; " +
                        "-fx-padding: 10px 30px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-cursor: hand;"
        );

        HBox buttonBox = new HBox(30, noButton, yesButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Create main layout
        layout.getChildren().addAll(logoBox, titleBox, messageBox, buttonBox);

        Scene scene = new Scene(layout);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        return result[0];
    }

    public static Optional<String> showInputDialog(String title, String message, String promptText) {
        final String[] userInput = {null};

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle(title);

        // Main VBox container
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(30, 40, 30, 40));
        layout.setStyle("-fx-background-color: #181818; -fx-border-color: #333; -fx-border-width: 1px;");
        layout.setMinWidth(500);
        layout.setMaxWidth(500);

        // Siemens logo (for the top section)
        Label logoLabel = new Label("SIEMENS");
        logoLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00ffff;");
        HBox logoBox = new HBox(logoLabel);
        logoBox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ccff00;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new javafx.geometry.Insets(20, 0, 20, 0));

        // Content text
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-wrap-text: true;");
        messageLabel.setMaxWidth(420);
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        VBox messageBox = new VBox(messageLabel);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new javafx.geometry.Insets(10, 0, 20, 0));

        // Input field
        TextField inputField = new TextField();
        inputField.setPromptText(promptText);
        inputField.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1); " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: rgba(255,255,255,0.5); " +
                        "-fx-padding: 10px; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-max-width: 400px;"
        );
        HBox inputBox = new HBox(inputField);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new javafx.geometry.Insets(0, 0, 20, 0));

        // Buttons
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            userInput[0] = inputField.getText();
            dialogStage.close();
        });
        okButton.setStyle(
                "-fx-background-color: #ccff00; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 30px; " +
                        "-fx-padding: 10px 30px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-cursor: hand;"
        );

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            userInput[0] = null;
            dialogStage.close();
        });
        cancelButton.setStyle(
                "-fx-background-color: #555555; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 30px; " +
                        "-fx-padding: 10px 30px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-cursor: hand;"
        );

        HBox buttonBox = new HBox(30, cancelButton, okButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Create main layout
        layout.getChildren().addAll(logoBox, titleBox, messageBox, inputBox, buttonBox);

        Scene scene = new Scene(layout);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        return userInput[0] != null ? Optional.of(userInput[0]) : Optional.empty();
    }
}