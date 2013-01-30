glassfish-obr-builder
=====================

Implementing http://java.net/jira/browse/GLASSFISH-19395 and as a basic of glassfish provisioning stories

This is the contribution of Glassfish fighterfish/experimental/glassfish-obr-builder.

Its licensed under CDDL+GPL Licenses by Oracle Glassfish community.

## Design Documentation 

1 Using Apache Felix Bundle Repository to generate obr xml file for glassfish modules

* http://java.net/jira/browse/GLASSFISH-19395

2 Defining a subsystem concept and using the glassfish-obr-builder to deploy a group of bundles and their dependencies

3 Initial Provision Strategy

1) Glassfish System OBR

Repository---> glassfish3/glassfish/modules

2) User-defined OBR

Repository---> defined in subsystem xml file

current provisioning way: local disk system, in the future, maven repo will be supported

## Building

You'll need a machine with JDK 6 1.22 above + and Apache Maven 3 installed.

1 glassfish-obr-builder module building

Checkout:

    https://github.com/tangyong/glassfish-obr-builder.git

Run Build:
    
    mvn -DskipTests=true clean install
    
2 glassfish-provisioning-samples building

Checkout:

    https://github.com/tangyong/glassfish-provisioning-samples.git

Run Build:
    
    mvn -DskipTests=true clean install

## GLASSFISH-19395 Testing

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

Then, re-building web-glue and weld-integration modules, and replacing glassfish3/glassfish/modules/web-glue.jar and weld-integration.jar with built web-glue.jar and weld-integration.jar.

2) Putting glassfish-obr-builder.jar into modules/autostart

3) Starting glassfish domain

4) Confirming obr-modules.xml

Under glassfish3/glassfish/domains/domain1/osgi-cache/felix, you should see generated obr-modules.xml. Opening the obr xml file, you will see Import-Service and Export-Service related requires and capabilities from web-glue resouce and weld-integration resource.

## Provisioning Subsystem Testing

The following assumes you use a windows system. (Linux/Unix is similar)

1 Creating a subsystem client directory in your disk

eg. in my env

1) mkdir d:\provisioning-sample\

2) put provisioning samples and client and subsystems.xml into  "d:\provisioning-sample\"

The following should be put into d:\provisioning-sample\

-a_api.jar

-a_impl.jar

-b_api.jar

-b_impl.jar

-c_api.jar

-c_impl.jar

-subsystems.xml

-provisioning_client.jar

The above bundles and subsystems.xml come from https://github.com/tangyong/glassfish-provisioning-samples.git.

Notice: because the provisioning is only used for making experiment, so I hardcode subsystem client directory as "d:\provisioning-sample\" in provisioning_client project, if you want to use a different  directory, please modify https://github.com/tangyong/glassfish-provisioning-samples/blob/master/provisioning_client/src/main/java/org/glassfish/provisioning/sample/client/Activator.java, then re-building the client project.

3) Putting glassfish-obr-builder.jar into glassfish modules/autostart

4) Starting glassfish domain

5) asadmin deploy --type=osgi d:\provisioning-sample\provisioning_client.jar

6) asadmin osgi lb 

Seeing whether these bundles have been deployed successfully, and some bundles have been in active state, because defaultly we start some modules based on subsystem xml file. 

In addition, in server.log, you can find the following contents:

   [#|2013-01-30T22:37:35.218+0900|INFO|glassfish 4.0|javax.enterprise.logging.stdout|_ThreadID=83;_ThreadName=admin-listener(1);_TimeMillis=1359553055218;_LevelValue=800;|module name: sample.glassfish.provisioning.a_impl   module start level: 1|#]

   [#|2013-01-30T22:37:35.218+0900|INFO|glassfish 4.0|javax.enterprise.logging.stdout|_ThreadID=83;_ThreadName=admin-listener(1);_TimeMillis=1359553055218;_LevelValue=800;|module name: sample.glassfish.provisioning.b_impl   module start level: 2|#]

   [#|2013-01-30T22:37:35.218+0900|INFO|glassfish 4.0|javax.enterprise.logging.stdout|_ThreadID=83;_ThreadName=admin-listener(1);_TimeMillis=1359553055218;_LevelValue=800;|module name: sample.glassfish.provisioning.c_impl   module start level: 3|#]

   [#|2013-01-30T22:37:35.250+0900|INFO|glassfish 4.0|javax.enterprise.logging.stdout|_ThreadID=83;_ThreadName=admin-listener(1);_TimeMillis=1359553055250;_LevelValue=800;|Hello A!AndHello B!AndHello B!|#]


You can also see user-defined obr file called "obr-provisioning-sample.xml" in glassfish3\glassfish\domains\domain1\osgi-cache\felix\provisioning-sample .

In addition, you can try to switch definition order of c_impl.jar and a_impl.jar in subsystems.xml, and no problem, deployment will be still normal.

## Bugs List

## To Do List

1 adding an api into ObrHandlerService to undeploy the subsystem

2 enhancing provision strategy

## Glassfish Team Leaders

* <sanjeeb.sahoo@oracle.com>