package org.geotoolkit.data.mif.geometry;

import org.geotoolkit.feature.DefaultName;

/**
 * A class to build feature from MIF ellipse object.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFEllipseBuilder extends MIFRectangleBuilder {

    public MIFEllipseBuilder() {
        NAME = new DefaultName("ELLIPSE");
    }
}
