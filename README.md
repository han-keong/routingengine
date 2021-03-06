# Setup

To install `RoutingEngine`, do the following:

  - Ensure that you have installed [Java SDK 11][java_sdk_11] at minimum.
  
  - Run the following commands on your Unix machine (e.g. MacOS) or on Windows with [Git Bash][git_bash]:
    ```
    git clone https://github.com/han-keong/routingengine
    
    cd routingengine
    
    javac -cp "lib/*" -d bin src/main/java/com/routingengine/*.java src/main/java/com/routingengine/*/*.java
    ```

[java_sdk_11]: https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
[git_bash]: https://www.atlassian.com/git/tutorials/install-git#windows


# Demonstration

* To see a demonstration of the routing engine, run the following command:

  - On your Unix machine (e.g. MacOS)
    ```
    java -cp "bin:lib/*" com.routingengine.examples.RoutingEngineExample 50000
    ```

  - On Windows with [Git Bash][git_bash]
    ```
    java -cp "bin\;lib/*" com.routingengine.examples.RoutingEngineExample 50000
    ```


* To run the server, run the following command:

  - On your Unix machine (e.g. MacOS)
    ```
    java -cp "bin:lib/*" com.routingengine.server.Server 50000
    ```

  - On Windows with [Git Bash][git_bash]
    ```
    java -cp "bin\;lib/*" com.routingengine.server.Server 50000
     ```


* To see a demonstration of the client, do the following:

  1. Ensure that the server is running in a separate terminal (see above on how to start the server)
  
  2. Run the following command:
    - On your Unix machine (e.g. MacOS)
      ```
      java -cp "bin:lib/*" com.routingengine.examples.ClientExample 50000
      ```

    - On Windows with [Git Bash][git_bash]
      ```
      java -cp "bin\;lib/*" com.routingengine.examples.ClientExample 50000
      ```

* After running each of the above commands, you can examine the output again by opening `logfile.txt` in the project root folder.


# Routing Engine

This routing engine solution is a server application which is based on [WebSockets][websockets].

