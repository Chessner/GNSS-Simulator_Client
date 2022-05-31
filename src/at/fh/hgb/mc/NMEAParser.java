package at.fh.hgb.mc;

import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

public class NMEAParser implements Runnable {
    private GNSSSimulator mSimulator;
    private NMEAInfo mDisplayInfo, mReceiveInfo;
    private SatelliteInfo mCurrentSat;
    private final ArrayList<PositionUpdateListener> mPositionUpdateListeners = new ArrayList<>();
    private final String REGEX_FOR_DOUBLE = "[0-9]+\\.[0-9]+";
    private final String REGEX_FOR_TIME = "[0-9]{6,6}.[0-9]{2,2}";
    private final String REGEX_FOR_INTEGER = "-?[0-9]+";
    private final String REGEX_FOR_HEX = "[0-9,A-F][0-9,A-F]";

    public NMEAParser() {
        try {
            mSimulator = new GNSSSimulator("GPS-Logs/NMEA-data-4--Fehlerhaft.nmea", 1000, "GGA");
        } catch (IOException _e) {
            _e.printStackTrace();
        }
    }

    // Method to convert hexadecimal to decimal
    static int hexadecimalToDecimal(String _hexVal) {
        // Storing the length of the
        int len = _hexVal.length();

        // Initializing base value to 1, i.e 16^0
        int base = 1;

        // Initially declaring and initializing decimal value to zero
        int decVal = 0;

        // Extracting characters as digits from last character
        for (int i = len - 1; i >= 0; i--) {

            if (_hexVal.charAt(i) >= '0'
                    && _hexVal.charAt(i) <= '9') {
                decVal += (_hexVal.charAt(i) - 48) * base;

                // Incrementing base by power
                base = base * 16;
            } else if (_hexVal.charAt(i) >= 'A'
                    && _hexVal.charAt(i) <= 'F') {
                decVal += (_hexVal.charAt(i) - 55) * base;

                // Incrementing base by power
                base = base * 16;
            }
        }

        // Returning the decimal value
        return decVal;
    }

    private int calcCheckSum(String _data) {
        char[] cArr = _data.toCharArray();
        int calcCheckSum = 0;
        for (char c : cArr) {
            calcCheckSum ^= c;
        }
        return calcCheckSum;
    }


