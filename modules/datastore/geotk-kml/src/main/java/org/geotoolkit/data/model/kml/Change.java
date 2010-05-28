package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>This interface maps Change element.</p>
 *
 * <br />&lt;element name="Change" type="kml:ChangeType"/>
 * <br />&lt;complexType name="ChangeType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractObjectGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/complexType>
 *
 * @author Samuel Andrés
 */
public interface Change {

    /**
     *
     * @return the lis of AbstractObjects
     */
    public List<AbstractObject> getObjects();
}
