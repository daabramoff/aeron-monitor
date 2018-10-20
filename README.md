[![GitHub](https://img.shields.io/github/license/mashape/apistatus.svg)
[![Build Status](https://travis-ci.com/data-ferry/aeron-monitor.svg?branch=master)](https://travis-ci.com/data-ferry/aeron-monitor)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/data-ferry/aeron-monitor.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/data-ferry/aeron-monitor/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/data-ferry/aeron-monitor.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/data-ferry/aeron-monitor/context:java)
[![Language grade: JavaScript](https://img.shields.io/lgtm/grade/javascript/g/data-ferry/aeron-monitor.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/data-ferry/aeron-monitor/context:javascript)

# Build

[Gradle](http://gradle.org/) is used as build system. Latest stable JDK 8 is also required to build
the project.

Clean build:

```shell
    $ git clone https://github.com/data-ferry/aeron-monitor.git
    $ cd aeron-monitor
    $ ./gradlew
```

# Run

```shell
    $ java -jar aeron-monitor.jar [--port=9000] \
                                  [--aeron="name0:path0[,name1:path1]..."] \
                                  [-Dloader.path="plugins-path"] 
```

# License

Copyright 2018 Data Ferry

Licensed under the MIT License

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
