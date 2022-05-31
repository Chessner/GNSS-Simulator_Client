package at.fh.hgb.mc;

import javafx.scene.control.Label;

/**
 * This class handles and presents the current time.
 * It also is a mixture between all three parts of the MVC pattern, meaning it not only creates the view,
 * but also handles changes to it and the data.
 */
public class TimeView implements PositionUpdateListener {
    /**
     * Unique id defining the label containing the current time.
     */
    public final String TIME_ID = "TIME_ID";
    /**
     * Reference to the parent GlobalView containing this view.
     */
    private final GlobalView mGlobalView;

    /**
     * Constructor for a new TimeView.
     * @param _globalView Reference to the parent GlobalView containing this view.
     */
    public TimeView(GlobalView _globalView) {
        mGlobalView = _globalView;
    }
    /**
     * Initializer for this view, setting up the javafx elements.
     * @return Label containing the current time.
     */
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
