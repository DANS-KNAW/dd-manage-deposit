dd-manage-deposit
=================

Service that manages and maintains information about deposits in a deposit area


SYNOPSIS
--------

    dd-manage-deposit { server | check }


DESCRIPTION
-----------

### Purpose and context
Service that manages and maintains information about deposits in a deposit area. A deposit area is a collection of directories
that are used to receive and process deposits. See also: [deposit-directory], [dd-sword2], [dd-ingest-flow].

### Interfaces

The service has the following interfaces.

![Overview](img/overview.png){:width=50%}

#### Management & Report

* _Protocol type_: HTTP
* _Internal or external_: **internal**
* _Purpose_: Generating deposit reports and cleaning up the deposit area.

See [API](./to-api.md) for details.

#### Deposit boxes

* _Protocol type_: Shared filesystem
* _Internal or external_: **internal**
* _Purpose_: Monitoring changes in the contents of the deposit boxes and keeping the information in the database in sync.

ARGUMENTS
---------

        positional arguments:
        {server,check}         available commands
        
        named arguments:
        -h, --help             show this help message and exit
        -v, --version          show the application version and exit

EXAMPLES
--------

<!-- Add examples of invoking this module from the command line or via HTTP other interfaces -->
    

INSTALLATION AND CONFIGURATION
------------------------------
Currently, this project is built as an RPM package for RHEL8/Rocky8 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/dd-manage-deposit` and the configuration files to `/etc/opt/dans.knaw.nl/dd-manage-deposit`. 

BUILDING FROM SOURCE
--------------------
Prerequisites:

* Java 11 or higher
* Maven 3.3.3 or higher
* RPM

Steps:
    
    git clone https://github.com/DANS-KNAW/dd-manage-deposit.git
    cd dd-manage-deposit 
    mvn clean install

[deposit-directory]: https://dans-knaw.github.io/dans-datastation-architecture/deposit-directory/
[dd-sword2]: https://dans-knaw.github.io/dans-datastation-architecture/#dd-sword2
[dd-ingest-flow]: https://dans-knaw.github.io/dans-datastation-architecture/#dd-ingest-flow
