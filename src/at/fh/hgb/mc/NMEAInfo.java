package at.fh.hgb.mc;

import java.util.ArrayList;

public class NMEAInfo {
    public ArrayList<SatelliteInfo> mSatellites = new ArrayList<>();
    public ArrayList<Integer> mIDsSatellitesUsed = new ArrayList<>();
    public double mLongitude, mLatitude, mPDOP, mHDOP, mVDOP, mQuality, mHeight;
    public String mTime;

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
