package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractFeature extends AbstractObject {

    public String getName();
    public boolean getVisibility();
    public boolean getOpen();
    public AtomPersonConstruct getAuthor();
    public AtomLink getAtomLink();
    public String getAddress();
    public AddressDetails getAddressDetails();
    public String getPhoneNumber();
    public String getSnippet();
    public String getDescription();
    public AbstractView getView();
    public AbstractTimePrimitive getTimePrimitive();
    public String getStyleUrl();
    public List<AbstractStyleSelector> getStyleSelectors();
    public Region getRegion();
    public ExtendedData getExtendedData();
    public List<SimpleType> getAbstractFeatureSimpleExtensions();
    public List<AbstractObject> getAbstractFeatureObjectExtensions();

}
