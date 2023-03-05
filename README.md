# How to run

```bash
# Start RabbitMQ
docker compose up -d

# For Consistent Hash Exchange
QUEUE=com.aveiga.consistent-hash.myqueue1 mvn spring-boot:run
QUEUE=com.aveiga.consistent-hash.myqueue2 mvn spring-boot:run 
QUEUE=com.aveiga.consistent-hash.myqueue3 mvn spring-boot:run

# For Super Streams
QUEUE=com.aveiga.superStream.parallel-0 mvn spring-boot:run
QUEUE=com.aveiga.superStream.parallel-1 mvn spring-boot:run
QUEUE=com.aveiga.superStream.parallel-2 mvn spring-boot:run 
```