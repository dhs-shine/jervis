# Jervis: Jenkins as a service



[![Maven Central Release][status-release]][maven-badge]
[![Build Status][status-build]][jervis-travis]
[![Coverage Status][status-coverage]][jervis-coveralls]
[![Dependency Status][status-versioneye]][jervis-versioneye]

* *Project status:* [released to maven central][maven-release].
* *Currently Targeted platforms:*
  * *Jenkins server host:* Linux and Mac OS X (Windows compatible)
  * *Jobs on clients:* Linux only (Multi-platform capable)

##### What is Jervis?

* What is Jervis? A library for [Job DSL plugin][jenkins-plugin-job-dsl]
  scripts and [shared Jenkins pipeline libraries][pipeline].  It is used to
  augment the automation of generating Jenkins jobs.
* What is Jervis not? Jervis is not a Jenkins plugin.

# Documentation

* [Jervis Wiki][jervis-wiki]
  * [Build Overview][jervis-wiki-overview]
  * [Supported Languages][jervis-wiki-languages]
  * [Supported Build Tools][jervis-wiki-build-tools]


The library API is also [fully documented][jervis-api-docs].  To generate the
latest developer docs execute the following command.

    ./gradlew groovydoc

The documentation can be found in `build/docs/groovydoc`.

##### Provided examples

* Bootstrapping Jenkins: Example [bootstrap for
  Jenkins][jervis-jenkins-bootstrap] which allows you to quickly try out this
  library.
* Job DSL Script: [Example Job DSL script](jobs/firstjob_dsl.groovy) is
  provided.
* Jenkins build node: There's also a [docker container][jervis-docker] designed
  to be used as a Jenkins build agent through the [Jenkins Docker
  Plugin][jenkins-plugin-docker].
* Jervis configuration files: [`lifecycles.json`][json-lifecycles],
  [`toolchains.json`][json-toolchains], and [`platforms.json`][json-platforms].
* Shared pipeline library: [`vars/`](vars) and [`resources/`](resources)
  directories.

# More about Jervis

Jervis is a combination of some letters in the words Jenkins and Travis: JEnkins
tRaVIS.  [Jenkins][jenkins] is a [continuous integration][wiki-ci] tool which is
typically installed on premises.  [Travis][travis] is a hosted, distributed
continuous integration system used by many [open source][wiki-os] projects.
Both Jenkins and Travis have paid and enterprise offerings.

Jervis uses Travis-like job generation using the [Job DSL
plugin][jenkins-plugin-job-dsl] and groovy scripts.  It reads the `.jervis.yml`
file of a project and generates a job in Jenkins based on it.  If `.jervis.yml`
doesn't exist then it will fall back to using the [`.travis.yml`][travis-yaml]
file.

For development planning and other documentation see the [Jervis
wiki][jervis-wiki].  If you wish to stay up to date with the latest Jervis news
then please feel free to [watch this repository][watch-repo] because I use the
issue tracking and wiki for planning.

## Why Jervis?

What is Jervis attempting to scale?  Let's talk about some scale bottlenecks
that have been overcome by Jenkins (formerly Hudson) and its community.

The scaling issue is a main bullet. The solution for the issue is in a
sub-bullet.

* Developers are challenged with integrating work, building often, and even
  deploying often.
  * Jenkins was invented.
* Jenkins infrastructure is strained when too many slaves are in one master and
  too many jobs are queued up on a daily basis.  A single master struggles to
  perform all requested builds in a timely manner.  Jenkins also suffers from
  single point of failure as a lone master.
  * Multi-master Jenkins was invented. This provides redundancy for the master.
    Throughput for daily build capacity is improved.
* Jenkins jobs suffer from a lot of duplicate code.  It is difficult to fix a
  bug in one job and have it propagate to other jobs.
  * Jenkins Job DSL plugin was invented.  Configuration through code is now
    possible.  Multiple jobs can be generated and regenerated with the same code
    using templates in a domain specific language.
* Onboarding new projects in a Jenkins installation can be difficult.  Typically
  engineers will get together and discuss the needs of the project and then
  configure a Jenkins job for the needs of the project.  For enterprises with a
  very large number of projects it is typically hard to scale number of build
  engineers to match with the large number of projects which require onboarding
  into the build ecosystem.
  * Jervis is being invented.  Job generation through convention over
    configuration.  Scaling the onboarding for a project by creating and abiding
    by conventions in how jobs are generated.  This is for large scale job
    generation and project onboarding.  Jervis is taking lessons learned from a
    seasoned build engineer and attempting to fill this gap in the Jenkins
    ecosystem.

