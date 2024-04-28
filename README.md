<a name="readme-top"></a>

<br />
<div style="text-align: center;">
  <h3 align="center">Mesh Network</h3>
  <p align="center">
   Lightest path from Origin to Destination.
    <br />
    <a href="https://github.com/lalitpo/mesh-network-service/issues">Report Bug</a> 
  </p>
</div>


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li><a href="#built-with">Built With</a></li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

This application aims to send information within a mesh network from Node ‘Origin’ to
Node ‘Destination’ in an efficient way.
Each Node has a unique identifier and a name (usually represented by city name) and its longitude and latitude as
coordinates.
A Node can have connections to other Nodes for which distance is calculated.

Below functionalities are implemented in the project:

1. See the list of all existing nodes
2. Create and delete nodes
3. Establish connections between two nodes and see the distance between them
4. See the list of all existing node connections. The list must be sortable by:
   a. any link node (alphabetically)
   b. distance
5. Know the optimal route between node origin and node destination

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Built With

[![My Skills](https://skillicons.dev/icons?i=java)](https://www.oracle.com/java/)
[![My Skills](https://skillicons.dev/icons?i=spring)](https://www.spring.io/)
[![My Skills](https://skillicons.dev/icons?i=maven)](https://maven.apache.org/)
[![My Skills](https://skillicons.dev/icons?i=postgres)](https://www.postgresql.org/)
[![My Skills](https://skillicons.dev/icons?i=docker)](https://www.docker.com/)


<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

To correctly import and run this project locally, please follow below guidelines and instructions for smooth development
process.

### Prerequisites

As mentioned above in the "Built with" section, please have Java installed on your system.
You can use [homebrew](https://brew.sh) or straightforward Installation from their respective website's homepage.

You can use any IDE like [IntelliJ](https://www.jetbrains.com/idea/), [VS Code](https://code.visualstudio.com), etc.

### Installation

Below is an example of how you can set up the project on your local machine.

1. In your favourite IDE, import/clone the project.
2. Build the project using the following command

    ```
    mvn clean install
    ```
3. Run docker containers for PostgreSQL database and Kafka Messaging Queue system.
   Docker containers for each of them are configured in docker-compose.yml file at
   lets-text-infra/docker/docker-compose.yml location.

    ```
    docker-compose -f mesh-network-infra/docker/docker-compose.yml up
    ```

4. Install [PostgreSQL](https://www.postgresql.org) on your machine for the database. You don't need an altogether a
   different UI to run queries because your IDE(IntelliJ, VS Code, etc.) will directly give you plugins to access them
   directly from the IDE.
   However, in case, you want a separate UI for it, use [pgAdmin](https://www.pgadmin.org)

   As the application is going to be used for locations/nodes and their connections, you must enable PostgreSQL
   extension PostGIS.
   For easier installation, run 'mesh-network-infra/mesh-network-db.sql' file in your PostgreSQL database.

   It already has the required tables and data for the application to run (you can create more.)
5.
6. Run the application by running file : ManAppApplication.java located at
   lets-text-app/src/main/java/com/rivada/meshnetworkapp/MeshNetworkAppApplication.java

    1. The application will be running on port 8080. You can access the application using the following URL:

        ```
        http://localhost:8080
        ```

    2. In your favourite REST API client, like [Postman](https://www.postman.com/), you can test below REST APIs to test
       the functionalities of the application.
    3. Below are the sample requests for the functionalities. You can
       replace these values with the values you want to test.

        ```
        1. Create Node
        POST http://localhost:8080/nodes/createNode
        {
            "cityName": "New York",
            "coordinates": [-74.0060,40.7128]
        }

        2. See the list of all existing nodes
        GET http://localhost:8080/nodes/getAllNodes

        3. Get Optimal Path
        GET http://localhost:8080/connections/getOptimalRoute?sourceNodeId=2&destinationNodeId=10
       
        4. See the list of all existing node connections. By default, it will be sorted by node Name (city Name). For sorting with distance, pass 'sort=D' in RequestParam
        GET http://localhost:8080/connections/getConnections?nodeId=15
        GET http://localhost:8080/connections/getConnections?nodeId=15&sort=D
       
        5. Establish connections between two nodes and see the distance between them.
        POST http://localhost:8080/connections/setConnection?sourceNodeId=8&destinationNodeId=15
       
        6. Delete Node
        DELETE http://localhost:8080/nodes/deleteNode?nodeId=25
        ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Developer :

[Lalit Poddar](mailto:lalit.poddar@gmail.com)  
[![LinkedIn][linkedin-shield]][linkedin-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links --> 

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555

[linkedin-url]: https://www.linkedin.com/in/lalit-poddar/