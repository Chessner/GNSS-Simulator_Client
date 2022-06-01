package at.fh.hgb.mc;

import javafx.scene.layout.Pane;
import java.awt.geom.Point2D;

/**
 * This class provides storage for the values defining a satellite.
 */
public abstract class SatelliteInfo {
    /**
     * Unique id for this satellite.
     */
    public int mID;
    /**
     * SNR value in dB of the connection to this satellite.
     * Marked with (double)Integer.MIN_VALUE if no SNR value exists.
     */
    public int mSNRdB;
    /**
     * Angles in degree to the specified line.
     */
    public int mAngleToHorizontalLine, mAngleToNorthLine;
    /**
     * Reference to the NMEAInfo containing this satellite.
     */
    protected NMEAInfo mParentNMEAInfo;

    /**
     * Calculates the coordinates of this satellite from the stored angles and the given radius.
     * @param _radius Value specifying the maximum distance the coordinates can be from (0,0).
     * @return A point containing the calculated coordinates.
     */
    public Point2D.Double getCoordinates(double _radius){
        double r = Math.cos(mAngleToHorizontalLine * (Math.PI / 180d)) * _radius;
        double x = r * Math.cos(Math.toRadians(mAngleToNorthLine - 90));
        double y = r * Math.sin(Math.toRadians(mAngleToNorthLine - 90));
        return new Point2D.Double(x,y);
    }

    /**
     * A satellite can create a Shape to put on the given Pane.
     * @param _parent Pane to put the created Shape on.
     * @param _referenceX Reference value the implementing object should use as 0 for the x-position.
     * @param _referenceY Reference value the implementing object should use as 0 for the y-position.
     * @param _radius Value specifying the maximum distance the coordinates can be from (0,0). (Used for calculating the coordinates)
     */
    public abstract void createShape(Pane _parent, double _referenceX, double _referenceY, double _radius);

    /**
     * Standard toString() implementation.
     * @return String representing this object.
     */
    @Override
    public String toString() {
        return "SatelliteInfo{" +
                "mID=" + mID +
                ", mSNRdB=" + mSNRdB +
                ", mAngleToHorizontal=" + mAngleToHorizontalLine +
                ", mAngleToNorth=" + mAngleToNorthLine +
                "}\n";
    }
}
