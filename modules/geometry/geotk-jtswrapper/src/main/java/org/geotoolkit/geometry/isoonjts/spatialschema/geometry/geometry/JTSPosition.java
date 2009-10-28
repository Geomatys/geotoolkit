/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/geometry/PositionImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Point;


/**
 * A union type consisting of either a {@linkplain DirectPosition direct position} or of a
 * reference to a {@linkplain Point point} from which a {@linkplain DirectPosition direct
 * position} shall be obtained. The use of this data type allows the identification of a
 * position either directly as a coordinate (variant direct) or indirectly as a reference
 * to a {@linkplain Point point} (variant indirect).
 *  
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 * @module pending
 */
public class JTSPosition implements Position {
        
    private DirectPosition position;
        
    public JTSPosition(final DirectPosition position) {
        this.position = position;
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition getDirectPosition() {
        return position;
    }
}
