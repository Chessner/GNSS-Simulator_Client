package at.fh.hgb.mc;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class DataView implements PositionUpdateListener{
    private GridPane mDataView;
    private final String LATITUDE_ID = "LATITUDE_ID";
    private final String LONGITUDE_ID = "LONGITUDE_ID";
    private final String PDOP_ID = "PDOP_ID";
    private final String HDOP_ID = "HDOP_ID";
    private final String VDOP_ID = "VDOP_ID";
    private final String ALTITUDE_ID = "ALTITUDE_ID";
    private GlobalView mGlobalView;

    public DataView(GlobalView _view){
        mGlobalView = _view;
    }
    public GridPane init() {
        mDataView = new GridPane();
        mDataView.setHgap(5);
        mDataView.setVgap(3);
        mDataView.setStyle("-fx-background-color: cyan;");

        Label latitudeText = new Label("Latitude");
        latitudeText.setStyle("-fx-font-weight: bold");
        Label latitudeValue = new Label("0,000000");
        latitudeValue.setId(LATITUDE_ID);

        Label longitudeText = new Label("Longitude");
        longitudeText.setStyle("-fx-font-weight: bold");
        Label longitudeValue = new Label("0,000000");
        longitudeValue.setId(LONGITUDE_ID);

        Label pdopText = new Label("PDOP");
        pdopText.setStyle("-fx-font-weight: bold");
        Label pdopValue = new Label("0");
        pdopValue.setId(PDOP_ID);

        Label vdopText = new Label("VDOP");
        vdopText.setStyle("-fx-font-weight: bold");
        Label vdopValue = new Label("0");
        vdopValue.setId(VDOP_ID);

        Label hdopText = new Label("HDOP");
        hdopText.setStyle("-fx-font-weight: bold");
        Label hdopValue = new Label("0");
        hdopValue.setId(HDOP_ID);

        Label altitudeText = new Label("Altitude");
        altitudeText.setStyle("-fx-font-weight: bold");
        Label altitudeValue = new Label("0");
        altitudeValue.setId(ALTITUDE_ID);

        mDataView.getChildren().addAll(latitudeText, latitudeValue, longitudeText, longitudeValue,
                pdopText, pdopValue, vdopText, vdopValue, hdopText, hdopValue, altitudeText, altitudeValue);

        GridPane.setConstraints(latitudeText, 0, 0);
        GridPane.setConstraints(longitudeText, 0, 1);
        GridPane.setConstraints(altitudeText, 0, 2);
        GridPane.setConstraints(latitudeValue, 1, 0);
        GridPane.setConstraints(longitudeValue, 1, 1);
        GridPane.setConstraints(altitudeValue, 1, 2);
        GridPane.setConstraints(pdopText, 2, 0);
        GridPane.setConstraints(hdopText, 2, 1);
        GridPane.setConstraints(vdopText, 2, 2);
        GridPane.setConstraints(pdopValue, 3, 0);
        GridPane.setConstraints(hdopValue, 3, 1);
        GridPane.setConstraints(vdopValue, 3, 2);


        return mDataView;
    }

    @Override
    public void update(NMEAInfo _info) {

        double lat = Math.round(_info.mLatitude*1000000d)/1000000d;
        String latString = Double.toString(lat);
        String[] latStringParts = latString.split("\\.");
        StringBuilder b = new StringBuilder(latStringParts[1]);
        b.append("0".repeat(Math.max(0, 6 - latStringParts[1].length())));
        b.insert(0,latStringParts[0]+",");

        Label latitudeLabel = (Label) mGlobalView.mScene.lookup("#"+ LATITUDE_ID);
        latitudeLabel.setText(b.toString());

        double lon = Math.round(_info.mLongitude*1000000d)/1000000d;
        String lonString = Double.toString(lon);
        String[] lonStringParts = lonString.split("\\.");
        b = new StringBuilder(lonStringParts[1]);
        b.append("0".repeat(Math.max(0, 6 - lonStringParts[1].length())));
        b.insert(0,lonStringParts[0]+",");

        Label longitudeLabel = (Label) mGlobalView.mScene.lookup("#"+ LONGITUDE_ID);
        longitudeLabel.setText(b.toString());

        Label hdop = (Label) mGlobalView.mScene.lookup("#"+ HDOP_ID);
        hdop.setText(Double.toString(_info.mHDOP));

        Label vdop = (Label) mGlobalView.mScene.lookup("#"+ VDOP_ID);
        vdop.setText(Double.toString(_info.mVDOP));

        Label pdop = (Label) mGlobalView.mScene.lookup("#"+ PDOP_ID);
        pdop.setText(Double.toString(_info.mPDOP));

        Label alti = (Label) mGlobalView.mScene.lookup("#"+ ALTITUDE_ID);
        alti.setText(Double.toString(_info.mHeight));
    }
}
