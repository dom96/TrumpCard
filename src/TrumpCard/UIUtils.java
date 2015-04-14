package TrumpCard;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;

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
}
