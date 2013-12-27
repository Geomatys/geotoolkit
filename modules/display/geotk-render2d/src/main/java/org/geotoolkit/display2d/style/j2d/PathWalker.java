/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.style.j2d;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PathWalker {

    private final PathIterator pathIterator;
    private final float lastPoint[] = new float[6];
    private final float currentPoint[] = new float[6];
    private float lastmoveToX = 0f;
    private float lastmoveToY = 0f;
    private float segmentStartX = 0f;
    private float segmentStartY = 0f;
    private float segmentEndX = 0f;
    private float segmentEndY = 0f;
    private float segmentLenght = 0f;
    private float remaining = 0f;
    private float angle = Float.NaN;
    private boolean finished = false;

    public PathWalker(final PathIterator iterator) {
        this.pathIterator = iterator;

        //get the first segment
        boolean first = true;
        while (first && !pathIterator.isDone()) {
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

                    segmentLenght = distance(segmentStartX, segmentStartY, segmentEndX, segmentEndY);
                    angle = Float.NaN;
                    remaining = segmentLenght;
                    first = false;
                    break;
            }
            System.arraycopy(currentPoint, 0, lastPoint, 0, 6);
            pathIterator.next();
        }

    }

    public boolean isFinished() {
        return finished; //|| (pathIterator.isDone() && remaining <= 0);
    }

    /**
     * Get the remaining distance until the current line segment end.
     * @return float
     */
    public float getSegmentLengthRemaining(){
        return remaining;
    }

    public void walk(float distance) {

        if (remaining > distance) {
            remaining -= distance;
        } else {
            distance -= remaining;
            remaining = 0;

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

                        segmentLenght = distance(segmentStartX, segmentStartY, segmentEndX, segmentEndY);
                        angle = Float.NaN;
                        remaining = segmentLenght;
                        break;
                }
                System.arraycopy(currentPoint, 0, lastPoint, 0, 6);
                pathIterator.next();

                if (remaining >= distance) {
                    remaining -= distance;
                    distance = 0;
                    return;
                } else {
                    distance -= remaining;
                    remaining = 0;
                }

            }

            //if we reach here, it means the iterator is finished and nothing left
            finished = true;
        }

    }

    public Point2D getPosition(final Point2D pt) {
        final double perc = 1d - remaining / segmentLenght;
        final double tlX = (segmentEndX - segmentStartX) * perc + segmentStartX;
        final double tlY = (segmentEndY - segmentStartY) * perc + segmentStartY;

        if(pt == null){
            return new Point2D.Double(tlX, tlY);
        }else{
            pt.setLocation(tlX, tlY);
            return pt;
        }
    }

    public float getRotation() {
        if(Float.isNaN(angle)){
            angle = angle(segmentStartX, segmentStartY, segmentEndX, segmentEndY);
        }
        return angle;
    }

    public static float distance(final float x1, final float y1, final float x2, final float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float angle(final float x1, final float y1, final float x2, final float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.atan2(dy, dx);
    }

}
