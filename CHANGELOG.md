#  (2019-11-27)



# [1.2.0](https://github.com/BrotherJing/scalable-ot-java-backend/compare/1.1.0...1.2.0) (2019-11-27)


### Bug Fixes

* **api:** disable dubbo consumer check on start up ([2c12036](https://github.com/BrotherJing/scalable-ot-java-backend/commit/2c12036))
* **api:** encapsulate routing logic in RouteService ([911517e](https://github.com/BrotherJing/scalable-ot-java-backend/commit/911517e))
* **backend:** extract interface for broadcast ([aba6eb2](https://github.com/BrotherJing/scalable-ot-java-backend/commit/aba6eb2))
* **backend:** fix NPE ([2fa2bfc](https://github.com/BrotherJing/scalable-ot-java-backend/commit/2fa2bfc))
* **backend:** register both server port and dubbo port in zookeeper ([bbab6b5](https://github.com/BrotherJing/scalable-ot-java-backend/commit/bbab6b5))
* **backend:** reset pointer before executing each json operation ([375211a](https://github.com/BrotherJing/scalable-ot-java-backend/commit/375211a))


### Features

* **api:** implement routing logic which support dynamically adding/removing broadcast server ([85a219a](https://github.com/BrotherJing/scalable-ot-java-backend/commit/85a219a))
* **api:** implement web socket load balancing by assigning server address based on docId ([4c84d39](https://github.com/BrotherJing/scalable-ot-java-backend/commit/4c84d39))
* **backend:** add redis interface for registering docId -> broadcast server route ([29b7c00](https://github.com/BrotherJing/scalable-ot-java-backend/commit/29b7c00))
* **backend:** implement consistent hash load balancer ([74a19d2](https://github.com/BrotherJing/scalable-ot-java-backend/commit/74a19d2))
* **backend:** improve error handing in consumer ([2121537](https://github.com/BrotherJing/scalable-ot-java-backend/commit/2121537))
* **broadcast:** implement sending broadcast request to specific server by extending dubbo cluster ([514252a](https://github.com/BrotherJing/scalable-ot-java-backend/commit/514252a))
* **broadcast:** register and discover broadcast service in zookeeper ([5675af1](https://github.com/BrotherJing/scalable-ot-java-backend/commit/5675af1))
* **broadcast:** register docId -> broadcast server route when init connection with client ([5efdf16](https://github.com/BrotherJing/scalable-ot-java-backend/commit/5efdf16))
* **broadcast:** separate api and broadcast service, interact through dubbo ([3768ac3](https://github.com/BrotherJing/scalable-ot-java-backend/commit/3768ac3))
* **consumer:** put consumer in consumer group to support horizontal scaling ([d8c14c5](https://github.com/BrotherJing/scalable-ot-java-backend/commit/d8c14c5))
* **consumer:** set up manual acknowledge ([8819484](https://github.com/BrotherJing/scalable-ot-java-backend/commit/8819484))
* **consumer:** skip duplicated command to achieve idempotency ([d31d302](https://github.com/BrotherJing/scalable-ot-java-backend/commit/d31d302))



# [1.1.0](https://github.com/BrotherJing/scalable-ot-java-backend/compare/1.0.0...1.1.0) (2019-11-14)


### Features

* **backend:** add api for create spreadsheet ([2b00363](https://github.com/BrotherJing/scalable-ot-java-backend/commit/2b00363))
* **backend:** move command execution logic into separate impl class ([286bbc4](https://github.com/BrotherJing/scalable-ot-java-backend/commit/286bbc4))
* **backend:** support fetch snapshot at specific version ([1b90732](https://github.com/BrotherJing/scalable-ot-java-backend/commit/1b90732))
* **backend:** take snapshot at intervals ([ceee211](https://github.com/BrotherJing/scalable-ot-java-backend/commit/ceee211))
* **core:** implement command execution for json type ([fc36536](https://github.com/BrotherJing/scalable-ot-java-backend/commit/fc36536))
* **proto:** generalize Command, Snapshot, etc. Add json operation type ([4065647](https://github.com/BrotherJing/scalable-ot-java-backend/commit/4065647))



# [1.0.0](https://github.com/BrotherJing/scalable-ot-java-backend/compare/8bf9ede...1.0.0) (2019-10-24)


### Bug Fixes

* **dao:** fix condition of command dao ([ea2b026](https://github.com/BrotherJing/scalable-ot-java-backend/commit/ea2b026))
* **doc-service:** fix apply insert operation ([a3104f1](https://github.com/BrotherJing/scalable-ot-java-backend/commit/a3104f1))


### Features

* **api:** provide api for sending op through kafka ([047c682](https://github.com/BrotherJing/scalable-ot-java-backend/commit/047c682))
* **backend:** implement command dao ([de8135c](https://github.com/BrotherJing/scalable-ot-java-backend/commit/de8135c))
* **backend:** implement op consumer and api for create/fetch ([8bf9ede](https://github.com/BrotherJing/scalable-ot-java-backend/commit/8bf9ede))
* **backend:** implement websocket module ([7702d5e](https://github.com/BrotherJing/scalable-ot-java-backend/commit/7702d5e))
* **broadcast:** implement broadcast service as a grpc service ([d182d40](https://github.com/BrotherJing/scalable-ot-java-backend/commit/d182d40))
* **proto:** add broadcast service definition ([363ca8f](https://github.com/BrotherJing/scalable-ot-java-backend/commit/363ca8f))
* **proto:** replace proto maven plugin ([8e14a44](https://github.com/BrotherJing/scalable-ot-java-backend/commit/8e14a44))



