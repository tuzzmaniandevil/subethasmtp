/*
 * $Id$
 * $URL$
 */
package org.subethamail.smtp;

/**
 * Provides version information from the manifest.
 *
 * @author Jeff Schnitzer
 */
public class Version {

    /**
     *
     * @return specification version
     */
    public static String getSpecification() {
        Package pkg = Version.class.getPackage();
        return (pkg == null) ? null : pkg.getSpecificationVersion();
    }

    /**
     *
     * @return implementation version
     */
    public static String getImplementation() {
        Package pkg = Version.class.getPackage();
        return (pkg == null) ? null : pkg.getImplementationVersion();
    }

    /**
     * A simple main method that prints the version and exits
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        System.out.println("Version: " + getSpecification());
        System.out.println("Implementation: " + getImplementation());
    }
}
