package at.fh.hgb.mc;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GlobalView extends Application {
    protected Scene mScene;
    private SatView mSatView;
    private DeviationView mDevView;
    private DataView mDataView;
    private NMEAParser mParser;
    private TimeView mTimeView;

    public static void main(String[] _args) {
        launch(_args);
    }

    @Override
    public void start(Stage _stage) throws Exception {
        BorderPane root = new BorderPane();
        mScene = new Scene(root, 640, 480);

        AnchorPane center = new AnchorPane();
        root.setCenter(center);

        mSatView = new SatView(this);
        AnchorPane satView = mSatView.init();

        mDevView = new DeviationView(this);
        StackPane devView = mDevView.init();

        center.getChildren().addAll(satView, devView);

        mDataView = new DataView(this);
        GridPane dataView = mDataView.init();
        root.setBottom(dataView);

        mTimeView = new TimeView(this);
        Label timeView = mTimeView.init();
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #" + Integer.toHexString(189) + Integer.toHexString(182) + Integer.toHexString(106) + "ff");
        stackPane.getChildren().add(timeView);
        root.setTop(stackPane);

        center.widthProperty().addListener(mSatView.getChangeListener());
        center.heightProperty().addListener(mSatView.getChangeListener());
        center.widthProperty().addListener(mDevView.getChangeListener());

        mParser = new NMEAParser();
        mParser.addPositionUpdateListener(mDataView);
        mParser.addPositionUpdateListener(mSatView);
        mParser.addPositionUpdateListener(mDevView);
        mParser.addPositionUpdateListener(mTimeView);

        Thread thread = new Thread(mParser);
        thread.start();
        _stage.setOnCloseRequest(_event -> {
                    try {
                        thread.join(1);
                    } catch (InterruptedException _e) {
                        _e.printStackTrace();
                    }
                    Platform.exit();
                    System.exit(1);
                }
        );
        _stage.setTitle("NMEA-0183 View");
        _stage.setScene(mScene);
        _stage.show();

        mSatView.lateInit();
        mDevView.lateInit();
    }
}
