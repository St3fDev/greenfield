# Greenfield
![_e81c05d1-7155-4437-93bb-3a395fa477e8 (3)](https://github.com/St3fDev/greenfield/assets/75563711/1320430c-0261-41f1-b285-04b005732120)
Greenfield is a smart city where a fleet of robots operates to maintain cleanliness across its districts. These cleaning robots traverse the various districts, ensuring the streets remain tidy. Periodically, these robots require maintenance and visit the city's mechanic, which can only service one robot at a time. Additionally, each robot is equipped with a sensor to monitor air pollution levels within Greenfield. These pollution measurements are transmitted to an Administrator Server via MQTT, where they are collected and analyzed. The Administrator Server dynamically manages the registration and removal of cleaning robots from the system. Furthermore, it provides pollution data to the environmental department experts (Administrator Client) for further analysis and decision-making regarding environmental policies in Greenfield.
## Requirements

- Java 1.8 

- Gradle

## Compile and run

- Build: `gradle build`

- Run AdministratorClient: `gradle run`
