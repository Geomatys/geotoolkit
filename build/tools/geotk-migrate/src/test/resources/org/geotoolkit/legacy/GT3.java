package my.application;

import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;

/**
 * Test file.
 * {@code GT2.java} is an example of GeoTools 2 code to upgrade to Geotk.
 * {@code GT3.java} is the expected result after {@link UpgradeFromGeoTools2} execution.
 */
public class GT {
    public static void main(String[] args) {
        if (args.length != 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UNEXPECTED_PARAMETER_$1, args[0]));
        }
        System.out.println(new DefaultCitation("My citation"));
        System.out.println(Utilities.equals("A", "B"));
    }
}
