package at.fh.hgb.mc;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DeviationView implements PositionUpdateListener {
    private DevViewChangeListener mChangeListener;
    private StackPane mDevView;
    private final String DEVVIEW_CANVAS_ID = "DEVVIEW_CANVAS";
    private ArrayList<Point> mData = new ArrayList<>();
    private GlobalView mGlobalView;

    public DeviationView(GlobalView _globalView) {
        mGlobalView = _globalView;
    }

    public StackPane init() {
        mDevView = new StackPane();
        mDevView.setMinSize(0, 0);
        mDevView.setStyle("-fx-background-color: lightgray;");

        Canvas canvas = new Canvas();
        canvas.setId(DEVVIEW_CANVAS_ID);
        canvas.widthProperty().bind(mDevView.widthProperty());
        canvas.heightProperty().bind(mDevView.heightProperty());

        mDevView.getChildren().addAll(canvas);

        AnchorPane.setRightAnchor(mDevView, 0d);
        AnchorPane.setBottomAnchor(mDevView, 0d);
        AnchorPane.setTopAnchor(mDevView, 0d);

        return mDevView;
    }

    public void lateInit() {
        double smallerSide = Math.min(mDevView.getWidth(), mDevView.getHeight());
        double offset = smallerSide / 14;
        double cWidth = mDevView.getWidth() - offset * 2;
        double cHeight = mDevView.getHeight() - offset * 2;
        double cCenterX = mDevView.getWidth() / 2;
        double cCenterY = mDevView.getHeight() / 2;
        smallerSide = Math.min(cWidth, cHeight);

        //outer circle
        Circle outerCircle = new Circle();
        outerCircle.setId("OuterCircle");
        outerCircle.setCenterX(cCenterX);
        outerCircle.setCenterY(cCenterY);
        outerCircle.setStyle("-fx-fill: #ffffff00; -fx-stroke: black");
        outerCircle.setRadius(smallerSide / 2);

        mDevView.getChildren().addAll(outerCircle);
    }

    public DevViewChangeListener getChangeListener() {
        if (mChangeListener == null) mChangeListener = new DevViewChangeListener();

        return mChangeListener;
    }

    @Override
    public void update(NMEAInfo _info) {
        if (_info.mLatitude == 0d && _info.mLongitude == 0d) return;


        Point point = new Point((int) (_info.mLongitude * 10000000), (int) (_info.mLatitude * 10000000));

        Canvas canvas = (Canvas) mGlobalView.mScene.lookup("#" + DEVVIEW_CANVAS_ID);
        double smallerSide = Math.min(canvas.getWidth(), canvas.getHeight());
        double offset = smallerSide / 14;
        double cWidth = canvas.getWidth() - offset * 2;
        double cHeight = canvas.getHeight() - offset * 2;
        double cCenterX = cWidth / 2 + offset;
        double cCenterY = cHeight / 2 + offset;
        smallerSide = Math.min(cWidth, cHeight);


        //create bounding box for data
        Rectangle world = new Rectangle(point.x - 1, point.y - 1, 2, 2).getBounds();
        for (int i = 0; i < mData.size(); i++) {
            Point p = mData.get(i);
            world = world.union(new Rectangle(p.x - 1, p.y - 1, 2, 2).getBounds());
        }
        mData.add(point);

        //create bounding box for canvas
        double r = Math.cos(45 * (Math.PI / 180d)) * smallerSide / 2;
        Rectangle window = new Rectangle((int) (cCenterX - r), (int) (cCenterY - r), (int) (r * 2), (int) (r * 2));

        //create transformation matrix to make data fit on canvas.
        Matrix transformationMatrix = Matrix.zoomToFit(world, window, true);

        //transform data
        ArrayList<Point> transformedData = new ArrayList<>();
        for (Point p : mData) {
            transformedData.add(transformationMatrix.multiply(p));
        }

        BufferedImage image = new BufferedImage((int) (cWidth + offset * 2), (int) (cHeight + offset * 2), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();


        //make background gray
        g2d.setColor(new Color(216, 212, 212));
        g2d.fillRect(0, 0, (int) (cWidth + offset * 2), (int) (cHeight + offset * 2));

        g2d.setColor(Color.BLACK);

        int[] xpoints = new int[transformedData.size()];
        int[] ypoints = new int[transformedData.size()];

        //draw points
        for (int i = 0; i < transformedData.size(); i++) {
            Point p = transformedData.get(i);
            if (i == transformedData.size() - 1) {
                g2d.setColor(Color.RED);
            }
            g2d.fillOval(p.x - 2, p.y - 2, 4, 4);

            xpoints[i] = p.x;
            ypoints[i] = p.y;
        }
        g2d.setColor(Color.BLACK);
        g2d.drawPolyline(xpoints, ypoints, transformedData.size());


        WritableImage writable = SwingFXUtils.toFXImage(image, null);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(writable, 0, 0);
    }

    public class DevViewChangeListener implements ChangeListener<Number> {
        private DevViewChangeListener() {
        }

        @Override
        public void changed(ObservableValue<? extends Number> _observableValue, Number _oldValue, Number _newValue) {
            if (_observableValue instanceof ReadOnlyDoubleProperty) {
                ReadOnlyDoubleProperty dProp = (ReadOnlyDoubleProperty) _observableValue;
                double val = dProp.doubleValue();
                String name = dProp.getName();

                double smallerSide = Math.min(mDevView.getWidth(), mDevView.getHeight());
                double offset = smallerSide / 14;
                double cWidth = mDevView.getWidth() - offset * 2;
                double cHeight = mDevView.getHeight() - offset * 2;
                double cCenterX = mDevView.getWidth() / 2;
                double cCenterY = mDevView.getHeight() / 2;
                smallerSide = Math.min(cWidth, cHeight);

                if (name.equalsIgnoreCase("width")) {
                    AnchorPane.setLeftAnchor(mDevView, val / 2);
                    Circle outerCircle = (Circle) mDevView.lookup("#OuterCircle");
                    if (outerCircle != null) {
                        outerCircle.setCenterX(cCenterX);
                        outerCircle.setRadius(smallerSide / 2);
                    }
                } else if (name.equalsIgnoreCase("height")) {
                    Circle outerCircle = (Circle) mDevView.lookup("#OuterCircle");
                    if (outerCircle != null) {
                        outerCircle.setCenterY(cCenterY);
                        outerCircle.setRadius(smallerSide / 2);
                    }
                }
            }
        }
    }
}
