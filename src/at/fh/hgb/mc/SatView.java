package at.fh.hgb.mc;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class SatView implements PositionUpdateListener {
    private SatViewChangeListener mChangeListener;
    private AnchorPane mSatView;
    private GlobalView mGlobalView;

    public SatView(GlobalView _globalView) {
        mGlobalView = _globalView;
    }

    public AnchorPane init() {
        mSatView = new AnchorPane();
        mSatView.setMinSize(0, 0);
        mSatView.setStyle("-fx-background-color: lightgray;");

        AnchorPane.setLeftAnchor(mSatView, 0d);
        AnchorPane.setBottomAnchor(mSatView, 0d);
        AnchorPane.setTopAnchor(mSatView, 0d);

        return mSatView;
    }

    public void lateInit() {
        double smallerSide = Math.min(mSatView.getWidth(), mSatView.getHeight());
        double offset = smallerSide / 14;
        double cWidth = mSatView.getWidth() - offset * 2;
        double cHeight = mSatView.getHeight() - offset * 2;
        double cCenterX = mSatView.getWidth() / 2;
        double cCenterY = mSatView.getHeight() / 2;
        smallerSide = Math.min(cWidth, cHeight);

        //outer circle
        Circle outerCircle = new Circle();
        outerCircle.setId("OuterCircle");
        outerCircle.setCenterX(cCenterX);
        outerCircle.setCenterY(cCenterY);
        outerCircle.setStyle("-fx-fill: #ffffff00; -fx-stroke: black");
        outerCircle.setRadius(smallerSide / 2);

        //inner circle
        Circle innerCircle = new Circle();
        innerCircle.setId("InnerCircle");
        innerCircle.setCenterX(cCenterX);
        innerCircle.setCenterY(cCenterY);
        innerCircle.setStyle("-fx-fill: #ffffff00; -fx-stroke: black");
        double radius = Math.cos(45 * (Math.PI / 180d)) * (smallerSide / 2);
        innerCircle.setRadius(radius);

        //draw lines
        Line horiLine = new Line();
        horiLine.setId("HorizontalLine");
        horiLine.setStartX(cCenterX - smallerSide / 2 - offset / 2);
        horiLine.setStartY(cCenterY);
        horiLine.setEndX(cCenterX + smallerSide / 2 + offset / 2);
        horiLine.setEndY(cCenterY);

        Line vertLine = new Line();
        vertLine.setId("VerticalLine");
        vertLine.setStartX(cCenterX);
        vertLine.setStartY(cCenterY - smallerSide / 2 - offset / 2);
        vertLine.setEndX(cCenterX);
        vertLine.setEndY(cCenterY + smallerSide / 2 + offset / 2);

        mSatView.getChildren().addAll(outerCircle, innerCircle, horiLine, vertLine);
    }

    public SatViewChangeListener getChangeListener() {
        if (mChangeListener == null) mChangeListener = new SatViewChangeListener();

        return mChangeListener;
    }

    @Override
    public void update(NMEAInfo _info) {

        double smallerSide = Math.min(mSatView.getWidth(), mSatView.getHeight());
        double offset = smallerSide / 14;
        double cWidth = mSatView.getWidth() - offset * 2;
        double cHeight = mSatView.getHeight() - offset * 2;
        double cCenterX = mSatView.getWidth() / 2;
        double cCenterY = mSatView.getHeight() / 2;
        smallerSide = Math.min(cWidth, cHeight);

        mSatView.getChildren().removeIf(n -> n.getId().equals("Satellite"));

        for (SatelliteInfo satelliteInfo : _info.mSatellites) {
            satelliteInfo.createShape(mSatView, cCenterX, cCenterY, smallerSide / 2);
        }
    }

    public class SatViewChangeListener implements ChangeListener<Number> {
        private SatViewChangeListener() {
        }

        @Override
        public void changed(ObservableValue<? extends Number> _observableValue, Number _oldValue, Number _newValue) {
            if (_observableValue instanceof ReadOnlyDoubleProperty) {
                ReadOnlyDoubleProperty dProp = (ReadOnlyDoubleProperty) _observableValue;
                double val = dProp.doubleValue();
                double halfVal = val/2;
                String name = dProp.getName();

                if (name.equalsIgnoreCase("width")) {
                    double smallerSide = Math.min(val, mSatView.getHeight());
                    double offset = smallerSide / 14;
                    double cWidth = halfVal - offset * 2;
                    double cHeight = mSatView.getHeight() - offset * 2;
                    double cCenterX = halfVal / 2;
                    double cCenterY = mSatView.getHeight() / 2;
                    smallerSide = Math.min(cWidth, cHeight);
                    AnchorPane.setRightAnchor(mSatView, val / 2);

                    Line vertLine = (Line) mSatView.lookup("#VerticalLine");
                    if (vertLine != null) {
                        vertLine.setStartX(cCenterX);
                        vertLine.setEndX(cCenterX);
                        vertLine.setStartY(cCenterY - smallerSide / 2 - offset / 2);
                        vertLine.setEndY(cCenterY + smallerSide / 2 + offset / 2);
                    }

                    Line horiLine = (Line) mSatView.lookup("#HorizontalLine");
                    if (horiLine != null) {
                        horiLine.setStartY(cCenterY);
                        horiLine.setEndY(cCenterY);
                        horiLine.setStartX(cCenterX - smallerSide / 2 - offset / 2);
                        horiLine.setEndX(cCenterX + smallerSide / 2 + offset / 2);
                    }


                    Circle outerCircle = (Circle) mSatView.lookup("#OuterCircle");
                    if (outerCircle != null) {
                        outerCircle.setCenterX(cCenterX);
                        outerCircle.setCenterY(cCenterY);
                        outerCircle.setRadius(smallerSide / 2);
                    }

                    //inner circle
                    Circle innerCircle = (Circle) mSatView.lookup("#InnerCircle");
                    if (innerCircle != null) {
                        innerCircle.setCenterX(cCenterX);
                        innerCircle.setCenterY(cCenterY);
                        double radius = Math.cos(45 * (Math.PI / 180d)) * (smallerSide / 2);
                        innerCircle.setRadius(radius);
                    }


                } else if (name.equalsIgnoreCase("height")) {
                    double smallerSide = Math.min(mSatView.getWidth(), val);
                    double offset = smallerSide / 14;
                    double cWidth = mSatView.getWidth() - offset * 2;
                    double cHeight = val - offset * 2;
                    double cCenterX = mSatView.getWidth() / 2;
                    double cCenterY = val / 2;
                    smallerSide = Math.min(cWidth, cHeight);

                    Line vertLine = (Line) mSatView.lookup("#VerticalLine");
                    if (vertLine != null) {
                        vertLine.setStartX(cCenterX);
                        vertLine.setEndX(cCenterX);
                        vertLine.setStartY(cCenterY - smallerSide / 2 - offset / 2);
                        vertLine.setEndY(cCenterY + smallerSide / 2 + offset / 2);
                    }

                    Line horiLine = (Line) mSatView.lookup("#HorizontalLine");
                    if (horiLine != null) {
                        horiLine.setStartY(cCenterY);
                        horiLine.setEndY(cCenterY);
                        horiLine.setStartX(cCenterX - smallerSide / 2 - offset / 2);
                        horiLine.setEndX(cCenterX + smallerSide / 2 + offset / 2);
                    }


                    Circle outerCircle = (Circle) mSatView.lookup("#OuterCircle");
                    if (outerCircle != null) {
                        outerCircle.setCenterY(cCenterY);
                        outerCircle.setCenterX(cCenterX);
                        outerCircle.setRadius(smallerSide / 2);
                    }

                    //inner circle
                    Circle innerCircle = (Circle) mSatView.lookup("#InnerCircle");
                    if (innerCircle != null) {
                        innerCircle.setCenterX(cCenterX);
                        innerCircle.setCenterY(cCenterY);
                        double radius = Math.cos(45 * (Math.PI / 180d)) * (smallerSide / 2);
                        innerCircle.setRadius(radius);
                    }


                }
            }
        }
    }
}
