A chat application with following requirements:
Server: 
-Allows multiple clients to be connected
-Has a proper GUI, which shows connected users with IP addresses and nicknames
-Forwards connection requests to target users, gets the responses, informs the requester
-After the connection, delivers the messages to the participants of the chat, where each chat may contain only two users.
Client: 
-Connects to server
-Gets the current list of online users, by their nicknames
-Sends a request to chat with a particular user identified by nickname
-Accepts/rejects incoming chat request
-Sents/receives messages from that users if request is accepted.
-Leaves the chat
-Direct connection between clients is not allowed. All messages are transferred over the server. 
