## Learn Reactive Project

This project is based on Udemy course "Build Reactive RESTFUL APIs using Spring Boot/WebFlux" 

[Udemy course link](https://www.udemy.com/course/build-reactive-restful-apis-using-spring-boot-webflux/)

### Note

Running:
```
> cd learn-reactivespring-client/
> mvn test
```

You'll get:
```
[INFO] Results:
[INFO] 
[ERROR] Failures: 
[ERROR]   ItemServiceTest.deleteNoItemUsingRetrive:257 Cannot mock responseSpec.onStatus when HttpStatus::is4xxClientError
[ERROR]   ItemServiceTest.editNoItemUsingRetrive:193 Cannot mock responseSpec.onStatus when HttpStatus::is4xxClientError
[INFO] 
[ERROR] Tests run: 14, Failures: 2, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.714 s
[INFO] Finished at: 2020-10-14T11:03:49+02:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.2:test (default-test) on project client-reactivespring: There are test failures.
...
```

This is due to a missing Mock operator when using:
- retrieve()
- onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new RuntimeException("No data found")))
- bodyToMono(Item.class).log("client service [RETRIVE]: ");

[See also @StackOverflow](https://stackoverflow.com/questions/59530512/mocking-org-springframework-web-reactive-function-client-webclient-responsespec)