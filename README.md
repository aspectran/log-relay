Log Relayer
===================

A web application that tails log files and relays the results in real time to the log viewer via the websocket endpoint.

## Running Aspectow

- Clone this repository

  ```sh
  $ git clone https://github.com/aspectran/log-relayer.git
  ```

- Build with Maven

  ```sh
  $ cd log-relayer
  $ mvn clean package
  ```

- Run with Aspectran Shell

  ```sh
  $ cd app/bin
  $ ./shell.sh
  ```

- Access in your browser at http://localhost:8090