# Set up

To include this library for use in your Job DSL plugin scripts you only need
include it in your build tool.

#### Maven

```xml
<dependency>
  <groupId>net.gleske</groupId>
  <artifactId>jervis</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```

#### Gradle

Your Job DSL scripts should have a `build.gradle` file which has the following
contents.

```gradle
apply plugin: 'maven'

repositories {
    mavenCentral()
}


configurations {
    libs
}

dependencies {
    libs 'net.gleske:jervis:1.0'
    libs 'org.yaml:snakeyaml:1.19'
}

task cleanLibs(type: Delete) {
    delete 'lib'
}

task libs(type: Copy) {
    into 'lib'
    from configurations.libs
}

defaultTasks 'clean', 'libs'
clean.dependsOn cleanLibs
```

Then execute `./gradlew libs` to assemble dependencies into the `lib` directory
of the Jenkins workspace.  Don't forget to add `lib` to the classpath.  This
must be done before you configure your Jenkins job to execute Job DSL scripts.

# Interactive debugging

Groovy Console is built into the Gradle file.

    ./gradlew console

# Other development commands

Generate code coverage reports.  Due to a [bug][#39], cobertura code coverage
is most reliable in Groovy versions 1.8.9 or 2.0.8.  See the final report in
`build/reports/cobertura`.

    GROOVY_VERSION=1.8.9 ./gradlew clean cobertura

Build the jar file.

    ./gradlew clean jar

Sign build jars and sign archives.

    ./gradlew clean check signArchives

See also [RELEASE.md](RELEASE.md).

# License

    Copyright 2014-2018 Sam Gleske

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[#39]: https://github.com/samrocketman/jervis/issues/39
[jenkins-plugin-docker]: https://wiki.jenkins-ci.org/display/JENKINS/Docker+Plugin
[jenkins-plugin-job-dsl]: https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin
[jenkins]: https://jenkins-ci.org/
[jervis-api-docs]: http://sam.gleske.net/jervis-api/
[jervis-coveralls]: https://coveralls.io/github/samrocketman/jervis
[jervis-docker]: https://github.com/samrocketman/docker-jenkins-jervis
[jervis-jenkins-bootstrap]: https://github.com/samrocketman/jenkins-bootstrap-jervis
[jervis-travis]: https://travis-ci.org/samrocketman/jervis
[jervis-versioneye]: https://www.versioneye.com/user/projects/54f2a1cc4f3108959a0007f1
[jervis-wiki-build-tools]: https://github.com/samrocketman/jervis/wiki/Supported-Tools
[jervis-wiki-languages]: https://github.com/samrocketman/jervis/wiki/Supported-Languages
[jervis-wiki-overview]: https://github.com/samrocketman/jervis/wiki/Build-overview
[jervis-wiki]: https://github.com/samrocketman/jervis/wiki
[json-lifecycles]: resources/lifecycles-ubuntu1604-stable.json
[json-platforms]: resources/platforms.json
[json-toolchains]: resources/toolchains-ubuntu1604-stable.json
[maven-badge]: https://github.com/jirutka/maven-badges
[maven-release]: http://search.maven.org/#search|ga|1|g:"net.gleske"%20a:"jervis"
[milestone-progress]: https://github.com/samrocketman/jervis/milestones
[pipeline]: https://jenkins.io/doc/book/pipeline/shared-libraries/
[status-build]: https://travis-ci.org/samrocketman/jervis.svg?branch=master
[status-coverage]: https://coveralls.io/repos/github/samrocketman/jervis/badge.svg?branch=master
[status-release]: https://maven-badges.herokuapp.com/maven-central/net.gleske/jervis/badge.svg
[status-versioneye]: https://www.versioneye.com/user/projects/54f2a1cc4f3108959a0007f1/badge.svg?style=flat
[travis-yaml]: http://docs.travis-ci.com/user/build-configuration/
[travis]: https://travis-ci.org/
[watch-repo]: https://help.github.com/articles/watching-repositories/
[wiki-ci]: https://en.wikipedia.org/wiki/Continuous_integration
[wiki-os]: http://en.m.wikipedia.org/wiki/Open_source
