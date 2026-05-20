package com.cs7player.lan_chat;
import android.util.Log;
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;
import java.net.InetSocketAddress;
import java.util.Map;

public class WSS extends WebSocketServer {

 public interface MessageListener {
  void onMessage(JSONObject data);
 }

 private final MessageListener listener;

 public WSS( int port, MessageListener listener) {
  super(new InetSocketAddress(port));
  this.listener = listener;
 }

 @Override
 public void onOpen( WebSocket conn, ClientHandshake handshake) {
  String ip = conn.getRemoteSocketAddress().getAddress().getHostAddress();
  Log.d("LAN_CHAT","CLIENT CONNECTED: " + ip);
 }

 @Override
 public void onClose( WebSocket conn, int code, String reason, boolean remote) {
  String ip = conn.getRemoteSocketAddress().getAddress().getHostAddress();
  PeerStore.removePeer(ip);
  Log.d("LAN_CHAT", "CLIENT DISCONNECTED");
 }

 @Override
 public void onMessage( WebSocket conn,String message) {
  try {
   JSONObject data = new JSONObject(message);
   String type = data.getString("type");
   String ip = conn.getRemoteSocketAddress().getAddress().getHostAddress();
   // INTRO
   if ("INTRO".equals(type)) {
    String username = data.getString("username");
    Peer peer = new Peer(ip,username,8080,conn);
    PeerStore.addPeer(ip, peer);
    Log.d( "LAN_CHAT", "INTRO: " + username);
    return;
   }
   // CHAT
   if ("CHAT".equals(type)) {
    Log.d( "LAN_CHAT", data.getString("from") + ": " + data.getString("message"));
    if (listener != null) {
     listener.onMessage(data);
    }
    // broadcast
    for (Map.Entry<String, Peer> entry : PeerStore.getPeers().entrySet()) {
     Peer peer = entry.getValue();
     if (peer.conn != null) {
      peer.conn.send(message);
     }
    }
   }
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 @Override
 public void onError( WebSocket conn,Exception ex) {
  ex.printStackTrace();
 }

 @Override
 public void onStart() {
  Log.d("LAN_CHAT", "SERVER STARTED");
 }
}