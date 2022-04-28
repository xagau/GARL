# GARL
Genetic Based Selection Forced Reinforcement Learning

Run using Java 1.8.

You can start it via:
```
java -jar GARL-1.0-SNAPSHOT-jar-with-dependencies.jar
```

This is a work in progress. Payouts *work* on this version!

I've provided hundreds of "fit/train" evolved genomes. To use these, expand a folder in the top level ./genomes/ inside of this directory should be the collection of hundreds of genomes

You must also add the config.properties file to your root. Your program should look as follows:

```
/GARL/
     GARL-1.0-SNAPSHOT-jar-with-dependencies.jar
     config.properties
     /genomes/1647404319361-genome-6eb067a5-4adb-45f8-8ab8-3761d2b3994c.json
     /genomes/...
```
Note: Requires Java Swing Installed and a Desktop.
