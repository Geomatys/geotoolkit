
package org.geotoolkit.dggs.a5.internal;

import org.geotoolkit.dggs.a5.internal.Hilbert;
import org.geotoolkit.dggs.a5.internal.Tiling;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.sis.geometries.math.Vector2D;
import org.geotoolkit.dggs.a5.internal.Hilbert.Anchor;
import org.geotoolkit.dggs.a5.internal.Hilbert.Orientation;
import org.geotoolkit.dggs.a5.internal.Utils.PentagonShape;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class HierarchyTest {

    private static Vector2D.Double[][] generateCells(int resolution, Orientation orientation) {
        final int[] sequence = IntStream.range(0, (int) Math.pow(4, resolution)).toArray();
        final Anchor[] anchors = IntStream.of(sequence).mapToObj((i) -> Hilbert.sToAnchor(i, resolution, orientation)).toArray(Anchor[]::new);
        return Stream.of(anchors)
                .map((anchor) -> Tiling.getPentagonVertices(resolution, 0, anchor).getVertices())
                .toArray(Vector2D.Double[][]::new);
    }

    private static void verifyHierarchy(int resolution, Orientation orientation) {
        final Vector2D.Double[][] level1Cells = generateCells(resolution, orientation);
        final Vector2D.Double[][] level2Cells = generateCells(resolution + 1, orientation);

        PentagonShape failedPentagon = null;
        Vector2D.Double[] failedChild = null;
        for (int i = 0; i < level2Cells.length; i++) {
            final Vector2D.Double[] child = level2Cells[i];
            final Vector2D.Double[] parent = level1Cells[(int)Math.floor(i / 4)];
            final PentagonShape pentagon = new PentagonShape(parent);
            boolean contained = false;
            for (Vector2D.Double vertex : child) {
                if (pentagon.containsPoint(vertex) < 0) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                failedPentagon = pentagon;
                failedChild = child;
            }
        }
        if (failedPentagon != null && failedChild != null) {
            System.out.println("Pentagon: " + Arrays.toString(failedPentagon.getVertices()));
            System.out.println("did not contain any of:" + Arrays.toString(failedChild));
        }
        assertNull(failedPentagon);
        assertNull(failedChild);
    }

    @Test
    public void testCellHierarchy() {

        final Orientation[] orientations = new Orientation[]{Orientation.uv, Orientation.vu, Orientation.uw, Orientation.wu, Orientation.vw, Orientation.wv};

        for (Orientation orientation : orientations) {
            for (int resolution = 1; resolution <=6; resolution++) {
                verifyHierarchy(resolution, orientation);
            }
        }
    }

}
