//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.01 at 11:07:36 AM JST 
//


package org.glassfish.obrbuilder.subsystem;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class SubSystemObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public SubSystemObjectFactory() {
    }

    /**
     * Create an instance of {@link Subsystems }
     * 
     */
    public Subsystems createSubsystems() {
        return new Subsystems();
    }

    /**
     * Create an instance of {@link Subsystem }
     * 
     */
    public Subsystem createSubsystemsSubsystem() {
        return new Subsystem();
    }

    /**
     * Create an instance of {@link Repository }
     * 
     */
    public Repository createSubsystemsRepository() {
        return new Repository();
    }

    /**
     * Create an instance of {@link Module }
     * 
     */
    public Module createSubsystemsSubsystemModule() {
        return new Module();
    }
}
