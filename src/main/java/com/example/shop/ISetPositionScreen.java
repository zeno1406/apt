package com.example.shop;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ISetPositionScreen {
    public void setPositionScreen (Stage stage, Scene scene) {
        // Get user's screen size
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Get window size
        double windowWidth = scene.getWidth();
        double windowHeight = scene.getHeight();

        // Calculate center position
        double centerX = (screenWidth - windowWidth) / 2;
        double centerY = (screenHeight - windowHeight) / 2;
        stage.setX(centerX);
        stage.setY(centerY);
    }
}
