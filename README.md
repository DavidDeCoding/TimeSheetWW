# TimeSheetWW
To submit the timesheet

To use this, 
Add a **config.properties** file in the root of the directory with:

url=<LOGIN_URL>

user=<YOUR_USER>

password=<YOUR_PASSWORD>

project=<YOUR_PROJECT>

task=<YOUR_TASK>

Note: No space or quotes.

Get your project + task values from time entry page. For example:
```
project=WWC2017.007 - Core (2017)
task=30 - Build & QA
```

Then,
Remove the commented out submit, *WARNING* test with it commented out first.

Finally,
Add do-time-sheet in your path. To run it, just run do-time-sheet
