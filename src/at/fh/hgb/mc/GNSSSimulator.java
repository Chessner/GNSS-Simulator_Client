package at.fh.hgb.mc;


import java.io.*;

/**
 * This class simulates the connection to a GNSS by reading lines from specified file.
 */
public class GNSSSimulator extends BufferedReader {
    /**
     * Duration in ms the Thread should sleep after reading a line containing the filter.
     */
    private int mSleepDuration;
    /**
     * Filter signalising a new data block.
     */
    private String mFilter;

    /**
     * Creates a buffering character-input stream that uses an input buffer of the specified size.
     *
     * @param _in A Reader
     * @param _sz Input-buffer size
     */
    public GNSSSimulator(Reader _in, int _sz) {
        super(_in, _sz);
    }

    /**
     * Creates a buffering character-input stream that uses a default-sized input buffe
     *
     * @param _in A Reader
     */
    public GNSSSimulator(Reader _in) {
        super(_in);
    }

    /**
     * Constructs a new GNSSimulator.
     *
     * @param _filename Path to the file the simulator should read from.
     * @param _sleep    Duration in ms the Thread should sleep after reading a line containing the filter.
     * @param _filter   Filter signalising a new data block.
     * @throws FileNotFoundException if the named file does not exist, is a directory rather than a regular file,
     *                               or for some other reason cannot be opened for reading.
     */
    public GNSSSimulator(String _filename, int _sleep, String _filter) throws FileNotFoundException {
        super(new FileReader(_filename));
        mSleepDuration = _sleep;
        mFilter = _filter;
    }

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();

        if (line == null) return null;

        if (line.contains(mFilter)) {
            try {
                Thread.sleep(mSleepDuration);
            } catch (InterruptedException _e) {
                _e.printStackTrace();
            }
        }
        return line;
    }
}
