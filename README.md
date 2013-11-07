Text Seer Process Tools
=======================

>A collection of functions and tools for use on process effect accumulation. Free Software, Hell Yeah!

* * Please note that the codebase has been updated substantially and this readme file has been left behind. It will be updated soon. *

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
 - 12-04-2013 -- Bought in code for Model combination and loading BPMN2.0 files. 


Notes
-----

A set of classes help with quick BPMN graph loading. This set of classes have included 
 * jBPT which is distributed under the GNU Lesser GPL (www.gnu.org/licenses/lgpl.html)
 * Yaoqiang BPMN which is distributed under GNU General Public License version 3.0 (GPLv3) (http://www.gnu.org/licenses/quick-guide-gplv3.html)


> Notes on use

All sections of code have been implemented in basic Example programs. 

1) XML reading of BPMN2.0 files. 
I have based the implementation of BPMN2.0 standard files that have been generated using the Eclipse based Activiti process designer. 
This has been done as the designer can import Signavio based BPMN files. 




