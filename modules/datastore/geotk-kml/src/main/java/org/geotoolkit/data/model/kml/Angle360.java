package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Angle360 type:</p>
 *
 * <br />&lt;simpleType name="angle360Type">
 * <br />&lt;restriction base="double">
 * <br />&lt;minInclusive value="-360"/>
 * <br />&lt;maxInclusive value="360.0"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public interface Angle360 extends SimpleType {

    /**
     * @return The angle value.
     */
    public double getAngle();
}
