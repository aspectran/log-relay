Log Relayer
===================

Log Relayer can send the latest logs recorded on the server to the web browser through the web socket endpoint and provides an integrated view to observe the logs of several servers in real time.

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
