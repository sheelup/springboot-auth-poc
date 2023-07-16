**BuildStep**

    git clone git@github.com:sheelup/springboot-auth-poc.git
    cd springboot-auth-poc
    mvn clean install
**Requirement**
Java 8, maven

**Run Step**
1) **Run with in-memory database (No external dependency) :**

execute below after build steps

    java -jar target/springboot-auth-poc.jar
2) **Run with MySQL database**

**Step1:** Install and run mysql8 [Below command can be useful if docker is installed]
    
    docker run --name mysql-instance -e MYSQL_ROOT_PASSWORD=confidencial --publish 6603:3306 -d mysql:latest

**Step2:** Create/Change mysql username and password as `root` and `DoNtTrY` respectively.

Alternative solution for Step2:   Update existing db username and password in `application-MYSQL.yaml` 

**Step3:** Log into mysql shell and create new database `mydb`

**Step4:** Run Jar with below command [run from same directory as where `mvn clean install` command executed ]

    java -Dspring.profiles.active=MYSQL -jar target/springboot-auth-poc.jar

**API Documentation:**

    http://localhost:8080/timzone-app/documentation/swagger-ui/index.html#/
