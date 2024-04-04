# Greenfield
![alt text](https://www.bing.com/images/create/in-a-smart-city-named-greenfield2c-a-fleet-of-clean/1-660eacc1d61a4b408067137e9b618cf5?id=b94f09JrEP2pQ4Pm%2bemWjw%3d%3d&view=detailv2&idpp=genimg&idpclose=1&thId=OIG2.d7i4zdeWvFOoNAb2.xo.&FORM=SYDBIC)
Greenfield is a smart city where a fleet of robots operates to maintain cleanliness across its districts. These cleaning robots traverse the various districts, ensuring the streets remain tidy. Periodically, these robots require maintenance and visit the city's mechanic, which can only service one robot at a time. Additionally, each robot is equipped with a sensor to monitor air pollution levels within Greenfield. These pollution measurements are transmitted to an Administrator Server via MQTT, where they are collected and analyzed. The Administrator Server dynamically manages the registration and removal of cleaning robots from the system. Furthermore, it provides pollution data to the environmental department experts (Administrator Client) for further analysis and decision-making regarding environmental policies in Greenfield.
## Requirements

- Java 1.8 

- Gradle

## Compile and run

- Build: `gradle build`

- Run AdministratorClient: `gradle run`
