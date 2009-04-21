package my.application;

import org.geotools.metadata.iso.citation.CitationImpl;
import org.geotools.resources.i18n.Errors;
import org.geotools.resources.i18n.ErrorKeys;
import org.geotoolkit.util.Utilities;

/**
 * Test file.
 * {@code GT2.java} is an example of GeoTools 2 code to upgrade to Geotoolkit.
 * {@code GT3.java} is the expected result after {@link UpgradeFromGeoTools2} execution.
 */
public class GT {
    public static void main(String[] args) {
        if (args.length != 0) {
            throw new IllegalArgumentException(Errors.format(ErrorKeys.UNEXPECTED_PARAMETER_$1, args[0]));
        }
        System.out.println(new CitationImpl("My citation"));
        System.out.println(Utilities.equals("A", "B"));
    }
}
