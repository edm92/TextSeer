Text Seer Process Tools
=======================

>A collection of functions and tools for use on process effect accumulation. Free Software, Hell Yeah!

* Please note that the codebase has been updated substantially and this readme file has been left behind. It will be updated soon.

Basic users: 
	For basic configuration options edit 'base.properties'
	
Intermediate users:
	For more configuration options edit src.config.Settings.java
	
Advanced users:
	For even more configuration options edit each java source file in the au.edu.dsl.dlab... package. 


To load process repository - Store models in '/models/' directory. 


```sh
If you get an error about logger not found, copy the file 'log4j.properties' to your bin directory.

IF YOU GET AN ERROR PLEASE CHECK IMPORT.log for details of error.
```




Features
 * Basic XML reading of BPMN 2.0 files 
 * Orbital Based Effect Accumulation 
 * Basic Internal Sub-process processing


Updates
-------
 - 2013-11-07 -- Started documenting code after adding default logic reasoner. 
 - 2013-04-12 -- Bought in code for Model combination and loading BPMN2.0 files. 



--------------------------------------------------
 Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/