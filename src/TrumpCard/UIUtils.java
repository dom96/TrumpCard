package TrumpCard;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import javax.swing.*;


public class UIUtils {
    /**
     * Adds Control `c` to `group` at the specified x and y coordinates.
     */
    public static void addAt(Group group, Node c, double x, double y)
    {
        Pane pane = new Pane();
        pane.setLayoutX(x);
        pane.setLayoutY(y);
        pane.setPrefWidth(group.getScene().getWidth());
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.getChildren().add(c);
        group.getChildren().add(pane);
    }

    public static void showErrorDialog(String message, String title)
    {
        // Display an error dialog using Swing as JavaFX doesn't support
        // message dialogs. At least not yet (http://code.makery.ch/blog/javafx-dialogs-official/)

        JFrame frame = new JFrame(title);
        JOptionPane.showMessageDialog(frame, message,
                title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showMessageDialog(String message)
    {
        JFrame frame = new JFrame("Message");
        JOptionPane.showMessageDialog(frame, message,
                "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorLabel(Label errorLabel, String message)
    {
        errorLabel.setVisible(true);
        errorLabel.setText(message);
        FadeTransition fade = new FadeTransition(Duration.millis(600), errorLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.4);
        fade.setCycleCount(10);
        fade.setAutoReverse(true);
        fade.play();
        fade.setOnFinished(event -> errorLabel.setVisible(false));
    }

    public static Button createButton(String text, Font font, String cssClass)
    {
        Button result = new Button();
        result.setText(text);
        result.getStyleClass().add(cssClass);
        result.setFont(font);
        // Set some padding on the buttons, and set their width to 200px.
        result.setPadding(new Insets(5, 10, 5, 10));
        result.setPrefWidth(200);
        // Make sure all buttons are the same size.
        result.setMaxWidth(Double.MAX_VALUE);
        return result;
    }

    public static void createBoldLabel(Pane box, String text, Node right, Font font)
    {
        Label boldLbl = new Label(text);
        boldLbl.setFont(font);
        boldLbl.getStyleClass().add("statusFieldName");

        box.getChildren().addAll(boldLbl, right);
    }
}
