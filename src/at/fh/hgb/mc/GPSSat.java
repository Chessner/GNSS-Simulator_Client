package at.fh.hgb.mc;

import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.awt.*;
import java.awt.geom.Point2D;

public class GPSSat extends SatelliteInfo {


    public void draw(Graphics2D _g2d, double _referenceX, double _referenceY, double _radius) {
        Point2D.Double point = getCoordinates(_radius);
        if (mSNRdB == Integer.MIN_VALUE) {
            //no snr value exists (marked with Integer.MIN_VALUE)
            _g2d.setColor(Color.red);
        } else if (mParentNMEAInfo.mIDsSatellitesUsed.contains(mID)) {
            //satellite was used
            _g2d.setColor(new Color(8, 147, 18));
        } else {
            //satellite was not used
            _g2d.setColor(new Color(0, 0, (int) Math.max(0, Math.min(mSNRdB, 255))));
        }
        _g2d.fillOval((int) (_referenceX + point.x - 10), (int) (_referenceY + point.y - 10), 20, 20);
        _g2d.setColor(Color.WHITE);
        _g2d.drawOval((int) (_referenceX + point.x - 10), (int) (_referenceY + point.y - 10), 20, 20);

        int sOffset;
        if (String.valueOf(mID).length() > 1) {
            sOffset = 6;
        } else {
            sOffset = 3;
        }
        _g2d.drawString(String.valueOf(mID), (int) (_referenceX + point.x - sOffset), (int) (_referenceY + point.y + 5));
    }

    @Override
    public void createShape(Pane _parent, double _referenceX, double _referenceY, double _radius) {
        Point2D.Double point = getCoordinates(_radius);
        Circle circle = new Circle();
        StringBuilder circleStyle = new StringBuilder();

        if (mSNRdB == Integer.MIN_VALUE) {
            //no snr value exists (marked with Integer.MIN_VALUE)
            circleStyle.append("-fx-fill: red; ");
        } else if (mParentNMEAInfo.mIDsSatellitesUsed.contains(mID)) {
            //satellite was used
            circleStyle.append("-fx-fill: green; ");
        } else {
            //satellite was not used
            circleStyle.append("-fx-fill: #0000");
            circleStyle.append(Integer.toHexString((int) Math.max(255, Math.min(0, mSNRdB))));
            circleStyle.append("ff; ");
        }
        circleStyle.append("-fx-stroke: white");

        circle.setRadius(10);
        circle.setCenterX(_referenceX + point.x);
        circle.setCenterY(_referenceY + point.y);

        String sID = String.valueOf(mID);
        Text text = new Text();
        text.setX(_referenceX + point.x - sID.length()*3);
        text.setY(_referenceY + point.y);
        text.setText(sID);
        text.setStyle("-fx-stroke: white;");
        text.setTextOrigin(VPos.CENTER);

        circle.setStyle(circleStyle.toString());
        _parent.getChildren().addAll(circle, text);
    }
}
