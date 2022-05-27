package at.fh.hgb.mc;

public abstract class SatelliteInfo {
    public int mID;
    public double mSNRdB;
    public double mAngleToHorizontal, mAngleToNorth;

   /* @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("ID: ");
        b.append(mID).append(", SNR(dB): ");
        b.append(mSNRdB);
        b.append(", Angle(Horiz.): ");
        b.append(mAngleToHorizontal);
        b.append(", Angle(North.): ");
        b.append(mAngleToNorth);
        return b.toString();
    }*/

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
