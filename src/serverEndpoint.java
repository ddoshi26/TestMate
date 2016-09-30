
package com.       // give the path
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/")  // give the path
public class ServerEndpoint {
	@OnOpen
     public void handleOpen() {
         System.out.println("Client now is connected!");
     }
     @OnMessage
     public String handleMessage(String message) {
     	 System.out.println("receive from clien " + message);
         String replyMessage = "echo" + message;
         System.out.println(replyMessage);
         return replyMessage;
     } 
     @OnClose
     public void handleClose() {
         System.out.println("Client now is disconnected!");
     }
}



