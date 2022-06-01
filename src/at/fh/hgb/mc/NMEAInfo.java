package at.fh.hgb.mc;


import java.util.ArrayList;

/**
 * Data class for NMEA information.
 */
public class NMEAInfo {
    /**
     * List containing the information about all in this block received satellites.
     */
    public ArrayList<SatelliteInfo> mSatellites = new ArrayList<>();
    /**
     * List containing the ids of satellites, that were used in the calculation of the position of the user.
     */
    public ArrayList<Integer> mIDsSatellitesUsed = new ArrayList<>();
    /**
     * Current longitude of the user.
     */
    public double mLongitude;
    /**
     * Current latitude of the user.
     */
    public double mLatitude;
    /**
     * Current PDOP.
     */
    public double mPDOP;
    /**
     * Current HDOP.
     */
    public double mHDOP;
    /**
     * Current VDOP.
     */
    public double mVDOP;
    /**
     * Current fix quality.
     */
    public double mQuality;
    /**
     * Current height of the user above the geoid.
     */
    public double mHeight;
    /**
     * String representation of the current time.
     * Format: 00(hours)00(minutes)00(seconds).000(milliseconds) -> 000000.000
     */
    public String mTime;

    /**
     * Standard toString() implementation.
     * @return String representing this object.
     */
    @Override
    public String toString() {
        return "NMEAInfo{" +
                "mSatellites=" + mSatellites +
                ", mIDsSatellitesUsed=" + mIDsSatellitesUsed +
                ", mLongitude=" + mLongitude +
                ", mLatitude=" + mLatitude +
                ", mPDOP=" + mPDOP +
                ", mHDOP=" + mHDOP +
                ", mVDOP=" + mVDOP +
                ", mQuality=" + mQuality +
                ", mHeight=" + mHeight +
                '}';
    }
}
