# Service Discovery (Eureka) on PCF / PWS Example with backend service hidden (no-route) and using container to continer networking

## 1.0 Build and Deploy the Front End Application 

```sh
cd fronend
gradle build
cf push frontend -p build/libs/frontend-0.0.1-SNAPSHOT.jar --random-route
```

Note the randomly generated route.

## 2.0 Create and Bind a Service Discovery Service 

To view available services:
```sh
cf marketplace
```

Create the Service:
```sh
cf create-service p-service-registry trial my-service-registry
```

Bind the Service to your front-end app (you may need to wait a bit for the service to fully initialize)
```sh
cf bind-service frontend my-service-registry
```

Restage your application to use the new Service Registry bindings.

```sh
cf restage frontend
```

## 3.0 Build and deploy the Back End Application

```sh
cd ..
cd backend
gradle build
cf push backend -p build/libs/backend-0.0.1-SNAPSHOT.jar --no-route
```

## 4.0 Bind the backend to the Service Discovery Service 

```sh
cf bind-service backend my-service-registry
```

Restage your application to use the new Service Registry bindings.

```sh
cf restage backend
```

## 5.0 Verify Registration of both the Front End and Back End application in the Service Discovery Service

In PCF AppsMan .. select your Service Discovery Service and click on "manage"

You should see 2 registed Apps -- underneath *UP*, you will be able to see Service Discovery destinations -- one should be IP based (direct), and the other one should be route based.

## 6.0 Add a Network Policy that will enable App to App communication (skipping the Go-Router)

With PWS (Pivotal Web Services) users will have access to do this. For other PCF instances, if you are unable to add such network policy, contact your PCF Ops team to enable access and container-to-container networking. 

```sh
cf add-network-policy frontend --destination-app backend
```

## 7.0 Test your Frontend App for Backend connectivity. 

The */call* endpoint will call the Backend Service (it's backend endpoint) and return it's message.

The */service* endpoint will list all found Servies in the Service Discovery Service and their individual routes or IP's.

## Notes:

1. Using the Spring Cloud Services Starters for Service Registry will make all application endpoints secured. You should use your own authentication system or disable it like I did in my examples (NOT recommended for production) in your application.properties:
```properties
security.basic.enabled=false
```

2. Deploying Apps with "--no-route" also hides the Actuator endpoints from being easily accesible with the Go-Router. 

3. To make RestTemplate calls with URI's from the Service Discovery instance , insure that your application (in this case Frontend) has the @EnableDiscoveryClient annotation and that your RestTemplate bean has the @LoadBalanced annotation.

4. By default in PCF, Services are registered with the route (GoRouter) .. To register direcly against IPS's the registation method has to be changed to direct (in your application.properties).
```properties
spring.cloud.services.registrationMethod=direct
```

5.  **Important !!** .. unlike the Go-Router (PCF routing) which provides almost instant updates to routing when a container / instance is removed. Using the Discovery Service can cause lags of up to a minute due to client side caching, and routing lookups only being updated when a scheduled heart beatfails.

6. To disable registeration automatic registration with Eureka (but when you still want to use it for lookup), you can disable auto-registration in your application.properties.

```properties
spring.cloud.service-registry.auto-registration.enabled=false 
```
In our case, no service requires lookups to frontend, so we can disable it's registration from Eureka to reduce noise.
