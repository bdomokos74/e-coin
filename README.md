# e-coin
BlockChain Implementation with Java.

1. Open the folder as project in InteliJ.
2. Change all database connections filepaths to your local machine.
3. Run.
If you want to run multiple peers: 
1. Copy Paste the same folder and open the multiple folders in Intelij in paralel
2. Make sure they don't share database connection filepaths.
3. Change local peer port and peer client ports for each copy accordingly. (They are hard coded in the code)

For More info check out [Chapter 7 of Introducing Blockchain with Java: Program, Implement, and Extend Blockchains with Java 1st ed. Edition](https://www.amazon.com/Introducing-Blockchain-Java-Implement-Blockchains/dp/1484279263/ref=sr_1_1?qid=1637096107&refinements=p_27%3ASpiro+Buzharovski&s=books&sr=1-1&text=Spiro+Buzharovski)

### Notes to run

- To run different instances: copy db dir to a separate run folder (e.g. run/p1/db, run/p2/db ...) and copy the Intellij run config with the corresponding working dir (e.g. run/p1)
- Set up ports, e.g. on p1:
`peer.port=6000;server.port=6001`
on p2:
`peer.port=6001;server.port=6000`

- https://blog.jetbrains.com/idea/2019/11/tutorial-reactive-spring-boot-a-javafx-spring-boot-application/
- https://harunzafer.com/blog/javafx-spring-boot-04
- 
- https://intellij-support.jetbrains.com/hc/en-us/articles/360006298560
