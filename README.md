glassfish-obr-builder
=====================

Implementing http://java.net/jira/browse/GLASSFISH-19395 and as a basic of glassfish provisioning stories

This is the contribution of Glassfish fighterfish/experimental/glassfish-obr-builder.

Its licensed under CDDL+GPL Licenses by Oracle Glassfish community.

## Design Documentation 

Using Apache Felix Bundle Repository to generate obr xml file for glassfish modules

* http://java.net/jira/browse/GLASSFISH-19395

## Building

You'll need a machine with JDK 6 1.22 above + and Apache Maven 3 installed.

Checkout:

    https://github.com/tangyong/glassfish-obr-builder.git

Run Build:
    
    mvn -DskipTests=true clean install

## Testing/Using

1) Adding Import-Service and Export-Service into osgi.bundle

-eg. web-glue (main/appserver/web/web-glue) 's osgi.bundle

     ...
     Require-Bundle: org.glassfish.main.ejb.ejb-container, org.glassfish.main.web.ha
     #TangYong Added Import-Service in oder to test Glassfish-Obr-Builder Module
     Import-Service: org.glassfish.internal.data.ApplicationRegistry, com.sun.enterprise.container.common.spi.JCDIService

-eg. weld-integration(main/appserver/web/weld-integration)'s osgi.bundle

     ...
     #TangYong Added Export-Service in oder to test Glassfish-Obr-Builder Module
     Export-Service: com.sun.enterprise.container.common.spi.JCDIService

Then, re-building web-glue and weld-integration modules, and replacing glassfish3/glassfish/modules/web-glue.jar and weld-integration.jar

with built web-glue.jar and weld-integration.jar.

2) Putting glassfish-obr-builder.jar into modules/autostart

3) Starting glassfish domain

4) Confirming obr-modules.xml

Under glassfish3/glassfish/domains/domain1/osgi-cache/felix, you should see generated obr-modules.xml. Opening the obr xml file, you will

see Import-Service and Export-Service related requires and capabilities from web-glue resouce and weld-integration resource.

## New Implemented Features


## Resolved Bugs


## To Do List

## Glassfish Team Leaders

* <sanjeeb.sahoo@oracle.com>