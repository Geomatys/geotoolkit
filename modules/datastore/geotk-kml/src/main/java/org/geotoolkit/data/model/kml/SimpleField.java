package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>This interface maps SimpleField element.</p>
 *
 * <br />&lt;element name="SimpleField" type="kml:SimpleFieldType"/>
 * <br />&lt;complexType name="SimpleFieldType" final="#all">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:displayName" minOccurs="0"/>
 * <br />&lt;element ref="kml:SimpleFieldExtension" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;attribute name="type" type="string"/>
 * <br />&lt;attribute name="name" type="string"/>
 *
 * @author Samuel Andrés
 */
public interface SimpleField {

    /**
     *
     * @return
     */
    public String getDisplayName();
    //public List<SimpleFieldExtension> getSimpleFieldExtensions();

    /**
     *
     * @return
     */
    public String getType();

    /**
     * 
     * @return
     */
    public String getName();
}
