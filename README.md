# MessageSpecification
#### Message Specification project using Angular JS and Java (Spring)


### How to run Angular JS Front end:

cd FrontEnd 


and then run below command to run angular js Front UI on http-server host: localhost and port: 8000


http-server -a localhost -p 8000


(Please don't change the above port and host because it binds to spring origin controller, otherwise the spring web app wouldn't be able to accept the angular js connections!)

### Run java web war file present in the target folder


#### Most Important files are:

src -> main -> java -> shearwater -> controller -> SpecificationController (Containing all the JAVA Spring Controllers and processing logic of the application)


FrontEnd -> index.html (Containing Angular JS controllers and HTML UI components)


target -> ShearWaterProjArtifact-0.0.1-SNAPSHOT (WAR file to run the application)
