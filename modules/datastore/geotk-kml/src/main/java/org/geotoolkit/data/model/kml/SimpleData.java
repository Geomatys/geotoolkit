package org.geotoolkit.data.model.kml;

/**
 * <p>This interface maps SimpleData element.</p>
 *
 * <br />&lt;element name="SimpleData" type="kml:SimpleDataType"/>
 * <br />&lt;complexType name="SimpleDataType" final="#all">
 * <br />&lt;simpleContent>
 * <br />&lt;extension base="string">
 * <br />&lt;attribute name="name" type="string" use="required"/>
 * <br />&lt;/extension>
 * <br />&lt;/simpleContent>
 * <br />&lt;/complexType>
 *
 * @author Samuel Andrés
 */
public interface SimpleData {

    /**
     *
     * @return
     */
    public String getName();

    /**
     * 
     * @return
     */
    public String getContent();

}
