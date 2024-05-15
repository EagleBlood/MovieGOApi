# MovieGO API

Custom SpringBoot API,

> [!IMPORTANT]
> For schema that works **ONLY** for [MovieGO](https://github.com/EagleBlood/MovieGO) you need to switch to [```main-android```](https://github.com/EagleBlood/MovieGOApi/tree/main-android) branch.

## Getting Started
To ensure successful execution of API server, the following prerequisites must be met:

* Java 8 or higher
* Maven

## 1. Installing
1. Clone the repository to your local machine
```bash
git clone https://github.com/EagleBlood/MovieGOApi.git
```

2. Navigate to the project directory:
```bash
cd MovieGOApi
```

3. Clean build install:
```bash
mvn clean install
```

## 2. Setup

After succesfull build in ```/src/main/java/connection/Connect.java``` you must assign valid url for your MySql Database and provide existing login and password within your DB workplace.

> [!NOTE]
> You can build DB structure from scratch using provided [scheme](), or use provided raw SQL files to import.

If everything was done acordingly you should connect to your DB and start making request at both [MovieGONative](https://github.com/EagleBlood/MovieGONative) and [MovieGOAdmin](https://github.com/EagleBlood/MovieGOAdmin) applications.
