![Genetic Code Analysis Toolkit Logo](/src/main/resources/bio/gcat/logo.png?raw=true)

This project contains the source code for the Genetic Code Analysis Toolkit (GCAT) project. Please refer to the [public homepage](http://www.gcat.bio/) and the [project homepage](http://www.mbi.hs-mannheim.de/research/mathematics-of-the-genetic-information.html) for more information.

# Installation
GCAT is available for Windows (7, 8, 10), MacOS and Linux. Please download the latest release from the [project homepage](http://www.gcat.bio/). It requires [Java version 8 (or higher)](https://www.java.com/verify) installed on your machine. Invoke GCAT by 
 * starting the JAR file, e.g. by double clicking it or running it in a terminal from the command line
   ```bash
   java -jar gcat.jar
   ```
 * running the EXE file (specifically for Windows).
 
 #### MacOS security prompt
 Depending on your security settings, you might be prompted and unable to execute the tool on MacOS. Applications downloaded from the internet are generally blocked, if not downloaded from the Apple App Store. To execute the GCAT on Mac, please visit the following [knowledge base article](https://support.apple.com/kb/PH25088) of Apple or execute the tool by calling it from the command line in a terminal:
 
 ```bash
 java -jar gcat.jar
 ```

# Components
## Core program with user interface
GCAT opens a graphical user interface that enables to work with it in an interactive mode.

## Command line batch processor
In addition to the graphical user interface, the toolkit also offers a command line batch processing component. With the command line processor any arbitrary long list of sequences can be processed, following the rules of a script, which can be easily created using the built in Batch Tool.

Use the following command to execute the command line processor:

```bash
java -cp gcat.jar bio.gcat.batch.Batch [-h] [-v] [-vv] [-vvv] scriptfile sequencefile
```


# Building GCAT
GCAT uses Maven as the build system. Everything is configured in `pom.xml`.

# Copyright and license
Code and documentation copyright 2014-2017 Mannheim University of Applied Sciences. Code released under the Apache License, Version 2.0.