It adopts a custom-made JSON request-response protocol, which has been designed for three kinds of users:

  1. [*Support Requests*](#support-request-methods) (submitted by customers)
  2. [*Agents*](#agent-methods)
  3. [*Admins*](#admin-methods) (administrators)

This protocol has a total of [20 methods][docs], which can be categorised by their intended users.

The following sections detail how the routing engine protocol works by explaining the purpose of each method to their respective users.

[websockets]: https://tools.ietf.org/html/rfc6455
[docs]: https://github.com/han-keong/routingengine/tree/master/src/test/docs


## Support Request Methods

*Support requests* are created by customers when they fill in a form on the customer support website and click the 'Submit' button.

Three fields are required:

  1. Name
  2. Email
  3. Type

With the necessary information, a [*new support request*][new_support_request] can be created by the customer's web client in the routing engine system.

A **UUID** (Universally Unique ID) will be assigned to the support request, which will be required to make further requests to the server.

Once the support request has been created, an agent **with the required skillset** needs to be assigned to the support request to cater to its type.

There may not be enough available agents at the moment, so the support request needs to [*wait for an agent*][wait_for_agent].

If the support request has been waiting for a certain period of time (30 seconds), its priority will be **incremented**.

When an agent has finally been assigned, the customer's web client will be connected to the agent through **Rainbow Unified Communications Platform**.

Rarely, the customer might want to [*change the type of his/her support request*][change_support_request_type].

In any case, once the support request has been serviced successfully, the customer or agent can proceed to [*close the support request*][close_support_request].

Sometimes, an *administrator* might want to [*check the status of a particular support request*][check_support_request].

The administrator might also need to [*remove a support request*][remove_support_request] for some reason.

### Summary of Support Request Methods

| Method                 | Command                                                                                                |
| ---------------------- | ------------------------------------------------------------------------------------------------------ |
| New Support Request    | `new_support_request {name:"bob", email:"bob@abc.com", type:1}`                                        |
| New Support Request    | `new_support_request {name:"bob", email:"bob@abc.com", type:"GENERAL_ENQUIRY"}`                        |
| New Support Request    | `new_support_request {name:"bob", email:"bob@abc.com", type:1, address:127.0.0.1}`                     |
| Wait For Agent         | `wait_for_agent {uuid:"<--valid support request uuid string-->"}`                                      |
| Change Support Request | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:1}`                 |
| Change Support Request | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:"GENERAL_ENQUIRY"}` |
| Close Support Request  | `close_support_request {uuid:"<--valid support request uuid string-->"}`                               |

[new_support_request]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/new_support_request_tests.md
[wait_for_agent]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/wait_for_agent_tests.md
[check_support_request]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/check_support_request_tests.md
[change_support_request_type]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/change_support_request_type_tests.md
[close_support_request]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/close_support_request_tests.md
[remove_support_request]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/remove_support_request_tests.md

## Agent Methods

An *agent* is typically a contact center employee, whose job is to take and service customers' support requests.

He/she needs to have a **specific skillset**, which can be set by the *administrator* when [*creating a new agent*][new_agent] in the routing engine system.

A **UUID** will be assigned to the agent, which will be required to make further requests to the server.

When the agent starts/stops working, he should [*update his/her availability*][update_agent_availability] in the routing engine system.

After that, he may start working by [*taking a support request*][take_support_request].

The routing engine will automatically assign a support request **with the highest priority** to the agent that he/she is **able to service with his current skillset**.

The agent can then proceed to service the customer's support request on his **Rainbow Unified Communications Platform** application.

In some occasions, the agent might be unable to service the customer's support request due to certain reasons, and has to [*drop the support request*][drop_support_request].

The support request will have to [wait for another agent][wait_for_agent], but its priority will be **doubled**.

However, in most occasions, the agent should be able to service the customer's support request successfully, and he/she may proceed to [close the support request][close_support_request].

After the agent has closed or dropped his support request, he can proceed to [take another support request][take_support_request], or he can stop working and [update his availability][update_agent_availability].

An agent can be [*activated/inactivated*][activate_agent] by an administrator according to demand from high/low support request traffic.

At any time, an administrator might also want to [*check the current status of an agent*][check_agent].

Occasionally, an agent might have received training in other skills, so an administrator might want to [*update the skillset of that agent*][update_agent_skills].

In the event that an agent is no longer working at the contact center, the administrator may [*remove the agent from the routing engine system*][remove_agent].

### Summary of Agent Methods

| Method                    | Command                                                                             |
| ------------------------- | ----------------------------------------------------------------------------------- |
| New Agent                 | `new_agent {skills:{1:true, 2:true}}`                                               |
| New Agent                 | `new_agent {skills:{"GENERAL_ENQUIRY":true, "CHECK_BILL":true}}`                    |
| New Agent                 | `new_agent {skills:{"GENERAL_ENQUIRY":true}, address:127.0.0.1}`                    |
| Update Agent Availability | `update_agent_availability {uuid:"<--valid agent uuid string-->", available:true}`  |
| Update Agent Availability | `update_agent_availability {uuid:"<--valid agent uuid string-->", available:false}` |
| Take Support Request      | `take_support_request {uuid:"<--valid agent uuid string-->"}`                       |
| Drop Support Request      | `drop_support_request {uuid:"<--valid agent uuid string-->"}`                       |

[new_agent]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/new_agent_tests.md
[update_agent_availability]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/update_agent_availability_tests.md
[take_support_request]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/take_support_request_tests.md
[drop_support_request]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/drop_support_request_tests.md
[activate_agent]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/activate_agent_tests.md
[check_agent]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/check_agent_tests.md
[update_agent_skills]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/update_agent_skills_tests.md
[remove_agent]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/remove_agent_tests.md


## Admin Methods

An *administrator* is somebody whose responsibility is to supervise the operations of the contact center.

When the routing engine server is initiated, he/she can [*ping*][ping] the server to check that it is in operation.

At any time, the administrator can oversee all support requests by [*checking the status of all support requests*][get_support_request_status].

The total number and **UUIDs** of all support requests for each of the following support request states will be listed:

| State of</br>support request | Support request</br>has been</br>closed | Support request</br>waiting in</br>queue | Support request</br>assigned to</br>an agent |
| :--------------------------: | :-------------------------------------: | :--------------------------------------: | :------------------------------------------: |
| Closed                       | Yes                                     | No                                       | No                                           |
| Open                         | No                                      | No                                       | No                                           |
| Waiting                      | No                                      | Yes                                      | No                                           |
| Assigned                     | No                                      | No                                       | Yes                                          |
| Total                        | Yes/No                                  | Yes/No                                   | Yes/No                                       |

The administrator may choose to [check the status of][check_support_request], [close][close_support_request], or even [remove][remove_support_request] a particular support request using its **UUID**.

Also, the administrator may want to oversee all agents by [*checking the status of all agents*][get_agent_status].

The total number and **UUIDs** of all agents for each of the following agent states will be listed:

| State of</br>agent | Agent has</br>not been</br>activated | Agent is</br>currently</br>available | Agent is</br>waiting for</br>support request | Agent is</br>assigned to a</br>support request |
| :----------------: | :----------------------------------: | :----------------------------------: | :------------------------------------------: | :--------------------------------------------: |
| Inactive           | Yes                                  | No                                   | No                                           | No                                             |
| Unavailable        | No                                   | No                                   | No                                           | No                                             |
| Available          | No                                   | Yes                                  | No                                           | No                                             |
| Waiting            | No                                   | No                                   | Yes                                          | No                                             |
| Assigned           | No                                   | No                                   | No                                           | Yes                                            |
| Total              | Yes/No                               | Yes/No                               | Yes/No                                       | Yes/No                                         |

The administrator may choose to [check the status of][check_agent], [activate/inactivate][activate_agent], or even [remove][remove_agent] a particular agent using its **UUID**.

If the administrator would like to examine how many support requests are currently in queue, he may [*check the status of the queue*][get_queue_status] as well.

The total number and **UUIDs** of all support requests in each queue will be listed. There will be a queue for each type of support request.

Lastly, if the administrator would just like to have a summary of all the numbers for each of the above, then he may simply [*get a status overview*][get_status_overview].

### Summary of Admin Methods

| Method                          | Command                                                                   |
| ------------------------------- | ------------------------------------------------------------------------- |
| Ping                            | `ping {}`                                                                 |
| Get All Support Requests Status | `get_support_request_status {}`                                           |
| Check Support Request Status    | `check_support_request {uuid:"<--valid support request uuid string-->"}`  |
| Close Support Request           | `close_support_request {uuid:"<--valid support request uuid string-->"}`  |
| Remove Support Request          | `remove_support_request {uuid:"<--valid support request uuid string-->"}` |
| Get All Agent Status            | `get_agent_status {}`                                                     |
| Check Agent Status              | `check_agent {uuid:"<--valid agent uuid string-->"}`                      |
| Activate Agent                  | `activate_agent {uuid:"<--valid agent uuid string-->", activate:true}`    |
| Activate Agent                  | `activate_agent {uuid:"<--valid agent uuid string-->", activate:false}`   |
| Remove Agent                    | `remove_agent {uuid:"<--valid agent uuid string-->"}`                     |
| Get Queue Status                | `get_queue_status {}`                                                     |
| Get Status Overview             | `get_status_overview {}`                                                  |

[ping]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/ping_tests.md
[get_support_request_status]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/get_support_request_status_tests.md
[get_agent_status]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/get_agent_status_tests.md
[get_queue_status]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/get_queue_status_tests.md
[get_status_overview]: https://github.com/han-keong/routingengine/blob/master/src/test/docs/get_status_overview_tests.md































