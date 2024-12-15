package com.example.kursach2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;
/**
 * JavaFX-приложение, генерирующее случайный рисунок из геометрических фигур на холсте.
 * Пользователи могут задавать параметры, такие как количество фигур, границы области рисования, размер кучности и видимость сетки.
 */
public class RandomDrawingJavaFX extends Application {

    private TextField numShapesField;
    private TextField xMinField, xMaxField, yMinField, yMaxField;
    private TextField clusterSizeField;
    private CheckBox showGridCheckbox;
    private Stage drawingStage;
    private Canvas canvas;
    private Random random = new Random();

    /**
     * {@inheritDoc}
     * Инициализирует и отображает главное окно приложения с полями ввода для параметров рисования.
     * @param primaryStage Основное окно приложения.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Random Drawing Generator (JavaFX)");


        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(5);
        inputGrid.setPadding(new Insets(10));

        addLabeledField(inputGrid, 0, 0, "Number of shapes:", numShapesField = new TextField("30"));
        addLabeledField(inputGrid, 0, 1, "Min X:", xMinField = new TextField("0"));
        addLabeledField(inputGrid, 0, 2, "Max X:", xMaxField = new TextField("500"));
        addLabeledField(inputGrid, 0, 3, "Min Y:", yMinField = new TextField("0"));
        addLabeledField(inputGrid, 0, 4, "Max Y:", yMaxField = new TextField("500"));
        addLabeledField(inputGrid, 0, 5, "Cluster size:", clusterSizeField = new TextField("30"));
        inputGrid.add(showGridCheckbox = new CheckBox("Show grid"), 0, 6);

        Button generateButton = new Button("Generate");
        generateButton.setOnAction(e -> generateDrawing());
        inputGrid.add(generateButton, 0, 7);


        VBox root = new VBox(10);
        root.getChildren().addAll(inputGrid);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Добавляет текстовое поле с меткой в указанный GridPane.
     * @param grid GridPane, куда добавляются элементы.
     * @param col Номер столбца.
     * @param row Номер строки.
     * @param label Текст метки.
     * @param field Текстовое поле.
     */
    private void addLabeledField(GridPane grid, int col, int row, String label, TextField field) {
        grid.add(new Label(label), col, row);
        grid.add(field, col + 1, row);
    }

    /**
     * Создает или отображает новое окно для отображения сгенерированного рисунка и вызывает метод drawShapes.
     */
    private void generateDrawing() {
        if (drawingStage == null || !drawingStage.isShowing()) {
            drawingStage = new Stage();
            drawingStage.setTitle("Generated Drawing");
            drawingStage.setOnCloseRequest(e -> drawingStage = null);

            canvas = new Canvas(500, 500);
            Pane root = new Pane(canvas);
            root.setPrefSize(500, 500);
            Scene scene = new Scene(root);
            drawingStage.setScene(scene);
            drawingStage.show();
        }
        drawShapes();
    }

    /**
     * Считывает входные параметры, проверяет их на корректность и рисует фигуры и сетку на холсте.
     */
    private void drawShapes() {
        try {
            int numShapes = Integer.parseInt(numShapesField.getText());
            double xMin = Double.parseDouble(xMinField.getText());
            double xMax = Double.parseDouble(xMaxField.getText());
            double yMin = Double.parseDouble(yMinField.getText());
            double yMax = Double.parseDouble(yMaxField.getText());
            int clusterSize = Integer.parseInt(clusterSizeField.getText());
            boolean showGrid = showGridCheckbox.isSelected();

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            if (showGrid) {
                drawGrid(gc, xMin, xMax, yMin, yMax);
            }

            for (int i = 0; i < numShapes; i++) {
                drawRandomShape(gc, xMin, xMax, yMin, yMax, clusterSize);
            }
        } catch (NumberFormatException ex) {
            showErrorDialog("Invalid input format!");
        }
    }

    /**
     * Рисует случайную геометрическую фигуру (прямоугольник, овал или дугу) на холсте.
     * @param gc Контекст графики для рисования.
     * @param xMin Минимальная координата x.
     * @param xMax Максимальная координата x.
     * @param yMin Минимальная координата y.
     * @param yMax Максимальная координата y.
     * @param clusterSize Максимальный размер фигуры.
     */
    private void drawRandomShape(GraphicsContext gc, double xMin, double xMax, double yMin, double yMax, int clusterSize) {
        int shapeType = random.nextInt(6);
        double x = random.nextDouble() * (xMax - xMin) + xMin;
        double y = random.nextDouble() * (yMax - yMin) + yMin;
        double size = random.nextDouble() * 40 + 10;

        double r = Math.min(1.0, random.nextDouble());
        double g = Math.min(1.0, random.nextDouble());
        double b = Math.min(1.0, random.nextDouble());
        gc.setFill(new Color(r, g, b, 1.0));

        switch (shapeType) {
            case 0:
                double x2 = x + random.nextInt(clusterSize * 2) - clusterSize;
                double y2 = y + random.nextInt(clusterSize * 2) - clusterSize;
                gc.strokeLine(x, y, x2, y2);
                break;
            case 1:
                gc.fillOval(x - size / 2, y - size / 2, size, size);
                break;
            case 2:
                gc.fillRect(x - size / 2, y - size / 2, size, size);
                break;
            case 3:
                double[] xPoints = {x, x - size / 2, x + size / 2};
                double[] yPoints = {y - size / 2, y + size / 2, y + size / 2};
                gc.fillPolygon(xPoints, yPoints, 3);
                break;
            case 4:
                drawParabola(gc, x, y, size);
                break;
            case 5:
                double[] xPoints2 = {x - size / 2, x + size / 2, x + size / 4, x - size / 4};
                double[] yPoints2 = {y + size / 2, y + size / 2, y - size / 2, y - size / 2};
                gc.fillPolygon(xPoints2, yPoints2, 4);
                break;
        }
    }

    /**
     * Рисует параболу на указанном GraphicsContext.
     * Характеристики параболы (кривизна и ширина) определяются случайным образом.
     * @param gc GraphicsContext, на котором рисуется парабола.
     * @param x Координата x вершины параболы.
     * @param y Координата y вершины параболы.
     * @param size Приблизительная ширина параболы.
     */
    private void drawParabola(GraphicsContext gc, double x, double y, double size) {
        double a = random.nextDouble() * 0.1;
        double focus = random.nextInt(20) + 10;
        double width = size;

        gc.beginPath();
        gc.moveTo(x - width / 2, y + focus);
        for (double i = -width / 2; i <= width / 2; i += 0.5) {
            double xCoord = x + i;
            double yCoord = y + i * i / (4 * focus);
            gc.lineTo(xCoord, yCoord);
        }
        gc.closePath();
        gc.fill();
    }

    /**
     * Рисует сетку на холсте.
     * @param gc Контекст графики для рисования.
     * @param xMin Минимальная координата x.
     * @param xMax Максимальная координата x.
     * @param yMin Минимальная координата y.
     * @param yMax Максимальная координата y.
     */
    private void drawGrid(GraphicsContext gc, double xMin, double xMax, double yMin, double yMax) {
        gc.setStroke(Color.LIGHTGRAY);
        for (int i = (int) xMin; i <= xMax; i += 20) {
            gc.strokeLine(i, yMin, i, yMax);
        }
        for (int i = (int) yMin; i <= yMax; i += 20) {
            gc.strokeLine(xMin, i, xMax, i);
        }
    }

    /**
     * Отображает диалоговое окно с сообщением об ошибке.
     * @param message Текст сообщения об ошибке.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Главный метод, запускающий JavaFX-приложение.
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        launch(args);
    }
}