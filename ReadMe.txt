TextSeerProcessTools
---------------------

A collection of functions and tools for use on process effect accumulation. 

** If you get an error about logger not found, copy the file 'log4j.properties' to your bin directory.

Basic users: 
	For basic configuration options edit 'base.properties'
Intermediate users:
	For more configuration options edit src.config.Settings.java
Advanced users:
	For even more configuration options edit each java source file in the au.edu.dsl.dlab... package. 

To load process repository - Store models in '/models/' directory. 
Files with .xml will be treated by BPMN parser
Files with .kb.txt will be treated as knowledge rules
Files with .acc are preprocessed effects for processes.


Features
 * Basic XML reading of BPMN 2.0 files (includes jGraph viewer, that reads from X,Y coords in original XML files)
 * Basic Java - Orbital Based Effect Accumulation (see below)
 * Basic Cycle Detection and removal (see below)
 * Basic Internal Sub-process processing
 * AND-Gateway Greedy effect accumulation
 * Random Process Generation




Notes on use
--------------
All sections of code have been implemented in basic Example programs. 

1) XML reading of BPMN2.0 files. 
I have based the implementation of BPMN2.0 standard files that have been generated using the Eclipse based Activiti process designer. 
This has been done as the designer can import Signavio based BPMN files. 

One fun feature to be aware of is the use of a 'save' function (edit base.properies to switch this off). When a process has had the effects scenarios 
computed, then they will be saved to a file in the repository called __FILENAME__.acc; in this file effect scenarios are delimeted using '###'.

*******************************************************************************************
!IMPORTANT NOTE!!IMPORTANT NOTE!!IMPORTANT NOTE!!IMPORTANT NOTE!!IMPORTANT NOTE!!IMPORTANT!
*******************************************************************************************
****To annotate effects, include the effects in the task 'Documentation' attribute. *******
*******************************************************************************************
!IMPORTANT NOTE!!IMPORTANT NOTE!!IMPORTANT NOTE!!IMPORTANT NOTE!!IMPORTANT NOTE!!IMPORTANT!
*******************************************************************************************


2) Orbital Effect Accumulation
This tool uses the orbital java library to provide reason support to compute:
 * pairWise effect accumulation
 * scenario effect accumulation
 * maximal effect subset computation
 * consistency checking
 * entailment checking 
See logicExample.java for demonstrations.

3) Basic Cycle detection. 
This tool uses the jGraphT java library to provide graph analysis functions. Included in this library is a cycle detection algorithm. 
TextSeerProcessTools uses cycleDetection during XML reading. If a cycle has been modeled into an imported file then the edge between the 
furthermost vertext from the start node and the closest vertex to the start node is removed. 

&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Needs Implementor for Addition to this function&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
This function can be fixed to make the cycle appear more relevant to the effect scenarios by removing all paths that start at Verticies that 
can not reach the end of the process without traversing to a node that appears earlier in the process. 
(See src.au.edu.dsl.dlab.processtools.parser.bpmn.Activiti.java myCycleFixer() for starting point). 

Node distance has been measured by converting the graph into a weighted directed graph and then calculating shortest path distance
to each node in the cycle. 

3) Basic Internal Sub-process processing
Simply, the tool will follow from a task or activity to a subProcess in a process model (this tool does not link across files). 
There is no support for error event processing at the current point in time. 


4) AND-Gateway Greedy effect accumulation
Using an order constrained permutation generation function, the tool will compute all interleavings of 
paths between AND gateways. Each path is added to the scenario processing function. 

5) Random Process Generation
A basic random process generator with randomised effect generation function has been included with this tool. 
There is no output file support for this tool. 


-----------------------------------------------------------------
-----------------------------------------------------------------
-----------------------------------------------------------------
-----------------------------------------------------------------
-----------------------------------------------------------------
-----------------------------------------------------------------


IF YOU GET AN ERROR PLEASE CHECK IMPORT.log for details of error.



-----------------------------------------------------------------
-----------------------------------------------------------------
-----------------------------------------------------------------
-----------------------------------------------------------------
-----------------------------------------------------------------

This software uses the GNU GPL v3
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007
Copyright © 2007 Free Software Foundation, Inc. <http://fsf.org/>
Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.