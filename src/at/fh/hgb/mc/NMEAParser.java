package at.fh.hgb.mc;

import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

public class NMEAParser implements Runnable {
    private GNSSSimulator mSimulator;
    private NMEAInfo mDisplayInfo, mReceiveInfo;
    private SatelliteInfo mCurrentSat;
    private ArrayList<PositionUpdateListener> mPositionUpdateListeners = new ArrayList<>();

    public NMEAParser() {
        try {
            mSimulator = new GNSSSimulator("GPS-Logs/NMEA-data-3--Materl-Position-Statisch.nmea", 1000, "GGA");
        } catch (IOException _e) {
            _e.printStackTrace();
        }
    }

    public void parse(String _data) {
        String[] dataParts = _data.split(",|\\*");
        String type = dataParts[0].substring(3, 6);

        switch (type) {
            case "GGA": {
                mDisplayInfo = mReceiveInfo;

                if (mDisplayInfo != null) updatePositionUpdateListeners();

                mReceiveInfo = new NMEAInfo();

                String time = dataParts[1];
                if (!time.equals("")) {
                    mReceiveInfo.mTime = time;
                } else{
                    mReceiveInfo.mTime = "0.0";
                }

                String lat = dataParts[2];
                if (!lat.equals("")) {
                    String latDegree = lat.substring(0, 2);
                    String latMinutes = lat.substring(2);
                    double latDegDouble = Double.parseDouble(latDegree);
                    double latMinDouble = Double.parseDouble(latMinutes);

                    mReceiveInfo.mLatitude = latDegDouble + latMinDouble / 60;
                }

                String lon = dataParts[4];
                if (!lon.equals("")) {
                    String longDegree = lon.substring(0, 3);
                    String longMinutes = lon.substring(3);
                    double longDegDouble = Double.parseDouble(longDegree);
                    double longMinDouble = Double.parseDouble(longMinutes);

                    mReceiveInfo.mLongitude = longDegDouble + longMinDouble / 60;
                }

                String qual = dataParts[6];
                if (!qual.equals("")) {
                    mReceiveInfo.mQuality = Integer.parseInt(qual);
                }
                String high = dataParts[9];
                if (!high.equals("")) {
                    mReceiveInfo.mHeight = Double.parseDouble(high);
                }

            }
            break;
            case "GSA": {
                if (mReceiveInfo == null) return;

                for (int i = 3; i < 15; i++) {
                    if (dataParts[i].equals("")) break;

                    int id = Integer.parseInt(dataParts[i]);
                    mReceiveInfo.mIDsSatellitesUsed.add(id);
                }

                if (!dataParts[15].equals("")) {
                    mReceiveInfo.mPDOP = Double.parseDouble(dataParts[15]);
                }
                if (!dataParts[16].equals("")) {
                    mReceiveInfo.mHDOP = Double.parseDouble(dataParts[16]);
                }
                if (!dataParts[17].equals("")) {
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
                            //System.out.println("Satellite of type: " + dataParts[0].substring(1,3) + " cannot be processed by this program!");
                            return;
                        }
                        mCurrentSat.mParentNMEAInfo = mReceiveInfo;

                        if (!dataParts[i].equals("")) {
                            mCurrentSat.mID = Integer.parseInt(dataParts[i]);
                        }
                    } else if (j % 5 == 0) {
                        //mAngleToHorizontal
                        if (!dataParts[i].equals("")) {
                            mCurrentSat.mAngleToHorizontal = Double.parseDouble(dataParts[i]);
                        }
                    } else if (j % 6 == 0) {
                        //mAngleToNorth
                        if (!dataParts[i].equals("")) {
                            mCurrentSat.mAngleToNorth = Double.parseDouble(dataParts[i]);
                        }
                    } else {
                        //SNR
                        if (!dataParts[i].equals("")) {
                            mCurrentSat.mSNRdB = Double.parseDouble(dataParts[i]);
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
                // System.out.println("no fitting data type could be found for: " + type);
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

                parse(line);

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
