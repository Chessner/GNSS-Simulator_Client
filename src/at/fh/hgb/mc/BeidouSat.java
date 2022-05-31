package at.fh.hgb.mc;

import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.awt.geom.Point2D;

/**
 * This class extends SatelliteInfo to provide implementation for a Beidou satellite.
 */
public class BeidouSat extends SatelliteInfo{
    @Override
    public void createShape(Pane _parent, double _referenceX, double _referenceY, double _radius) {
        Point2D.Double point = getCoordinates(_radius);
        Polygon triangle = new Polygon();
        triangle.setId("Satellite");
        StringBuilder triangleStyle = new StringBuilder();

        if (mSNRdB == Integer.MIN_VALUE) {
            //no snr value exists (marked with Integer.MIN_VALUE)
            triangleStyle.append("-fx-fill: red; ");
        } else if (mParentNMEAInfo.mIDsSatellitesUsed.contains(mID)) {
            //satellite was used
            triangleStyle.append("-fx-fill: green; ");
        } else {
            //satellite was not used
            triangleStyle.append("-fx-fill: #0000");
            triangleStyle.append(Integer.toHexString((int) Math.max(255, Math.min(0, mSNRdB))));
            triangleStyle.append("ff; ");
        }
        triangleStyle.append("-fx-stroke: white");

        double ankathete = 15; // Seiten beziehen sich nicht auf das ganze gleichseitige Dreieck, sondern auf ein inneres kleineres
        double gegenkathete = Math.tan(Math.toRadians(30)) * ankathete;
        double hypotenuse = Math.sqrt(Math.pow(10,2)+Math.pow(gegenkathete,2));
        double x = _referenceX + point.x;
        double y = _referenceY + point.y;

        triangle.getPoints().addAll(x,y - hypotenuse, //top corner
                x + ankathete, y + gegenkathete, //bottom right
                x - ankathete, y + gegenkathete); //bottom left

        String sID = String.valueOf(mID);
        Text text = new Text();
        text.setId("Satellite");
        text.setX(_referenceX + point.x - sID.length()*3);
        text.setY(_referenceY + point.y);
        text.setText(sID);
        text.setStyle("-fx-stroke: white;");
        text.setTextOrigin(VPos.CENTER);

        triangle.setStyle(triangleStyle.toString());
        _parent.getChildren().addAll(triangle, text);
    }
}
