package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.apache.sis.util.Version;
import org.geotoolkit.util.Versioned;
import org.geotoolkit.wps.xml.WPSMarshallerPool;

/**
 * Describe basic attributes which should be found on every xml documents on
 * WPS service (requests and responses).
 *
 * TODO: Maybe it can be generified for all OGC services.
 *
 * @author Alexis Manin (Geomatys)
 */
public class DocumentBase implements Versioned {

    /**
     * WPS 2 defined extensions.
     * @implNote The JAXB definition is not on this attribute, but on a private
     * method, so we can omit it for WPS 1 documents without impacting public
     * API.
     */
    private List<Object> extension;
    @XmlAttribute(name = "service", required = true)
    private String service = "WPS";
    @XmlAttribute(name = "version", required = true)
    private String version;
    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace", required = true)
    private String lang;

    public DocumentBase() {}

    public DocumentBase(String service) {
        this(service, null, null);
    }

    public DocumentBase(String service, final String version) {
        this(service, version, null);
    }

    public DocumentBase(String service, final String version, final String lang) {
        this.service = service;
        this.version = version;
        this.lang = lang;
    }

    /**
     * Gets the value of the extension property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extension property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtension().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getExtension() {
        if (extension == null) {
            extension = new ArrayList<>();
        }
        return this.extension;
    }

    @XmlElement(name = "Extension", namespace= WPSMarshallerPool.WPS_2_0_NAMESPACE)
    private List<Object> getExtensionToMarshal() {
        if (FilterByVersion.isV2()) {
            return extension;
        } else {
            return null;
        }
    }

    private void setExtensionToMarshal(List extension) {
        this.extension = extension;
    }

    /**
     * Gets the value of the service property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Version getVersion() {
        if (version == null) {
            return new Version("2.0.0");
        } else {
            return new Version(version);
        }
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * RFC 4646 language code of the human-readable text (e.g. "en-CA") in the process description.
     *
     * @return
     *     possible object is
     *     {@link String }
     */
    public String getLanguage() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    public void setLanguage(String value) {
        this.lang = value;
    }
}
