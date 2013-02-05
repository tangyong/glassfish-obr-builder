glassfish-obr-builder
=====================

Implementing http://java.net/jira/browse/GLASSFISH-19395 and as a basic of glassfish provisioning stories

This is the contribution of Glassfish fighterfish/experimental/glassfish-obr-builder.

Its licensed under CDDL+GPL Licenses by Oracle Glassfish community.

## Design Documentation 

1 Using Apache Felix Bundle Repository to generate obr xml file for glassfish modules

* http://java.net/jira/browse/GLASSFISH-19395

2 Defining a subsystem concept and using the glassfish-obr-builder to deploy a group of bundles and their dependencies

* http://java.net/jira/browse/GLASSFISH-19146

* http://java.net/jira/browse/GLASSFISH-19400 (future plan)

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
    
    mvn clean install
    
2 glassfish-provisioning-samples building

Checkout:

    https://github.com/tangyong/glassfish-provisioning-samples.git

Run Build:
    
    mvn  clean install

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

## Provisioning Subsystem Testing(Non Rest Access)

The following assumes you use a windows system. (Linux/Unix is similar)

1 creating a directory in your disk where you put provisioning sample bundles as following:

eg. in my env

1) mkdir d:\provisioning-sample\

2) putting provisioning sample bundles into the above created directory

-a_api.jar

-a_impl.jar

-b_api.jar

-b_impl.jar

-c_api.jar

-c_impl.jar

2 putting the subsystems.xml into any place of your disk.

3 editting the subsystems.xml and replacing the value of \<repository\> with right value based on 2.

4 putting glassfish-obr-builder.jar into glassfish modules/autostart

5 starting glassfish domain

6 asadmin deploy --type=osgi provisioning_web_client.war

7 accessing "http://localhost:8080/subsystem/" and click "Deploying Subsystem" link

8 uploading your subsystems.xml and click "deploy" button, then, you will see deployed subsystem detailed info from page.

9 backing to Subsystems Administration Page, and click "Listing Subsystems" link, then, selecting a subsystems from list box, clicking "display detailed info", and you will see detailed deployed subsystems info.

10 asadmin osgi lb 

Seeing whether these bundles have been deployed successfully, and some bundles have been in active state, because defaultly we start some modules based on subsystem xml file. 

In addition, in server.log, you can find the following contents:

   [#|2013-01-30T22:37:35.218+0900|INFO|glassfish 4.0|javax.enterprise.logging.stdout|_ThreadID=83;_ThreadName=admin-listener(1);_TimeMillis=1359553055218;_LevelValue=800;|module name: sample.glassfish.provisioning.a_impl   module start level: 1|#]

   [#|2013-01-30T22:37:35.218+0900|INFO|glassfish 4.0|javax.enterprise.logging.stdout|_ThreadID=83;_ThreadName=admin-listener(1);_TimeMillis=1359553055218;_LevelValue=800;|module name: sample.glassfish.provisioning.b_impl   module start level: 2|#]

   [#|2013-01-30T22:37:35.218+0900|INFO|glassfish 4.0|javax.enterprise.logging.stdout|_ThreadID=83;_ThreadName=admin-listener(1);_TimeMillis=1359553055218;_LevelValue=800;|module name: sample.glassfish.provisioning.c_impl   module start level: 3|#]

   [#|2013-01-30T22:37:35.250+0900|INFO|glassfish 4.0|javax.enterprise.logging.stdout|_ThreadID=83;_ThreadName=admin-listener(1);_TimeMillis=1359553055250;_LevelValue=800;|Hello A!AndHello B!AndHello B!|#]


You can also see user-defined obr file called "obr-provisioning-sample.xml" and subsystem xml file in glassfish3\glassfish\domains\domain1\osgi-cache\felix\bundle272(the bundle id maybe is different on your env)\data\subsystems\provisioning-samples

In addition, you can try to switch definition order of c_impl.jar and a_impl.jar in subsystems.xml, and no problem, deployment will be still normal.

## Provisioning Subsystem Testing(Rest Access)

Currently, a "listsubsystems" REST API has been available and other REST API is doing

So, in order to demostrate the  "listsubsystems" REST API, please firstly deploying a subsystem based on "Provisioning Subsystem Testing(Non Rest Access) 's step1Å`step8",

then, in browser, clicking "http://localhost:8080/osgi/jersey-http-service/obrhandler/listsubsystems/provisioning-samples" , you will see the following result:

   <subsystems name="provisioning-samples" description="subsystems provisioning test">
      <repository name="provisioning-sample" uri="D:/provisioning-sample/"/>
      <subsystem name="provisioning-sample" description="subsystems provisioning test samples">
         <module name="sample.glassfish.provisioning.c_api" version="1.0.0.SNAPSHOT" start="false" description="c api module"/>
         <module name="sample.glassfish.provisioning.c_impl" start="true" startlevel="3"/>
         <module name="sample.glassfish.provisioning.a_api" start="false"/>
         <module name="sample.glassfish.provisioning.a_impl" start="true" startlevel="1"/>
         <module name="sample.glassfish.provisioning.b_api" start="false"/>
         <module name="sample.glassfish.provisioning.b_impl" start="true" startlevel="2"/>
      </subsystem>
   </subsystems>

## New Features

1 The feature of deploying subsystems can be used

2 A new WAB based provisioning client can be used for deploying subsystems,  and is being enhanced

3 The feature of listing subsystems can be used

4 Have implemented saving user-defined obr file and subsystem xml file into glassfish-obr-builder bundle storage area

5 "listsubsystems" REST API has been available 

## Bugs List

None.

## Doing List

1 Improve provisioning client using WAB to meet some configurations and can execute different actions

* https://github.com/tangyong/glassfish-obr-builder/issues/19

* https://github.com/tangyong/glassfish-provisioning-samples/issues/8

2 Supporting undeploying subsystems

* https://github.com/tangyong/glassfish-obr-builder/issues/17

3 Refactoring ObrHandlerServiceImpl class to make code more friendly

* https://github.com/tangyong/glassfish-obr-builder/issues/28

4 Supporting Rest Access from client

* https://github.com/tangyong/glassfish-obr-builder/issues/31

## To Do List

1 adding some apis into ObrHandlerService to update the subsystem

* https://github.com/tangyong/glassfish-obr-builder/issues/25

2 combining subsystem deployment with asadmin deployment command

* https://github.com/tangyong/glassfish-obr-builder/issues/21

3 enhancing provision strategy

* https://github.com/tangyong/glassfish-obr-builder/issues/8

4 supporting target provisoning(eg. Cloud Provisioning, glassfish cluster remote provisioning...)

* https://github.com/tangyong/glassfish-obr-builder/issues/29

## Glassfish Team Leaders

* <sanjeeb.sahoo@oracle.com>