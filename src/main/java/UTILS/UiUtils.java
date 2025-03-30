package UTILS;

import DTO.EmployeeDTO;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

public class UiUtils {
    private static final UiUtils INSTANCE = new UiUtils();
    private double xOffset = 0;
    private double yOffset = 0;
    private UiUtils() {

    }
    public static UiUtils gI() {
        return INSTANCE;
    }

    public <S, T> void addTooltipToColumn(TableColumn<S, T> column, int maxLength, Function<T, String> toStringFunction) {
        column.setCellFactory(tc -> new TableCell<>() {
            private final Tooltip tooltip = new Tooltip();

            {
                // Cấu hình tooltip một lần
                tooltip.setShowDelay(Duration.millis(100));
                tooltip.setHideDelay(Duration.millis(50));
                tooltip.setWrapText(true); // Cho phép hiển thị nhiều dòng nếu cần
                tooltip.setMaxWidth(300);  // Giới hạn độ rộng tooltip
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String text = toStringFunction.apply(item);
                    if (text == null) text = ""; // Tránh lỗi null

                    if (text.length() > maxLength) {
                        setText(text.substring(0, maxLength) + "...");
                        tooltip.setText(text);
                        setTooltip(tooltip);
                    } else {
                        setText(text);
                        setTooltip(null);
                    }
                }
            }
        });
    }

    public <S> void addTooltipToColumn(TableColumn<S, String> column, int maxLength) {
        addTooltipToColumn(column, maxLength, Function.identity());
    }


    public void makeWindowDraggable(Parent root, Stage stage) {
        root.setOnMousePressed((MouseEvent e) -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        root.setOnMouseDragged((MouseEvent e) -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
            stage.setOpacity(0.8); // Làm mờ khi kéo
        });

        root.setOnMouseReleased((MouseEvent e) -> {
            stage.setOpacity(1); // Trả lại độ trong suốt bình thường
        });
    }

    public void applyButtonAnimation(Button btn) {
        ParallelTransition animation = createButtonAnimation(btn);
        animation.play();
    }

    public ParallelTransition createButtonAnimation(Button btn) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), btn);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), btn);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.8);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(2);

        return new ParallelTransition(scaleTransition, fadeTransition);
    }

    public void openStage(String fxmlFile, Consumer<Object> controllerInitializer, Runnable onCloseCallback, Class<?> referenceClass) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(referenceClass.getResource(fxmlFile));
                Parent root = loader.load();

                Object controller = loader.getController();
                if (controllerInitializer != null) {
                    controllerInitializer.accept(controller); // Khởi tạo controller
                }

                Stage stage = new Stage();
                stage.setTitle("Your Dialog Title");
                stage.initModality(Modality.APPLICATION_MODAL); // Block UI chính

                stage.setOnHidden(event -> {
                    if (onCloseCallback != null) {
                        onCloseCallback.run(); // Gọi callback khi modal đóng
                    }
                });

                stage.getIcons().add(new javafx.scene.image.Image("img/logo.png"));
                stage.setScene(new Scene(root));
                stage.showAndWait(); // Chặn UI chính cho đến khi modal đóng

            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        });
    }

    /*
    * UiUtils.gI().openStage("EmployeeEdit.fxml", controller -> {
    EmployeeEditController editController = (EmployeeEditController) controller;
    editController.setEmployee(selectedEmployee); // Truyền dữ liệu
}, this::refreshTable, getClass());*/


}
