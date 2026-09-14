# Sliding Window Log RateLimiter
Rate Limiter for highly scalable system in java
- Uses Spring Framework Tomcat embedded server.
  
1. The Entry Point (main method): This is the foundation. It boots up the Tomcat web server so your application can actually listen for traffic.
2. The Configuration: This is the plumbing. It sets up the JedisPool connection to Redis and wires up your rate limiter so it's ready to be used.
3. The Web Filter: This is the front door security guard. It stands in front of your API, uses the plumbing (the limiter), and decides who gets in.

Here is the complete, production-ready Spring Boot application code showing how all these pieces work together.

Since the application uses Jedis, it requires a running Redis instance to function properly for rate limiting. The easiest way to get Redis running without cluttering your system is using Docker.

How to Test
-----------

If you have Docker installed, simply run this command in your terminal. It will start a Redis container in the background mapped to the default port (6379):

```
docker run -d --name redis-db -p 6379:6379 redis
```
Build: `mvn clean package`

Run the application: `mvn spring-boot:run`

Open two browser tabs and navigate to http://localhost:8080 in both.

You should see the "Welcome to App" page in both tabs.

Refresh one of the tabs rapidly. After the second refresh, the third request will be blocked, and you will see a "429 Too Many Requests" error page in that tab.

The other tab will continue to work normally.

You can also test from the terminal using curl:

`for i in {1..10}; do curl -i http://localhost:8080/; echo ""; done`