package at.fh.hgb.mc;

import java.awt.*;
import java.awt.geom.Point2D;

public class GPSSat extends SatelliteInfo{


    @Override
    public void draw(Graphics2D _g2d, double _referenceX, double _referenceY, double _radius) {
        Point2D.Double point = getCoordinates(_radius);

        _g2d.setColor(new Color(8,147,18));
        _g2d.fillOval((int) (_referenceX + point.x - 10), (int) (_referenceY + point.y - 10), 20, 20);
        _g2d.setColor(Color.WHITE);
        _g2d.drawOval((int) (_referenceX + point.x - 10), (int) (_referenceY + point.y - 10), 20, 20);
    }
}
