package atbmhttt.atbmcq_16.helpers;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class BorderPaneHelper {
    public static void setAllSections(
            BorderPane contentArea,
            Node leftNode, Node topNode,
            Node rightNode, Node bottomNode, Node centerNode) {
        contentArea.setLeft(leftNode);
        contentArea.setTop(topNode);
        contentArea.setRight(rightNode);
        contentArea.setBottom(bottomNode);
        contentArea.setCenter(centerNode);
    }
}
