![Genetic Code Analysis Toolkit Logo](/src/main/resources/bio/gcat/logo.png?raw=true)

This project contains the source code for the Genetic Code Analysis Toolkit (GCAT) project at the institute for mathematical biology.

Please refer to the [public homepage](http://www.gcat.bio/) and the [project homepage](http://www.mbi.hs-mannheim.de/research/mathematics-of-the-genetic-information.html) for more information.

## Getting started
Running the tool is as easy as to download the latest release from the [project homepage](http://www.gcat.bio/). Given that you have a [recent Java version](https://www.java.com/verify) installed on your machine, you can run the tool by using the Java Web Start "Launch" button on the homepage, or by downloading the latest JAR or EXE (specifically for Windows) executables.

### Command line batch processor
In addition to the graphical user interface, the toolkit offers a command line batch processing component. With the command line processor any arbitrary long list of sequences can be processed, following the rules of a script, which can be easily created using the built in Batch Tool.

Use the following command to execute the command line processor:

```bash
java -cp gcat.jar bio.gcat.batch.Batch \[-h] \[-v] \[-vv] \[-vvv] scriptfile sequencefile
```

### MacOS security prompt
Depending on your security settings, you might be prompted and unable to execute the tool on macOS. Applications downloaded from the internet are generally blocked, if not downloaded from the Apple App Store. To execute 

To execute the Genetic Code Analysis Toolkit on Mac, please visit the following [knowledge base article](https://support.apple.com/kb/PH25088) of Apple or execute the tool by using the Terminal window, calling the following command:

```bash
java -jar gcat.jar
```

## Copyright and license
Code and documentation copyright 2014-2017 Mannheim University of Applied Sciences. Code released under the Apache License, Version 2.0.