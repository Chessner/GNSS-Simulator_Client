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

/**
 * This class handles and presents various Points on a canvas within a circle.
 * It also is a mixture between all three parts of the MVC pattern, meaning it not only creates the view,
 * but also handles changes to it and the data.
 */
public class DeviationView implements PositionUpdateListener {
    /**
     * Singleton instance of DevViewChangeListener.
     */
    private DevViewChangeListener mChangeListener;
    /**
     * Reference to the root StackPane of this view.
     */
    private StackPane mDevView;
    /**
     * Unique id for the canvas on which the various points will be drawn.
     */
    private final String DEVVIEW_CANVAS_ID = "DEVVIEW_CANVAS";
    /**
     * List containing the received points.
     */
    private final ArrayList<Point> mData = new ArrayList<>();
    /**
     * Reference to the parent GlobalView containing this view.
     */
    private GlobalView mGlobalView;

    /**
     * Constructor for a new DeviationView.
     * @param _globalView Reference to the parent GlobalView containing this view.
     */
    public DeviationView(GlobalView _globalView) {
        mGlobalView = _globalView;
    }

    /**
     * Initializer for this view, setting up the views and canvas.
     * @return StackPane containing all initialized javafx elements.
     */
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

    /**
     * Special initializer of this view called after starting the stage.
     * It sets up the overlay of this view.
     */
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

    /**
     * Provides singleton instance of DevViewChangeListener.
     *
     * @return Instance of DevViewChangeListener.
     */
    public DevViewChangeListener getChangeListener() {
        if (mChangeListener == null) mChangeListener = new DevViewChangeListener();

        return mChangeListener;
    }

    @Override
    public void update(NMEAInfo _info) {
        if (_info.mLatitude == 0d && _info.mLongitude == 0d) return;


        Point point = new Point((int) (_info.mLongitude * 1000000), (int) (_info.mLatitude * 10000000));

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

    /**
     * Implementation of a ChangeListener. It gets notified whenever a specific value changes.
     */
    public class DevViewChangeListener implements ChangeListener<Number> {
        private DevViewChangeListener() {
        }

        /**
         * Method that handles the notification, that a value that this object listens to, has changed.
         *
         * @param _observable Observable value this object listens to.
         * @param _oldValue   Previous value.
         * @param _newValue   Value the variable has changed to.
         */
        @Override
        public void changed(ObservableValue<? extends Number> _observable, Number _oldValue, Number _newValue) {
            if (_observable instanceof ReadOnlyDoubleProperty) {
                ReadOnlyDoubleProperty dProp = (ReadOnlyDoubleProperty) _observable;
                double val = dProp.doubleValue();
                String name = dProp.getName();


                if (name.equalsIgnoreCase("width")) {
                    double smallerSide = Math.min(val, mDevView.getHeight());
                    double offset = smallerSide / 14;
                    double cWidth = val/2 - offset * 2;
                    double cHeight = mDevView.getHeight() - offset * 2;
                    double cCenterX = val / 4;
                    double cCenterY = mDevView.getHeight() / 2;
                    smallerSide = Math.min(cWidth, cHeight);

                    AnchorPane.setLeftAnchor(mDevView, val / 2);
                    Circle outerCircle = (Circle) mDevView.lookup("#OuterCircle");
                    if (outerCircle != null) {
                        outerCircle.setCenterX(cCenterX);
                        outerCircle.setCenterY(cCenterY);
                        outerCircle.setRadius(smallerSide / 2);
                    }
                } else if (name.equalsIgnoreCase("height")) {
                    double smallerSide = Math.min(mDevView.getWidth(), val);
                    double offset = smallerSide / 14;
                    double cWidth = mDevView.getWidth() - offset * 2;
                    double cHeight = val - offset * 2;
                    double cCenterX = mDevView.getWidth() / 2;
                    double cCenterY = val / 2;
                    smallerSide = Math.min(cWidth, cHeight);

                    Circle outerCircle = (Circle) mDevView.lookup("#OuterCircle");
                    if (outerCircle != null) {
                        outerCircle.setCenterX(cCenterX);
                        outerCircle.setCenterY(cCenterY);
                        outerCircle.setRadius(smallerSide / 2);
                    }
                }
            }
        }
    }
}
