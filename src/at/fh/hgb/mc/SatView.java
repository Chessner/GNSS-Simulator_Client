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

import java.awt.*;
import java.awt.image.BufferedImage;

public class SatView implements PositionUpdateListener {
    private SatViewChangeListener mChangeListener;
    private StackPane mSatView;
    private GlobalView mGlobalView;
    public final String SATVIEW_CANVAS_ID = "SATVIEW_CANVAS";

    public SatView(GlobalView _globalView) {
        mGlobalView = _globalView;
    }

    public StackPane init() {
        mSatView = new StackPane();
        mSatView.setMinSize(0, 0);
        mSatView.setStyle("-fx-background-color: blue;");

        Canvas canvas = new Canvas();
        canvas.setId(SATVIEW_CANVAS_ID);
        canvas.widthProperty().bind(mSatView.widthProperty());
        canvas.heightProperty().bind(mSatView.heightProperty());

        mSatView.getChildren().addAll(canvas);

        AnchorPane.setLeftAnchor(mSatView, 0d);
        AnchorPane.setBottomAnchor(mSatView, 0d);
        AnchorPane.setTopAnchor(mSatView, 0d);

        return mSatView;
    }

    public SatViewChangeListener getChangeListener() {
        if (mChangeListener == null) mChangeListener = new SatViewChangeListener();

        return mChangeListener;
    }

    @Override
    public void update(NMEAInfo _info) {
        Canvas canvas = (Canvas) mGlobalView.mScene.lookup("#" + SATVIEW_CANVAS_ID);

        double smallerSide = Math.min(canvas.getWidth(), canvas.getHeight());
        double offset = smallerSide/14;
        double cWidth = canvas.getWidth() - offset * 2;
        double cHeight = canvas.getHeight() - offset * 2;
        double cCenterX = cWidth / 2 + offset;
        double cCenterY = cHeight / 2 + offset;
        smallerSide = Math.min(cWidth,cHeight);

        BufferedImage image = new BufferedImage((int) (cWidth + offset * 2), (int) (cHeight + offset * 2), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        //make background gray
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, (int) (cWidth + offset * 2), (int) (cHeight + offset * 2));

        g2d.setColor(Color.black);

        //draw lines
        g2d.drawLine((int) (cCenterX - smallerSide / 2 - offset / 2), (int) cCenterY, (int) (cCenterX + smallerSide / 2 + offset / 2), (int) cCenterY);
        g2d.drawLine((int) cCenterX, (int) (cCenterY - smallerSide / 2 - offset / 2), (int) cCenterX, (int) (cCenterY + smallerSide / 2 + offset / 2));

        //calc outer ring
        double hypotenuse = smallerSide / 2;
        double ankathete = Math.cos(0) * hypotenuse;

        //draw outer ring
        g2d.drawOval((int) (cCenterX - ankathete), (int) (cCenterY - ankathete), (int) ankathete * 2, (int) ankathete * 2);

        //calc inner ring
        ankathete = Math.cos(45 * (Math.PI / 180d)) * hypotenuse;

        //draw inner ring
        g2d.drawOval((int) (cCenterX - ankathete), (int) (cCenterY - ankathete), (int) ankathete * 2, (int) ankathete * 2);

        for (SatelliteInfo satelliteInfo : _info.mSatellites) {
           /* double r = Math.cos(satelliteInfo.mAngleToHorizontal * (Math.PI / 180d)) * hypotenuse;
            double x = r * Math.cos((satelliteInfo.mAngleToNorth - 90) * (Math.PI / 180d));
            double y = r * Math.sin((satelliteInfo.mAngleToNorth - 90) * (Math.PI / 180d));

            g2d.drawRect((int) (cCenterX + x - 10), (int) (cCenterY + y - 10), 20, 20);*/
            satelliteInfo.draw(g2d,cCenterX,cCenterY,hypotenuse);
        }

        WritableImage writable = SwingFXUtils.toFXImage(image, null);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(writable, 0, 0);
    }

    public class SatViewChangeListener implements ChangeListener<Number> {
        private SatViewChangeListener() {
        }

        @Override
        public void changed(ObservableValue<? extends Number> _observableValue, Number _oldValue, Number _newValue) {
            if (_observableValue instanceof ReadOnlyDoubleProperty) {
                ReadOnlyDoubleProperty dProp = (ReadOnlyDoubleProperty) _observableValue;
                double val = dProp.doubleValue();
                String name = dProp.getName();
                if (name.equalsIgnoreCase("width")) {
                    AnchorPane.setRightAnchor(mSatView, val / 2);
                }
            }
        }
    }
}
