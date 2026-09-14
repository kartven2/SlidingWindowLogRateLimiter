# Sliding Window Log RateLimiter
Rate Limiter for highly scalable system in java
- Uses Spring Framework Tomcat embedded server.
  
1. The Entry Point (main method): This is the foundation. It boots up the Tomcat web server so your application can actually listen for traffic.
2. The Configuration: This is the plumbing. It sets up the JedisPool connection to Redis and wires up your rate limiter so it's ready to be used.
3. The Web Filter: This is the front door security guard. It stands in front of your API, uses the plumbing (the limiter), and decides who gets in.

Here is the complete, production-ready Spring Boot application code showing how all these pieces work together.
