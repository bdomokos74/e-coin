# e-coin
BlockChain Implementation with Java.

```
docker-compose ps  
docker volume ls

docker-compose stop db 
docker-compose rm db
docker volume rm e-coin_db-data
```

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
