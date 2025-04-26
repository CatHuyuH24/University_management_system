package atbmhttt.atbmcq_16.admin.Views;

import java.util.List;

import atbmhttt.atbmcq_16.admin.ViewModels.GrantPrivilegesViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GrantPrivilegesView {
    private GrantPrivilegesViewModel viewModel;

    public void displayPrivileges(String username) {
        viewModel = new GrantPrivilegesViewModel(username);
        Stage dialog = new Stage();
        dialog.setTitle("Grant Privilege");
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(18));

        Label typeLabel = new Label("Object Type:");
        javafx.scene.control.ComboBox<String> typeCombo = new javafx.scene.control.ComboBox<>();
        typeCombo.getItems().addAll("TABLE", "VIEW", "PROCEDURE", "FUNCTION");
        typeCombo.getSelectionModel().selectFirst();

        Label nameLabel = new Label("Object Name:");
        javafx.scene.control.ComboBox<String> nameCombo = new javafx.scene.control.ComboBox<>();
        nameCombo.setDisable(true);

        Label privLabel = new Label("Privilege:");
        javafx.scene.control.ComboBox<String> privCombo = new javafx.scene.control.ComboBox<>();
        privCombo.setDisable(true);

        Label grantOptionLabel = new Label("WITH GRANT OPTION:");
        javafx.scene.control.ComboBox<String> grantOptionCombo = new javafx.scene.control.ComboBox<>();
        grantOptionCombo.getItems().addAll("FALSE", "TRUE");
        grantOptionCombo.getSelectionModel().selectFirst();

        typeCombo.setOnAction(ev -> {
            String type = typeCombo.getValue();
            nameCombo.getItems().clear();
            privCombo.getItems().clear();
            if (type.equals("TABLE") || type.equals("VIEW")) {
                nameCombo.getItems().addAll(viewModel.getAllObjectNames(type));
                privCombo.getItems().addAll("SELECT", "UPDATE", "INSERT", "DELETE");
            } else if (type.equals("PROCEDURE") || type.equals("FUNCTION")) {
                nameCombo.getItems().addAll(viewModel.getAllObjectNames(type));
                privCombo.getItems().add("EXECUTE");
            }
            nameCombo.setDisable(false);
            privCombo.setDisable(false);
            if (!nameCombo.getItems().isEmpty())
                nameCombo.getSelectionModel().selectFirst();
            if (!privCombo.getItems().isEmpty())
                privCombo.getSelectionModel().selectFirst();
        });
        typeCombo.getOnAction().handle(null);

        Button okButton = new Button("OK");
        okButton.setOnAction(ev -> {
            String type = typeCombo.getValue();
            String objName = nameCombo.getValue();
            String priv = privCombo.getValue();
            boolean withGrantOption = grantOptionCombo.getValue().equals("TRUE");
            if (objName == null || priv == null)
                return;
            // Nếu là TABLE hoặc VIEW và UPDATE thì hiện popup chọn cột
            if ((type.equals("TABLE") || type.equals("VIEW")) && priv.equals("UPDATE")) {
                showTableColumnsDialogForGrant(objName, withGrantOption);
                dialog.close();
            } else {
                viewModel.grantGeneralPrivilege(type, objName, priv, withGrantOption);
                dialog.close();
            }
        });
        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        layout.getChildren().addAll(typeLabel, typeCombo, nameLabel, nameCombo, privLabel, privCombo,
                grantOptionLabel, grantOptionCombo, buttonBox);
        Scene scene = new Scene(layout, 350, 340);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showTableColumnsDialogForGrant(String objectName, boolean withGrantOption) {
        List<String> columns = viewModel.getColumnsOfTable(objectName);
        Stage dialog = new Stage();
        dialog.setTitle("Columns of " + objectName);
        Image iconImage = new Image(getClass().getResource("/images/app_icon.png").toExternalForm());
        dialog.getIcons().add(iconImage);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().add(new Label("Columns in '" + objectName + "':"));
        List<String> selectedColumns = new java.util.ArrayList<>();
        for (String col : columns) {
            HBox row = new HBox();
            row.setSpacing(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label colLabel = new Label(col);
            javafx.scene.control.CheckBox colCheckBox = new javafx.scene.control.CheckBox();
            colCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected)
                    selectedColumns.add(col);
                else
                    selectedColumns.remove(col);
            });
            HBox.setHgrow(colLabel, Priority.ALWAYS);
            colLabel.setMaxWidth(Double.MAX_VALUE);
            row.getChildren().addAll(colLabel, colCheckBox);
            row.setFillHeight(true);
            row.setStyle("-fx-padding: 0; -fx-alignment: center-right;");
            layout.getChildren().add(row);
        }
        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        layout.getChildren().add(spacer);
        Button okButton = new Button("OK");
        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        layout.getChildren().add(buttonBox);
        okButton.setOnAction(event -> {
            if (!selectedColumns.isEmpty()) {
                // Gọi hàm thực hiện store procedure với các tham số
                viewModel.grantUpdatePrivilegeWithOption(objectName, selectedColumns, withGrantOption);
            }
            dialog.close();
        });
        Scene scene = new Scene(layout, 350, 300);
        dialog.setScene(scene);
        dialog.show();
    }
}
