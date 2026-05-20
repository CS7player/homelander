package com.cs7player.lan_chat;
import org.java_websocket.WebSocket;

public class Peer {

 public String ip;
 public String username;
 public int port;
 public WebSocket conn;

 public Peer(String ip,String username,int port,WebSocket conn) {
  this.ip = ip;
  this.username = username;
  this.port = port;
  this.conn = conn;
 }

 @Override
 public String toString() {
  return username + " (" + ip + ":" + port + ")";
 }
}