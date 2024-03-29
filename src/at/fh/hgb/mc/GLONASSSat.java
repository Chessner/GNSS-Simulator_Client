package at.fh.hgb.mc;


import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.geom.Point2D;

/**
 * This class extends SatelliteInfo to provide implementation for a GLONASS satellite.
 */
public class GLONASSSat extends SatelliteInfo{
    @Override
    public void createShape(Pane _parent, double _referenceX, double _referenceY, double _radius) {
        Point2D.Double point = getCoordinates(_radius);
        Rectangle rectangle = new Rectangle();
        rectangle.setId("Satellite");
        StringBuilder recStyle = new StringBuilder();

        if (mSNRdB == Integer.MIN_VALUE) {
            //no snr value exists (marked with Integer.MIN_VALUE)
            recStyle.append("-fx-fill: red; ");
        } else if (mParentNMEAInfo.mIDsSatellitesUsed.contains(mID)) {
            //satellite was used
            recStyle.append("-fx-fill: green; ");
        } else {
            //satellite was not used
            recStyle.append("-fx-fill: #0000");
            recStyle.append(Integer.toHexString((int) Math.max(255, Math.min(0, mSNRdB))));
            recStyle.append("ff; ");
        }
        recStyle.append("-fx-stroke: white");

        rectangle.setX(_referenceX + point.x - 10);
        rectangle.setY(_referenceY + point.y - 10);
        rectangle.setWidth(20);
        rectangle.setHeight(20);
        rectangle.setArcHeight(10);
        rectangle.setArcWidth(10);

        String sID = String.valueOf(mID);
        Text text = new Text();
        text.setId("Satellite");
        text.setX(_referenceX + point.x - sID.length()*3);
        text.setY(_referenceY + point.y);
        text.setText(sID);
        text.setStyle("-fx-stroke: white;");
        text.setTextOrigin(VPos.CENTER);

        rectangle.setStyle(recStyle.toString());
        _parent.getChildren().addAll(rectangle, text);
    }
}
