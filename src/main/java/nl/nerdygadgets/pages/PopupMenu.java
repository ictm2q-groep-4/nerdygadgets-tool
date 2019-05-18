package nl.nerdygadgets.pages;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.nerdygadgets.infrastructure.components.Component;
import nl.nerdygadgets.infrastructure.components.ComponentType;

public class PopupMenu {

    private Stage window;

    private TextField
            hostNameField,
            IPV4Field,
            IPV6Field,
            SSHUsernameField,
            SSHPasswordField;

    private Button submit;

    private Pane pane;

    private Label errorLabel;

    private boolean ok;

    public PopupMenu(Pane pane, boolean editing) {
        this.pane = pane;
        this.window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);

        try {

            VBox dialog;
            Component dataContainer = (Component) pane.getUserData();

            boolean isHardware = dataContainer.componentType == ComponentType.DATABASESERVER || dataContainer.componentType == ComponentType.WEBSERVER;

            if (isHardware) {
                dialog = FXMLLoader.load(getClass().getResource("/pages/components/AttributePopup.fxml"));

                IPV4Field = (TextField) dialog.getChildren().get(3);
                IPV6Field = (TextField) dialog.getChildren().get(5);
                SSHUsernameField = (TextField) dialog.getChildren().get(7);
                SSHPasswordField = (TextField) dialog.getChildren().get(9);
            } else {
                dialog = FXMLLoader.load(getClass().getResource("/pages/components/HostnameAlert.fxml"));
            }

            hostNameField = (TextField) dialog.getChildren().get(1);


            if (editing) {
                hostNameField.setText(dataContainer.getHostname());

                if (isHardware) {
                    if (dataContainer.ipv4 != null && dataContainer.ipv4.toString() != null) {
                        IPV4Field.setText(dataContainer.ipv4.toString().replaceAll("/", "").replaceAll("localhost", ""));
                    }

                    if (dataContainer.ipv6 != null && dataContainer.ipv6.toString() != null) {
                        IPV6Field.setText(dataContainer.ipv6.toString().replaceAll("/", "").replaceAll("localhost", ""));
                    }

                    if (dataContainer.username != null) {
                        SSHUsernameField.setText(dataContainer.username);
                    }

                    if (dataContainer.password != null) {
                        SSHPasswordField.setText(dataContainer.password);
                    }
                }

            }

            if (isHardware) {
                errorLabel = (Label) dialog.getChildren().get(10);
                submit = (Button) dialog.getChildren().get(11);
            } else {
                errorLabel = (Label) dialog.getChildren().get(2);
                submit = (Button) dialog.getChildren().get(3);
            }

            submit.setDefaultButton(true);

            submit.setOnAction(event -> {

                if (hostNameField.getText().trim().isEmpty()) {
                    errorLabel.setText("Vul alstublieft een hostnaam in.");
                    errorLabel.setVisible(true);
                    return;
                }

                dataContainer.hostname = hostNameField.getText();

                if(isHardware) {
                    dataContainer.setIpv4(IPV4Field.getText().trim().isEmpty() ? "127.0.0.1" : IPV4Field.getText());
                    dataContainer.setIpv6(IPV6Field.getText().trim().isEmpty() ? "::1" : IPV6Field.getText());

                    dataContainer.setUser(SSHUsernameField.getText());
                    dataContainer.setPass(SSHPasswordField.getText());
                }

                pane.setUserData(dataContainer);

                this.ok = true;
                window.close();
            });

            window.setScene(new Scene(dialog));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public Pane getPane() {
        return pane;
    }

    public Stage getWindow() {
        return window;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public TextField getHostNameField() {
        return hostNameField;
    }

    public TextField getIPV4Field() {
        return IPV4Field;
    }

    public TextField getIPV6Field() {
        return IPV6Field;
    }

    public TextField getSSHUsernameField() {
        return SSHUsernameField;
    }

    public TextField getSSHPasswordField() {
        return SSHPasswordField;
    }

    public Button getSubmit() {
        return submit;
    }

    public boolean isOk() {
        return ok;
    }

}
