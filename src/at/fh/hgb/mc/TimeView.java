package at.fh.hgb.mc;

import javafx.scene.control.Label;

public class TimeView implements PositionUpdateListener {
    public final String TIME_ID = "TIME_ID";
    private GlobalView mGlobalView;

    public TimeView(GlobalView _globalView) {
        mGlobalView = _globalView;
    }

    public Label init() {
        Label time = new Label("00:00:00");
        time.setStyle("-fx-font-size: 24; -fx-text-fill: black;");
        time.setId(TIME_ID);
        return time;
    }

    @Override
    public void update(NMEAInfo _info) {
        Label time = (Label) mGlobalView.mScene.lookup("#" + TIME_ID);

        if (_info.mTime == null) return;

        if(_info.mTime.split("\\.")[0].length() < 6) return;

        String displayTime = _info.mTime.substring(0,2) + ":" + _info.mTime.substring(2,4) + ":" + _info.mTime.substring(4,6);
        time.setText(displayTime);
    }
}
