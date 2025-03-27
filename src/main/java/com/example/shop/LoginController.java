package com.example.shop;

import BUS.AccountBUS;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.xml.stream.EventFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class LoginController {
    @FXML
    private ImageView imgFormLoginRegisPanel;
    @FXML
    private Label lbFieldsUserNameLogin;
    @FXML
    private  Label lbErrorPasswordMessage;
    @FXML
    private  Label lbErrorUserNameMessage;
    @FXML
    private Label lbFieldPasswordLogin;
    @FXML
    private TextField txtFormLoginUserName;
    @FXML
    private PasswordField pswdFormLoginPassword;
    @FXML
    private Button btnFormLogin;

    @FXML
    protected void onLbFieldsUserNameLoginClicked(MouseEvent event) {
        txtFormLoginUserName.requestFocus();
    }

    @FXML
    protected void onLbFieldPasswordLoginClicked(MouseEvent event) {
        pswdFormLoginPassword.requestFocus();
    }

    @FXML
    protected void onBtnFormLoginClicked(MouseEvent event) {
        AccountBUS.getInstance().loadLocal();
        String userNameInput = txtFormLoginUserName.getText();
        String passwordInput = pswdFormLoginPassword.getText();
        boolean checkAccount = AccountBUS.getInstance().checkLogin(userNameInput, passwordInput);
        if(checkAccount) {
            lbErrorUserNameMessage.setText("done");
            lbErrorPasswordMessage.setText("done");
        }
        else if (userNameInput.equals("admin") && (passwordInput.equals("1234"))) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                Parent root = fxmlLoader.load();
                Stage stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            lbErrorUserNameMessage.setText("Error password or username");
            lbErrorPasswordMessage.setText("Error username or password");
            lbErrorUserNameMessage.setVisible(true);
            lbErrorPasswordMessage.setVisible(true);
        }
    }

    @FXML
    protected void onFormFieldKeyEnterPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            Node source = (Node) event.getSource();
            if (source.equals(txtFormLoginUserName))
                pswdFormLoginPassword.requestFocus();
            else if (source.equals(pswdFormLoginPassword)) {
                btnFormLogin.fireEvent(new MouseEvent(
                        MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0,
                        MouseButton.PRIMARY,
                        1,
                        false, false, false, false, false, false, false, false, false, false,
                        null
                ));
            }

        }
    }
}
