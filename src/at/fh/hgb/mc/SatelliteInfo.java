package at.fh.hgb.mc;

import javafx.scene.layout.Pane;

import java.awt.geom.Point2D;

public abstract class SatelliteInfo {
    public int mID;
    public double mSNRdB;
    public double mAngleToHorizontal, mAngleToNorth;
    protected NMEAInfo mParentNMEAInfo;

    public Point2D.Double getCoordinates(double _radius){
        double r = Math.cos(mAngleToHorizontal * (Math.PI / 180d)) * _radius;
        double x = r * Math.cos((mAngleToNorth - 90) * (Math.PI / 180d));
        double y = r * Math.sin((mAngleToNorth - 90) * (Math.PI / 180d));
        return new Point2D.Double(x,y);
    }

    public abstract void createShape(Pane _parent, double _referenceX, double _referenceY, double _radius);

    @Override
    public String toString() {
        return "SatelliteInfo{" +
                "mID=" + mID +
                ", mSNRdB=" + mSNRdB +
                ", mAngleToHorizontal=" + mAngleToHorizontal +
                ", mAngleToNorth=" + mAngleToNorth +
                "}\n";
    }
}
