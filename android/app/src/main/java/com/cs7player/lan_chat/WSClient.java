package com.cs7player.lan_chat;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class WSClient extends WebSocketClient {

 private final String myUsername;
 private final String peerIp;

 public interface MessageListener {
  void onMessage(JSONObject data);
 }

 private final MessageListener listener;

 public WSClient(String ip,int port,String username,MessageListener listener) throws Exception {
  super(new URI("ws://" + ip + ":" + port));
  this.myUsername = username;
  this.peerIp = ip;
  this.listener = listener;
 }

 @Override
 public void onOpen(ServerHandshake handshake) {
  try {
   Log.d("LAN_CHAT", "CONNECTED: " + peerIp);
   JSONObject intro = new JSONObject();
   intro.put("type", "INTRO");
   intro.put("username", myUsername);
   send(intro.toString());
   PeerStore.addPeer(peerIp,new Peer( peerIp, "unknown", 8080, this));
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 @Override
 public void onMessage(String message) {
  try {
   JSONObject data = new JSONObject(message);
   String type = data.getString("type");
   if ("CHAT".equals(type)) {
    Log.d("LAN_CHAT",data.getString("from")  + ": "  + data.getString("message"));
    if (listener != null) {
     listener.onMessage(data);
    }
   }
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 @Override
 public void onClose(int code,String reason,boolean remote) {
  Log.d("LAN_CHAT", "DISCONNECTED");
  PeerStore.removePeer(peerIp);
 }

 @Override
 public void onError(Exception ex) {
  ex.printStackTrace();
 }
}