# OpenShift Test Jenkins Job Docker build

Dieser Jenkins job triggert auf OpenShift V3 ein Build, ist dieser Job grün funktionieren die folgenden Features:

* Login mittels Jenkins service Account
* Master läuft (API)
* Die Source (Dockerfile und Context) kann von Github herunter geladen werden
* Während dem Build können via Composer vom Internet Dependencies herunter geladen werden.
* Docker Registry ist verfügbar, das Resultat konnte in die Registry gepushed werden
* Die Applikation konnte deployed werden
* Von "aussen" konnte via Route auf die Applikation zugegriffen werden.


## Test Applikation und Jenkins Job einrichten.

Die Testapplikation kann mit den folgenden Befehlen erstellt und exposed werden.

**Dockerbuild:**

```
$ oc new-app https://github.com/appuio/example-php-docker-helloworld.git --strategy=docker --name=builddocker
$ oc expose svc builddocker
```

**Source To Image Build**

```
$ oc new-app https://github.com/appuio/example-php-sti-helloworld.git --name=buildsource2image
$ oc expose svc buildsource2image
```


### Jenkins Job
Auf dem Jenkins ein neuer Pipeline Job einrichten und entsprechend das jenkins.groovy script im aktuellen Verzeichnis angeben.

Parameter:
* OSE_MASTER, OpenShift Master URL including protokol. eg. https://master.example.com:8443
* OSE_APPURL, The URL to test after deployment
* OSE_EMAIL_TO, Notification when deployment failes
* OSE_NAMESPACE, OpenShift Project
* OSE_BUILDCONFIG, OpenShift Buildconfig name eg. builddocker or buildsource2image for the examples in this README
* OSE_DEPLOYMENTCONFIG, OpenShift Deployment name eg. builddocker or buildsource2image for the examples in this README

Es muss noch definiert werden, wann der Job jeweils getriggered werden soll:
Bsp: @hourly
