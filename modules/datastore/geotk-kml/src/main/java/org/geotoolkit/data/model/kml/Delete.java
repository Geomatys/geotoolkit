package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>This interface maps Delete element.</p>
 *
 * <br />&lt;element name="Delete" type="kml:DeleteType"/>
 * <br />&lt;complexType name="DeleteType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractFeatureGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/complexType>
 *
 * @author Samuel Andrés
 */
public interface Delete {

    /**
     *
     * @return AbstractFeature list.
     */
    public List<AbstractFeature> getFeatures();

}
