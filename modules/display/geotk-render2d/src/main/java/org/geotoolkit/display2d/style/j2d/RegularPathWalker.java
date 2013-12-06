

package org.geotoolkit.display2d.style.j2d;

import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display.shape.ShapeUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RegularPathWalker {


    private final List<Point2D> points = new ArrayList<>();
    private final double resolution;

    private final Line2D.Double line = new Line2D.Double();

    public RegularPathWalker(final PathIterator pathIterator, double resolution) {
        this.resolution = resolution;

        final double[] currentPoint = new double[6];
        final double[] lastPoint = new double[6];
        double segmentStartX;
        double segmentStartY;
        double segmentEndX;
        double segmentEndY;
        double lastmoveToX = 0;
        double lastmoveToY = 0;

        while (!pathIterator.isDone()) {
            final int type = pathIterator.currentSegment(currentPoint);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    System.arraycopy(currentPoint, 0, lastPoint, 0, 6);
                    segmentStartX = lastPoint[0];
                    segmentStartY = lastPoint[1];
                    segmentEndX = currentPoint[0];
                    segmentEndY = currentPoint[1];
                    //keep point for close instruction
                    lastmoveToX = currentPoint[0];
                    lastmoveToY = currentPoint[1];

                    points.add(new Point2D.Double(currentPoint[0], currentPoint[1]));
                    break;

                case PathIterator.SEG_CLOSE:
                    currentPoint[0] = lastmoveToX;
                    currentPoint[1] = lastmoveToY;
                    // Fall into....

                case PathIterator.SEG_LINETO:
                    segmentStartX = lastPoint[0];
                    segmentStartY = lastPoint[1];
                    segmentEndX = currentPoint[0];
                    segmentEndY = currentPoint[1];

                    line.x1 = segmentStartX;
                    line.y1 = segmentStartY;
                    line.x2 = segmentEndX;
                    line.y2 = segmentEndY;

                    addValuesOn(line, (Point2D)points.get(points.size()-1).clone());
                    break;
            }
            System.arraycopy(currentPoint, 0, lastPoint, 0, 6);
            pathIterator.next();
        }

    }

    public boolean isFinished() {
        return points.size() < 2;
    }

    public Line2D.Double next(){
        final Point2D p1 = points.get(0);
        final Point2D p2 = points.get(1);
        line.setLine(p1, p2);
        points.remove(0);
        return line;
    }

    public void addValuesOn(final Line2D.Double line, final Point2D closePoint) {
        double distance = resolution;
        Point2D position;
        while ((position = ShapeUtilities.colinearPoint(line, closePoint, distance)) != null) {
            points.add(position);
            line.x1 = position.getX();
            line.y1 = position.getY();
            closePoint.setLocation(position);
            distance = resolution;
        }
        //return distance - previous.distance(line.getP2());
    }

}
