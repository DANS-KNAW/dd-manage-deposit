Installation
============

Currently, this project is built as an RPM package for RHEL8/Rocky8 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/dd-manage-deposit` and the configuration files to `/etc/opt/dans.knaw.nl/dd-manage-deposit`.

Building from source
--------------------
Prerequisites:

* Java 21 or higher
* Maven 3.8.7 or higher
* RPM

Steps:

    git clone https://github.com/DANS-KNAW/dd-manage-deposit.git
    cd dd-manage-deposit 
    mvn clean install
