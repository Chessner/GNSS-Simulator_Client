package at.fh.hgb.mc;

import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.awt.geom.Point2D;

public class GalileoSat extends SatelliteInfo {
    @Override
    public void createShape(Pane _parent, double _referenceX, double _referenceY, double _radius) {
        Point2D.Double point = getCoordinates(_radius);
        Polygon stopSign = new Polygon();
        stopSign.setId("Satellite");
        StringBuilder stopSignStyle = new StringBuilder();

        if (mSNRdB == Integer.MIN_VALUE) {
            //no snr value exists (marked with Integer.MIN_VALUE)
            stopSignStyle.append("-fx-fill: red; ");
        } else if (mParentNMEAInfo.mIDsSatellitesUsed.contains(mID)) {
            //satellite was used
            stopSignStyle.append("-fx-fill: green; ");
        } else {
            //satellite was not used
            stopSignStyle.append("-fx-fill: #0000");
            stopSignStyle.append(Integer.toHexString((int) Math.max(255, Math.min(0, mSNRdB))));
            stopSignStyle.append("ff; ");
        }
        stopSignStyle.append("-fx-stroke: white");

        stopSign.getPoints().addAll(_referenceX + point.x - 10, _referenceY + point.y - 5,
                _referenceX + point.x - 5, _referenceY + point.y - 10,
                _referenceX + point.x + 5, _referenceY + point.y - 10,
                _referenceX + point.x + 10, _referenceY + point.y - 5,
                _referenceX + point.x + 10, _referenceY + point.y + 5,
                _referenceX + point.x + 5, _referenceY + point.y + 10,
                _referenceX + point.x - 5, _referenceY + point.y + 10,
                _referenceX + point.x - 10, _referenceY + point.y + 5);

        String sID = String.valueOf(mID);
        Text text = new Text();
        text.setId("Satellite");
        text.setX(_referenceX + point.x - sID.length() * 3);
        text.setY(_referenceY + point.y);
        text.setText(sID);
        text.setStyle("-fx-stroke: white;");
        text.setTextOrigin(VPos.CENTER);

        stopSign.setStyle(stopSignStyle.toString());
        _parent.getChildren().addAll(stopSign, text);
    }
}
