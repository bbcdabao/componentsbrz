# <img src="https://github.com/bbcdabao/componentsbrz/blob/develop/docs/images/logo.svg" alt="A" width="60" height="60" title="Componentsbrz" >    Componentsbrz</img>

[![GitHub license](https://img.shields.io/github/license/bbcdabao/componentsbrz.svg)](https://github.com/bbcdabao/componentsbrz/blob/main/LICENSE)
[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/bbcdabao/componentsbrz.svg)](https://github.com/bbcdabao/componentsbrz)
[![GitHub release](https://img.shields.io/github/release/bbcdabao/componentsbrz.svg)](https://github.com/bbcdabao/componentsbrz/releases)

# Componentsbrz Background

I call it the component experimental field, which contains some interesting example and springboot component, which will continue to be added later.

The following introduces the naming rules and content overview. And make it easier for you to view this project.

- __Naming rules:__<br>
  - Directories ending in "brz" are springboot components. brz comes from "Subaru BRZ" which is a great car. And i like the letter ending "brz".<br>
  - Directories that do not end with "brz" are examples of using these components. These examples are more interesting.<br>

- __Compile:__<br>
  - Useing jdk 17 or above version.
  - Useing node 22.4.0.
  - run "mvn install" in the root directory "componentsbrz".

# Below is an introduction to all components

- __websocketbrz__<br>
  - [Component Description](./websocketbrz/README.md)
  - Example1:terminalhub<br>
  An example of remote SSH login developed using the websocketbrz component.
  <div style="display: flex; justify-content: space-between;">
    <img src="./docs/images/terminalhub-0.png" alt="" style="width: 49%;" />
    <img src="./docs/images/terminalhub-1.png" alt="" style="width: 49%;" />
  </div>
  <div style="display: flex; justify-content: space-between;">
    <img src="./docs/images/terminalhub-2.png" alt="" style="width: 49%;" />
    <img src="./docs/images/terminalhub-3.png" alt="" style="width: 49%;" />
  </div>
  <div style="display: flex; justify-content: space-between;">
    <img src="./docs/images/terminalhub-4.png" alt="" style="width: 49%;" />
    <img src="./docs/images/terminalhub-5.png" alt="" style="width: 49%;" />
  </div>
  <div style="display: flex; justify-content: space-between;">
    <img src="https://github.com/bbcdabao/componentsbrz/blob/develop/docs/images/terminalhubf-act.gif" alt="" width="99%"/>
  </div>
  
  - Example2:chatroom<br>
  An example of a chat room developed using the websocketbrz component.<br>
  First, run the jar file as the chat registration center: java -jar chatroom-1.0.0-SNAPSHOT.jar<br>
  Then open "wbclient.html" with a browser.

- __flowcontrolbrz__<br>
  - [Component Description](./flowcontrolbrz/README.md)
  - Example1: not yet<br>

- __messagebrz__<br>
  - [Component Description](./messagebrz/README.md)
  - Example1: not yet<br>
  
