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
        time.setId(TIME_ID);
        return time;
    }

    @Override
    public void update(NMEAInfo _info) {
        Label time = (Label) mGlobalView.mScene.lookup("#" + TIME_ID);
        String infoTime = Double.toString(_info.mTime);

        String displayTime = infoTime.substring(0,2) + ":" + infoTime.substring(2,4) + ":" + infoTime.substring(4,6);
        time.setText(displayTime);
    }
}
