# Splitty - OOPP Team 57

This repository contains our JavaFX/Spring implementation of the **Splitty** project.

The project includes the Gradle wrapper (`gradlew`), so there is no need to install Gradle to run the app.

## Getting Started

To get started with running the application, follow these steps:

1. Clone the repository to your local machine:
```bash
$ git clone git@gitlab.ewi.tudelft.nl:cse1105/2023-2024/teams/oopp-team-57.git
```

2. Navigate to the project directory:
```bash
$ cd oopp-team-57
```

## Running the Application

Once you have navigated to the project directory, you can run the application using Gradle. Follow these steps:

1. Execute the following Gradle command to build and run the server:
```bash
$ ./gradlew bootRun     # on Linux/Mac
$ gradlew.bat bootRun   # on Windows
```

2. Execute the following Gradle command to build and run the client:
```bash
$ ./gradlew run     # on Linux/Mac
$ gradlew.bat run   # on Windows
```

*Note: The server should be running when the client is started.*

## Additional Commands

### Cleaning the Build

If you want to clean the build artifacts, you can use the following Gradle command:
```bash
$ ./gradlew clean     # on Linux/Mac
$ gradlew.bat clean   # on Windows
```

### Running Tests

To run the tests for the application, use the following Gradle command:
```bash
$ ./gradlew test      # on Linux/Mac
$ gradlew.bat test    # on Windows
```

## Documentation

### Setting Up the Config File

Once you have run the application once, a `config.properties` file will be generated. To use features like email sending, setting a preferred currency, or switching between Splitty servers, you need to edit properties in this file.

1. To set up email notifications, you need to set the following properties:
```
email_host=
email_port=
email_username=
email_password=
email_sender_email=
email_sender_name=
```

*Note: The application does not support SMTP with implicit SSL/TLS. Make sure to use an SMTP server that supports STARTTLS (usually runs on port 587).*

2. For changing the currency, you could change the `currency` property to one of the following options: `EUR` (default), `CHF`, `GBP`, `USD`.

3. To use a different Splitty server, change the `url` and `ws_url` properties.

### Navigation

To use keyboard navigation, you can press `TAB` multiple times until the button you wish to choose is highlighted in green. Also, when `BACKSPACE` or `ESPACE` is pressed, the previous page is shown.

### WebSockets and Long Polling

In our application, every resource is propagated with WebSockets. We use `ObservableList` to store every type of entity we use (Participants, Expenses, Tags, Debts), and whenever a client makes a change to one of them, it is propagated to all other open clients. An example of long polling is used for instantly propagating changes of the events on the admin screen.

### Admin Features

The admin screen can be accessed through the `Admin` button on the start screen, where the user is prompted for a password. The password is provided in the console of the server, printed out in the following way: `Generated Admin Password: <password>`. To enter the admin space, enter the password from the server console into the password field on the login screen.

### Extensions

We have implemented every extension for the project: Live Language Switch, Detailed Expenses, Foreign Currency, Open Debts, Statistics and Email Notification.

## Support

If you encounter any issues while running the application, have any questions, or wish to submit a new language, please feel free to [open an issue](https://gitlab.ewi.tudelft.nl/cse1105/2023-2024/teams/oopp-team-57/-/issues) in this repository.
