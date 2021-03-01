# regexp_corpora

#### `dollar_program.java`: 
Program that identifies dollar amounts.
- including words like million or billion
- including numbers and decimals
- including dollar signs, the words “dollar”, “dollars”, “cent” and “cents"
- including US dollars and optionally other types of dollars
- excluding currencies that are not stated in terms of dollars and cents (e.g., ignore yen, franc, etc.)

This program will return each match of the regular expression into an output file,`dollar_output.txt`, one match per line.


#### `dollar_output.txt`:
This .txt file stores all the returned match of the regular expression of the file,`dollar_program.java`, one match per line.


#### `telephone_regexp.java`:  
Program that identifies telephone numbers.
- including telephone numbers with area code. (eg. (xxx) xxx-xxxx or xxx-xxx-xxxx or xxx/xxx-xxxx )
- including telephone numbers without area code. (eg. xxx-xxxx )

This program will return each match of the regular expression into an output file,`telephone_output.txt`, one match per line.


#### `dollar_output.txt`:
This .txt file stores all the returned match of the regular expression of the file,`telephone_regexp.java`, one match per line.


#### `all_OANC.txt`:
The training corpus used to develop the system.


#### `test_dollar_phone_corpus.txt`:
The test corpus. This will be run on when the program is finished writing, and the result will be submited for grading


