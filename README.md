
## Text Seer Process Tools
----------------------------

> TextSeer process tools provides a collection of functions and tools for use on process effect accumulation. Initial development was conducted by researchers at the University of Wollongong and has contributed to PhD and Masters projects.


Please note that the codebase has been updated substantially. 
```
    include /build/jar/TextSeer-version.jar in your project. 
```

Basic users:
    > TextSeer is currently a set of functions that can be used to develop systems that take advantage of semantic effect accumulation. In the SRC directory in the base directory there are a number of examples for use of the library functions.

* **Process Similarity**
> *Written by Evan Morrison, a new process similarity measure using Stanford NLP.*


* **Abductive Example**
> *Written by Evan Morrison, an abductive reasoner, will take in a knowledgebase, some possible actions effects (facts) and an observation. The reasoner will then find possible actions that could be performed together.*

* **Accumulation Example**
> *Written by Evan Morrison, accumulation demonstrates a method for conducting belief update across effects annotated to a business process model. The procedure is documented in research papers by Hinge et. al. 2009, Morrison et. al. 2011, 2014*

* **BPMN2 Model Loading Example**
> *Written by Evan Morrison, demonstrates the use of bpmn model loading and graph checking functions.*

* **Decision Free Graph Converter**
> *Written by Evan Morrison, documented in work described in Morrison et. al. 2011, 2014. This demonstrates the process for loading a graph and then removing all xor decisions from the graph.*

* **Default Logic Example**
> *Written by Evan Morrison, a default logic reasoner based on Reiters Default logic, this is a lightweight and incomplete implementation of a default logic reasoner. No decision procedure has been documented for this tool and it will only produce results for a subset of default logic problems.*

* **Logic Example**
> *Written by Evan Morrison, demonstrating the implemented sat solver and basic logic functions inherited from the Orbital Library. This demonstrates the use of a pairwise belief update accumulation function.*  

* **Order Constrained Permutation**
> *Written by Evan Morrison, templated functions written to compute permutations that hold combination based orders.* 

* **Semantic Tracing**
> *Written by Xiong Wen, an implementation of a tracing application.*

   
For basic configuration options edit 'base.properties'
For more configuration options edit src.config.Settings.java

Notes:
Most example processes are stored in the '/models/' directory.

```
********************************************************************
If you get an error about logger not found, copy the file 
'log4j.properties' to your bin directory.
IF YOU GET AN ERROR PLEASE CHECK IMPORT.log for details of error.
********************************************************************
```



Features:
---------
* Basic XML reading of BPMN 2.0 files
* Orbital Based Effect Accumulation
* Basic Internal Sub-process processing


Updates
-------
 - 2014-04-08 -- Major updates. Refactored out the code from collections, added posets
 - 2014-03-07 -- Minor updates. Added deductive closure operation. Future TODO: Add config option to turn this off as it is computational expensive to run. 
 - 2014-02-18 -- Minor Updates. Added a build file finally. Work on both a MAS process simulator and WEB GUI frontend has begun and will be bought into the project over the coming months.
 - 2013-11-07 -- Started documenting code after adding default logic reasoner.
 - 2013-04-12 -- Bought in code for Model combination and loading BPMN2.0 files.

Todo
-------
 - Get rid of jbpt - it is stupid!
 - Finish distance functions
 - Add compliance checking functions
 - Add realization functions
 - Add optimisation

--------------------------------------------------
 Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
Fork me on GitHub 


This tool uses the Orbital Java Library downloaded from 
http://symbolaris.com/download.html

    Copyright (c) 1996-2009 Andre Platzer. All rights reserved.

License Agreement:

This software is the confidential and proprietary information
of Andre Platzer. ("Confidential Information"). You
shall not disclose such Confidential Information and shall use
it only in accordance with the terms of the license agreement
you entered into.

NO WARRANTIES ARE GIVEN for any function (express or implied) these
files and documents might or might not have. The licenser shall never
be liable for any damage suffered by licensee or any third party as
a result of using these files or derivatives.

You are not to modify, adapt, translate, reverse engineer, decompile,
disassemble, or reverse-engineer the product or attempt to discover
its source code. You agree to notify your employees and agents who
may have access to the software of the restrictions contained in this
Agreement and to insure their compliance with these restrictions.

Distribution and use of these files and documents is prohibited unless
you make sure that:
  (o) this License Agreement and Copyright notice is contained,
  (o) the files are not modified,
  (o) you clearly indicate that you use our files, and
  (o) you give credit to Andre Platzer in all work resulting from the
      use of these files.

