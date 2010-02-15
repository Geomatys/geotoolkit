/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
public class DefaultPathWalker implements PathWalker{

    private final PathIterator pathIterator;
    private final float lastPoint[] = new float[6];
    private final float currentPoint[] = new float[6];
    private float segmentStartX = 0f;
    private float segmentStartY = 0f;
    private float segmentEndX = 0f;
    private float segmentEndY = 0f;
    private float segmentLenght = 0f;
    private float remaining = 0f;
    private float angle = Float.NaN;
    private boolean finished = false;

    public DefaultPathWalker(PathIterator iterator) {
        this.pathIterator = iterator;

        //get the first segment
        boolean first = true;
        while (!pathIterator.isDone() && first) {
            final int type = pathIterator.currentSegment(currentPoint);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    //copy last point values
                    for (int i = 0; i < 6; i++) {
                        lastPoint[i] = currentPoint[i];
                    }
                    segmentStartX = lastPoint[0];
                    segmentStartY = lastPoint[1];
                    segmentEndX = currentPoint[0];
                    segmentEndY = currentPoint[1];
                    break;

                case PathIterator.SEG_CLOSE:
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

            //copy last point values
            for (int i = 0; i < 6; i++) {
                lastPoint[i] = currentPoint[i];
            }
            pathIterator.next();
        }

    }

    @Override
    public boolean isFinished() {
        return finished; //|| (pathIterator.isDone() && remaining <= 0);
    }

    @Override
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
                        //copy last point values
                        for (int i = 0; i < 6; i++) {
                            lastPoint[i] = currentPoint[i];
                        }
                        segmentStartX = lastPoint[0];
                        segmentStartY = lastPoint[1];
                        segmentEndX = currentPoint[0];
                        segmentEndY = currentPoint[1];
                        break;

                    case PathIterator.SEG_CLOSE:
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

                //copy last point values
                for (int i = 0; i < 6; i++) {
                    lastPoint[i] = currentPoint[i];
                }
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

    @Override
    public Point2D getPosition(Point2D pt) {
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

    @Override
    public float getRotation() {
        if(Float.isNaN(angle)){
            angle = angle(segmentStartX, segmentStartY, segmentEndX, segmentEndY);
        }
        return angle;
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static float angle(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.atan2(dy, dx);
    }

}
