/**
 * An explanation
 * for this package is provided in the {@linkplain org.opengis.service OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the GeotoolKit implementation.
 */
@XmlSchema(elementFormDefault= XmlNsForm.QUALIFIED,
namespace="http://www.isotc211.org/2005/gco",
xmlns = {
    @XmlNs(prefix = "gfc", namespaceURI = "http://www.isotc211.org/2005/gfc"),
    @XmlNs(prefix = "gmd", namespaceURI = "http://www.isotc211.org/2005/gmd"),
    @XmlNs(prefix = "gco", namespaceURI = "http://www.isotc211.org/2005/gco"),
    @XmlNs(prefix = "xsi", namespaceURI = "http://www.w3.org/2001/XMLSchema-instance")
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(ScopedNameAdapter.class),
    @XmlJavaTypeAdapter(LocalNameAdapter.class),
    @XmlJavaTypeAdapter(GO_GenericName.class),
    // Primitive type handling
    @XmlJavaTypeAdapter(CharSequenceAdapter.class),
    @XmlJavaTypeAdapter(type=CharSequence.class, value=CharSequenceAdapter.class),
    @XmlJavaTypeAdapter(GO_Decimal.class),
    @XmlJavaTypeAdapter(type=double.class, value=GO_Decimal.class),
    @XmlJavaTypeAdapter(GO_Decimal.AsFloat.class),
    @XmlJavaTypeAdapter(type=float.class, value=GO_Decimal.AsFloat.class),
    @XmlJavaTypeAdapter(GO_Integer.AsLong.class),
    @XmlJavaTypeAdapter(type=long.class, value=GO_Integer.AsLong.class)
})
package org.geotoolkit.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.geotoolkit.internal.jaxb.metadata.*;
import org.geotoolkit.resources.jaxb.feature.catalog.*;
import org.geotoolkit.resources.jaxb.feature.catalog.code.*;
import org.geotoolkit.internal.jaxb.gco.*;
