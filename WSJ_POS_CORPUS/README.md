# WSJ_POS_CORPUS

There are 2 possible versions of each file:

-  file.pos -- there are two columns separated by a tab:
   - 1st column: token
   - 2nd column: POS tag
   - Blank lines separate sentences.

   This is the format of training files, system output, and development or test files used for scoring purposes.

-  file.words -- one token per line, with blank lines between sentences.
   Format of an input file for a tagging program.


### How to run this program:
Use the following command to run my system.

1. To compile the file, use `javac zh1130_HW3.java`

2. To run my system, use `java zh1130_HW3 WSJ_02-21.pos WSJ_23.words`
My program takes two command line arguments. The first one is the name of the develop corpus, and the second one is the name of the test corpus. Thus, the first command line argument can be replaced by the name of any develop corpus. Similarly, the second command line argument can be replaced by the name of any test corpus.

3. Check the file named "submission.pos" to see the result

The way I used to handle OOV is simply give every out of vocabulary word a default likelihood `1/1000`.



### File details:

#### `WSJ_02-21.pos`  
To use as the training corpus.


#### `WSJ_24.words` 
To use as the development set (for testing the system).


#### `WSJ_24.pos`
To use to check how well the system is doing.


#### `WSJ_23.words`  
To run the system on. The output file named `submission.pos` will be in the .pos format.


#### `submission.pos`
The output file in the .pos format.


#### `score.py`
This is a scorer which can be used on the development corpus. 

The scoring command is: 

`python3 score.py WSJ_24.pos WSJ_24_sys.pos`

assuming that the system output is called WSJ_24_sys.pos

This will give you an accuracy score for further debugging and tuning.

