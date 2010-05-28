package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps the colorType type</p>
 *
 * <br />&lt;simpleType name="colorType">
 * <br />&lt;annotation>
 * <br />&lt;documentation>&lt;![CDATA[
 *       aabbggrr
 *       ffffffff: opaque white
 *       ff000000: opaque]]>
 * <br />&lt;/documentation>
 * <br />&lt;/annotation>
 * <br />&lt;restriction base="hexBinary">
 * <br />&lt;length value="4"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public interface Color extends SimpleType {

    /**
     *
     * @return the color hxadecimal notation.
     */
    public String getColor();
}
