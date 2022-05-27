# GARL
Genetic Based Selection Forced Reinforcement Learning

Run using Java 1.8.

Download GARL-X-XX.zip (latest) and extract contents into a folder you intend to use.

Note: `Game' Parameters and constantly changing so genomes optimised for previous game criteria may not perform well within the current environment and may require millions of thoughts and epochs to evolve to an optimal solution.

For screen saver mode: You can start it via:
```
java -jar GARL-1.0-SNAPSHOT-jar-with-dependencies.jar
```

For an interactive mode: You can start it via:
```
java -cp GARL-1.0-SNAPSHOT-jar-with-dependencies.jar garl.GARLTask
```
Note: While in an interactive mode, you may need to manually run payouts if you're running 1.30 or earlier. 
This is a work in progress. Payouts *work* on this version! Make sure to change your config.properties file to suite your own wallet.
A formal windows ScreenSaver is being worked on, it will be a compiled SCR file with a setup file that will allow you to specify your wallet details and location of genomes / config as it is not possible to install other files due to administrative rights to C:/GARL where a SCR file must reside.

I've provided hundreds of "fit/train" evolved genomes. To use these, expand a folder in the top level ./genomes/ inside of this directory should be the collection of hundreds of genomes

You must also add the config.properties file to your root. Your program should look as follows:

```
/GARL/
     GARL-1.0-SNAPSHOT-jar-with-dependencies.jar
     config.properties
     /genomes/1647404319361-genome-6eb067a5-4adb-45f8-8ab8-3761d2b3994c.json
     /genomes/1652710848907-2-epoch.json
     /genomes/...
```
Note: Requires Java Swing Installed and a Desktop.
Note: For support please visit https://discord.gg/qMkfbxG
Note: You can reach me, Sean Beecroft (xagau through github, or through discord, or at my personal email: seanbeecroft@gmail.com)

# Whats next?

- Learning through proper back-propagation
- Adding full SARSA, Expected SARSA, Q-Learning functions as valid node functions.
- Adding multiple goals that can be aligned, so they must be hit in a target.