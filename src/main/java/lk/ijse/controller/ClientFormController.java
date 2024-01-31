package lk.ijse.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientFormController {

    public javafx.scene.control.Button Cameraman;
    public javafx.scene.control.Button emoji1;

    @FXML
    private Label lblClientName;

    @FXML
    private AnchorPane emoji;

    @FXML
    private VBox msgVbox;

    @FXML
    private TextField txtMessage;

    public ScrollPane scrollPane;
    static boolean openWindow = false;
    private static final double PANE_HEIGHT = 500;
    public AnchorPane emojiPane;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName ;



    public void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost", 3030);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Client connected");
                    ServerFormController.receiveMessage(clientName+" joined.");

                    while (socket.isConnected()){
                        String receivingMsg = dataInputStream.readUTF();
                        receivingMsg(receivingMsg, ClientFormController.this.msgVbox);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

        this.msgVbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) newValue);
            }
        });
        emoji.setVisible(false);
    }


    public void setClientName(String name) {
        lblClientName.setText(name);
        clientName = name;
    }

    private void receivingMsg(String receivingMsg, VBox msgVbox) {
        if (receivingMsg.matches(".*\\.(png|jpe?g|gif)$")){

            String [] msg = receivingMsg.split("#");
            String name = msg[0];
            String msgFromServer = msg[1];

            System.out.println(name);
            System.out.println(msgFromServer);
            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(name);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            Image image = new Image(msgFromServer);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));
            hBox.getChildren().add(imageView);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    msgVbox.getChildren().add(hBoxName);
                    msgVbox.getChildren().add(hBox);
                }
            });

        }else {
            String name = receivingMsg.split(":")[0];
            String msgFromServer = receivingMsg.split(":")[1];

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));

            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(name);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            Text text = new Text(msgFromServer);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0,0,0));

            hBox.getChildren().add(textFlow);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    msgVbox.getChildren().add(hBoxName);
                    msgVbox.getChildren().add(hBox);
                }
            });
        }
    }

    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()){
            if(!msgToSend.matches(".*\\.(png|jpe?g|gif)$")){

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 0, 10));

                Text text = new Text(msgToSend);
                text.setStyle("-fx-font-size: 14");
                TextFlow textFlow = new TextFlow(text);

                textFlow.setStyle("-fx-background-color: #0693e3; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(1, 1, 1));

                hBox.getChildren().add(textFlow);

                HBox hBoxTime = new HBox();
                hBoxTime.setAlignment(Pos.CENTER_RIGHT);
                hBoxTime.setPadding(new Insets(0, 5, 5, 10));
                String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                Text time = new Text(stringTime);
                time.setStyle("-fx-font-size: 8");

                hBoxTime.getChildren().add(time);

                msgVbox.getChildren().add(hBox);
                msgVbox.getChildren().add(hBoxTime);

                try {
                    dataOutputStream.writeUTF(clientName + ":" + msgToSend);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                txtMessage.clear();
            }
        }
    }

    @FXML
    void sendbtnOnAction(ActionEvent event) { sendMsg(txtMessage.getText()); }

    @FXML
    void txtMessageOnAction(ActionEvent actionEvent) {
        sendButtonOnAction(actionEvent);
    }

    private void sendButtonOnAction(ActionEvent actionEvent) { sendMsg(txtMessage.getText()); }

    @FXML
    void CamerabtnOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);

        Window window = ((Node) actionEvent.getTarget()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            sendImage(file.toURI().toString());
        }
    }

    private void configureFileChooser(FileChooser fileChooser) {
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
        );
    }

    private void sendImage(String file) {
        Image image = new Image(file);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5,5,5,10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        msgVbox.getChildren().add(hBox);

        try {
            System.out.println(clientName + ":" + file);
            dataOutputStream.writeUTF(clientName + "#" + file);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void emojiPaneOnAction(MouseEvent event) {
        emoji.setVisible(true);
    }

    @FXML
    void btnEmojiOnAction(MouseEvent event) {
        if(emoji.isVisible()){
            emoji.setVisible(false);
        }else{
            emoji.setVisible(true);
        }

    }

    @FXML
    void sad(MouseEvent mouseEvent) {
        String emoji = new String(Character.toChars(128546));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    @FXML
    void real_amile(MouseEvent event) {
        String emoji = new String(Character.toChars(128514));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    @FXML
    void woow(MouseEvent event) {
        String emoji = new String(Character.toChars(128559));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    @FXML
    void love(MouseEvent event) {
        String emoji = new String(Character.toChars(128525));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    @FXML
    void tong_smile(MouseEvent event) {
        String emoji = new String(Character.toChars(128539));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    @FXML
    void money(MouseEvent event) {
        String emoji = new String(Character.toChars(129297));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }


    @FXML
    void lot_sad(MouseEvent event) {
        String emoji = new String(Character.toChars(128554));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    @FXML
    void smile_one_eyy(MouseEvent event) {
        String emoji = new String(Character.toChars(128540));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }


    @FXML
    void tuin(MouseEvent event) {
        String emoji = new String(Character.toChars(128519));
        txtMessage.setText(emoji);
        emojiPane.setVisible(false);
    }

    public void shutdown() {
        ServerFormController.receiveMessage(clientName+ "left");
    }

}
