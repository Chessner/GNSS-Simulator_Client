package at.fh.hgb.mc;

import java.io.*;

public class GNSSSimulator extends BufferedReader {
    private String mFilename;
    private int mSleepDuration;
    private String mFilter;

    public GNSSSimulator(Reader in, int sz) {
        super(in, sz);
    }

    public GNSSSimulator(Reader in) {
        super(in);
    }


    public GNSSSimulator(String _filename, int _sleep, String _filter) throws FileNotFoundException {
        super(new FileReader(_filename));
        mFilename = _filename;
        mSleepDuration = _sleep;
        mFilter = _filter;
    }

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();

        if(line == null) return null;

        if(line.contains(mFilter)){
            try {
                Thread.sleep(mSleepDuration);
            } catch (InterruptedException _e) {
                _e.printStackTrace();
            }
        }
        return line;
    }
}
