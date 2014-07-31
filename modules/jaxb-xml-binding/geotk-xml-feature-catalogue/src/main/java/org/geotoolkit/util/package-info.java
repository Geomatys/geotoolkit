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
//  @XmlJavaTypeAdapter(ScopedNameAdapter.class), // TODO
    @XmlJavaTypeAdapter(GO_LocalName.class),
    @XmlJavaTypeAdapter(GO_GenericName.class),
    // Primitive type handling
    @XmlJavaTypeAdapter(CharSequenceAdapter.class),
    @XmlJavaTypeAdapter(type=CharSequence.class, value=CharSequenceAdapter.class),
    @XmlJavaTypeAdapter(GO_Decimal.class),
    @XmlJavaTypeAdapter(type=double.class, value=GO_Decimal.class),
    @XmlJavaTypeAdapter(GO_Decimal32.class),
    @XmlJavaTypeAdapter(type=float.class, value=GO_Decimal32.class),
    @XmlJavaTypeAdapter(GO_Integer64.class),
    @XmlJavaTypeAdapter(type=long.class, value=GO_Integer64.class)
})
package org.geotoolkit.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.apache.sis.internal.jaxb.gco.*;
