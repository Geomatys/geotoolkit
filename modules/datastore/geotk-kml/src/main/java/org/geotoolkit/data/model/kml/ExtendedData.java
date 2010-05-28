package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>This interface maps ExtendedDate element.</p>
 *
 * <br />&lt;element name="ExtendedData" type="kml:ExtendedDataType"/>
 * <br />&lt;complexType name="ExtendedDataType" final="#all">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:Data" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:SchemaData" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;any namespace="##other" processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/complexType>
 *
 * @author Samuel Andrés
 */
public interface ExtendedData {

    /**
     *
     * @return
     */
    public List<Data> getDatas();

    /**
     *
     * @return
     */
    public List<SchemaData> getSchemaData();

    /**
     * 
     * @return
     */
    public List<Object> getAnyOtherElements();
}
