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

