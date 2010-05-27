package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Anglepos180 type:</p>
 *
 * <br />&lt;simpleType name="anglepos180Type">
 * <br />&lt;restriction base="double">
 * <br />&lt;minInclusive value="0"/>
 * <br />&lt;maxInclusive value="180.0"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public interface Anglepos180 extends SimpleType {

    /**
     * @return The angle value.
     */
    public double getAngle();
}
