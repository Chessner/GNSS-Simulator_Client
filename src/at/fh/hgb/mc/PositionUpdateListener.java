package at.fh.hgb.mc;

/**
 * A class can implement this interface to listen for changes in the observed object.
 */
public interface PositionUpdateListener {
    /**
     * This method is called whenever the observed object changes.
     *
     * @param _info NMEAInfo object containing relevant data.
     * @see NMEAInfo
     */
    void update(NMEAInfo _info);
}