    public void parse(String _data) {
        String[] dataParts = _data.split(",|\\*");


        //Check checksum
        if (dataParts[dataParts.length - 1].matches(REGEX_FOR_HEX)) {

            StringBuilder stringBuilder = new StringBuilder();
            String s = dataParts[0];
            stringBuilder.append(s.replace("$", ""));
            for (int i = 1; i < dataParts.length - 1; i++) {
                stringBuilder.append(dataParts[i]);
            }
            int calcCheckSum = calcCheckSum(stringBuilder.toString());

            int receivedCheckSum = hexadecimalToDecimal(dataParts[dataParts.length - 1]);

            if (receivedCheckSum != calcCheckSum) {
                return;
            }
        } else {
            return;
        }


        String type = dataParts[0].substring(3, 6);

        switch (type) {
            case "GGA": {
                if (dataParts.length < 16) return; //14 data fields + sentence type + checksum

                if (mReceiveInfo != null) {
                    mDisplayInfo = mReceiveInfo;
                    updatePositionUpdateListeners();
                }

                mReceiveInfo = new NMEAInfo();

                String time = dataParts[1];
                if (time.matches(REGEX_FOR_TIME)) {
                    mReceiveInfo.mTime = time;
                } else {
                    mReceiveInfo.mTime = "000000.00";
                }
                String lat = dataParts[2];
                if (lat.matches(REGEX_FOR_DOUBLE)) {
                    String latDegree = lat.substring(0, 2);
                    String latMinutes = lat.substring(2);
                    double latDegDouble = Double.parseDouble(latDegree);
                    double latMinDouble = Double.parseDouble(latMinutes);
                    mReceiveInfo.mLatitude = Math.min(latDegDouble + latMinDouble / 60, 90.0d);
                }

                String lon = dataParts[4];
                if (lon.matches(REGEX_FOR_DOUBLE)) {
                    String longDegree = lon.substring(0, 3);
                    String longMinutes = lon.substring(3);
                    double longDegDouble = Double.parseDouble(longDegree);
                    double longMinDouble = Double.parseDouble(longMinutes);

                    mReceiveInfo.mLongitude = Math.min(longDegDouble + longMinDouble / 60, 360.0d);
                }

                String qual = dataParts[6];
                if (qual.matches("[0-2]")) {
                    mReceiveInfo.mQuality = Integer.parseInt(qual);
                }

                String high = dataParts[9];
                if (high.matches(REGEX_FOR_DOUBLE)) {
                    mReceiveInfo.mHeight = Double.parseDouble(high);
                }

            }
            break;
            case "GSA": {
                if (dataParts.length < 19) return; //17 data fields + sentence type + checksum

                if (mReceiveInfo == null) return;

                for (int i = 3; i < 15; i++) {
                    if (dataParts[i].equals("")) break;

                    int id = Integer.parseInt(dataParts[i]);
                    mReceiveInfo.mIDsSatellitesUsed.add(id);
                }

                if (dataParts[15].matches(REGEX_FOR_DOUBLE)) {
                    mReceiveInfo.mPDOP = Double.parseDouble(dataParts[15]);
                }
                if (dataParts[16].matches(REGEX_FOR_DOUBLE)) {
                    mReceiveInfo.mHDOP = Double.parseDouble(dataParts[16]);
                }
                if (dataParts[17].matches(REGEX_FOR_DOUBLE)) {
                    mReceiveInfo.mVDOP = Double.parseDouble(dataParts[17]);
                }
            }
            break;
            case "GSV": {
                if (mReceiveInfo == null) return;

                for (int i = 4, j = 4; i < dataParts.length - 1; j++, i++) {
                    if (j % 4 == 0) {
                        //id
                        if (dataParts[0].contains("GP")) {
                            mCurrentSat = new GPSSat();
                        } else if (dataParts[0].contains("GL")) {
                            mCurrentSat = new GLONASSSat();
                        } else if (dataParts[0].contains("GA")) {
                            mCurrentSat = new GalileoSat();
                        } else if (dataParts[0].contains("BD")) {
                            mCurrentSat = new BeidouSat();
                        } else {
                            System.out.println("Satellite of type: " + dataParts[0].substring(1, 3) + " cannot be processed by this program!");
                            return;
                        }
                        mCurrentSat.mParentNMEAInfo = mReceiveInfo;

                        if (dataParts[i].matches(REGEX_FOR_INTEGER)) {
                            mCurrentSat.mID = Integer.parseInt(dataParts[i]);
                        }
                    } else if (j % 5 == 0) {
                        //mAngleToHorizontal
                        if (dataParts[i].matches(REGEX_FOR_INTEGER)) {
                            mCurrentSat.mAngleToHorizontalLine = Integer.parseInt(dataParts[i]);
                        }
                    } else if (j % 6 == 0) {
                        //mAngleToNorth
                        if (dataParts[i].matches(REGEX_FOR_INTEGER)) {
                            mCurrentSat.mAngleToNorthLine = Integer.parseInt(dataParts[i]);
                        }
                    } else {
                        //SNR
                        if (dataParts[i].matches(REGEX_FOR_INTEGER)) {
                            mCurrentSat.mSNRdB = Integer.parseInt(dataParts[i]);
                        } else {
                            //no snr -> mark with Integer.MIN_VALUE
                            mCurrentSat.mSNRdB = Integer.MIN_VALUE;
                        }
                        mReceiveInfo.mSatellites.add(mCurrentSat);
                        j = 3;
                    }
                }
            }
            default:
                System.out.println("no fitting data type could be found for: " + type);
                break;
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while (true) {
                line = mSimulator.readLine();
                if (line == null) break;

                String[] d = line.split("\\$");
                if (d.length > 2) {
                    for (int i = 1; i < d.length; i++) {
                        parse("$" + d[i]);
                    }
                } else {
                    parse(line);
                }

            }
        } catch (IOException _e) {
            _e.printStackTrace();
        }
    }

    public void addPositionUpdateListener(PositionUpdateListener _listener) {
        mPositionUpdateListeners.add(_listener);
    }

    private void updatePositionUpdateListeners() {
        Platform.runLater(() -> {
            for (PositionUpdateListener l : mPositionUpdateListeners) {
                l.update(mDisplayInfo);
            }
        });
    }
}
