dd-manage-deposit
=================

Service that manages and maintains information about deposits in a deposit area

Purpose
-------
Service that manages and maintains information about deposits in a deposit area. A deposit area is a collection of directories
that are used to receive and process deposits. See also: [deposit-directory], [dd-sword2], [dd-ingest-flow].

Interfaces
----------

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

[deposit-directory]: https://dans-knaw.github.io/dans-datastation-architecture/deposit-directory/
[dd-sword2]: https://dans-knaw.github.io/dans-datastation-architecture/#dd-sword2
[dd-ingest-flow]: https://dans-knaw.github.io/dans-datastation-architecture/#dd-ingest-flow
