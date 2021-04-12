# Noun\_Group_tagger
This program uses similar data that is used for [WSJ\_POS\_CORPUS](../WSJ_POS_CORPUS). However, for this program we will focus more on feature selection than on an algorithm.

## How to run this program:
Use the following command to run my system.

1. To compile the file, use 
	
	`javac zh1130_HW5.java`

2. To get a training feature file, use 

	`java zh1130_HW5 WSJ_POS_CORPUS/WSJ_02-21.pos-chunk training.feature true`
	
3. To get a test feature file, use 
	`java zh1130_HW5 WSJ_POS_CORPUS/WSJ_23.pos test.feature false`

	My program takes three command line arguments. The first one is the name of the file that you want this program to take as an input; the second one is the name of the output file that you want it to be; the third one should be a boolean value: true if this file contains BIO tag, and false if this file doesn't contain BIO tag.  

4. Compile and run `MEtrain.java`, giving it the feature-enhanced training file as input; it will produce a MaxEnt model. `MEtrain` and `MEtest` use the maxent and trove packages, so you must include the corresponding jar files, `maxent-3.0.0.jar` and `trove.jar`, on the classpath when you compile and run.

	*  For **Linux**, **Apple** and other **Posix** systems, do:
		1. `javac -cp maxent-3.0.0.jar:trove.jar *.java` ### compiling
		2. `java -cp .:maxent-3.0.0.jar:trove.jar MEtrain training.feature model.chunk` ### creating the model of the training data
		3. `java -cp .:maxent-3.0.0.jar:trove.jar MEtag test.feature model.chunk response.chunk` ### creating the system output

	* For **Windows** Only -- Use semicolons instead of colons in each of the above commands, i.e., the command for Windows would be:
		1. `javac -cp maxent-3.0.0.jar;trove.jar *.java` ### compiling
		2. `java -cp .:maxent-3.0.0.jar;trove.jar MEtrain training.chunk model.chunk` ### creating the model of the training data
		3. `java -cp .:maxent-3.0.0.jar;trove.jar MEtag test.chunk model.chunk response.chunk` ### creating the system output
	
	*  Quick Fixes:
		* If the system is running out of memory, you can specify how much RAM java uses. For example, `java -Xmx16g -cp ...`will use 16 gigabytes of RAM.
		* If your system cannot find java files or packages and just doesn't run for that reason, the easiest fix is to run (the java steps) on one of NYU's linux servers.

5. Score the results with the python script as follows:
	`python score.chunk.py ../WSJ_CHUNK_FILES/WSJ_24.pos-chunk response.chunk` ### WSJ_24.pos-chunk is the answer key and response.chunk is the output file we get.

## File details:
 
###`/WSJ_CHUNK_FILES/WSJ_02-21.pos-chunk`
the training file

###`/WSJ_CHUNK_FILES/WSJ_24.pos`
the development file that you will test your system on

###`/WSJ_CHUNK_FILES/WSJ_24.pos-chunk`
the answer key to test your system output against

###`/WSJ_CHUNK_FILES/WSJ_23.pos`
the test file, to run your final system on, producing system output

### `/MAX_ENT_FILES/maxent-3.0.0.jar` 
maxent-3.0.0.jar, MEtag.java. MEtrain.java and trove.jar -- Java files for running the maxent training and classification programs.

### `/MAX_ENT_FILES/MEtag.java` 
maxent-3.0.0.jar, MEtag.java. MEtrain.java and trove.jar -- Java files for running the maxent training and classification programs.

### `/MAX_ENT_FILES/MEtrain.java`
maxent-3.0.0.jar, MEtag.java. MEtrain.java and trove.jar -- Java files for running the maxent training and classification programs.
  
### `/MAX_ENT_FILES/trove.jar`  
maxent-3.0.0.jar, MEtag.java. MEtrain.java and trove.jar -- Java files for running the maxent training and classification programs.

### `/MAX_ENT_FILES/score.chunk.py`
A python scoring script to score the results. 

Run the script from the command line using the following command:

`python score.chunk.py ../WSJ_CHUNK_FILES/WSJ_24.pos-chunk response.chunk`

WSJ_24.pos-chunk is the answer key and response.chunk is the output file we get.

This will give you an accuracy score for further debugging and tuning.

### `/MAX_ENT_FILES/WSJ_23.chunk`
output file from the test corpus `/WSJ_CHUNK_FILES/WSJ_23.pos`

