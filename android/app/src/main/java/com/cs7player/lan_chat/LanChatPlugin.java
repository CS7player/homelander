package com.cs7player.lan_chat;

import android.util.Log;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.JSObject;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.PluginMethod;

@CapacitorPlugin(name = "LanChat")
public class LanChatPlugin extends Plugin {

 private UDP udp;
 private WSS server;

 @PluginMethod
 public void start(PluginCall call) {
  String username = call.getString("username", "unknown");
  try {
   // START WS SERVER
   server = new WSS(8080, data -> {
    try {
     JSObject obj = new JSObject();
     obj.put("type", data.getString("type"));
     obj.put("from", data.getString("from"));
     obj.put("message", data.getString("message"));
     obj.put("timestamp", data.optLong("timestamp", System.currentTimeMillis()));
     notifyListeners("message", obj);
    } catch (Exception e) {
     e.printStackTrace();
    }
   });
   server.start();
   Log.d("LAN_CHAT", "WS SERVER STARTED");
   // START UDP
   udp = new UDP();
   String localIp = udp.getLocalIp();
   udp.startDiscovery(getContext(), username, (ip, foundUsername, port) -> {
    try {
     // SKIP SELF
     if (localIp.equals(ip)) {
      return;
     }
     // AVOID DUPLICATE PEERS
     if (PeerStore.hasPeer(ip)) {
      return;
     }
     // PREVENT DOUBLE CONNECTIONS
     if (localIp.compareTo(ip) > 0) {
      return;
     }
     Log.d("LAN_CHAT", "FOUND USER: " + foundUsername + " @ " + ip);
     WSClient client = new WSClient(ip, port, username, data -> {
      try {
       JSObject obj = new JSObject();
       obj.put("type", data.getString("type"));
       obj.put("from", data.getString("from"));
       obj.put("message", data.getString("message"));
       obj.put("timestamp", data.optLong("timestamp", System.currentTimeMillis()));
       notifyListeners("message", obj);
      } catch (Exception e) {
       e.printStackTrace();
      }
     });
     client.connect();
     // SEND PEER EVENT TO ANGULAR
     JSObject peer = new JSObject();
     peer.put("ip", ip);
     peer.put("username", foundUsername);
     peer.put("port", port);
     notifyListeners("peerFound", peer);
    } catch (Exception e) {
     e.printStackTrace();
    }
   });
   JSObject result = new JSObject();
   result.put("status", "started");
   call.resolve(result);
  } catch (Exception e) {
   call.reject(e.getMessage());
  }
 }

 @PluginMethod
 public void sendMessage(PluginCall call) {
  try {
   String username = call.getString("username");
   String message = call.getString("message");
   if (message == null || message.trim().isEmpty()) {
    call.reject("Message is empty");
    return;
   }
   // CREATE JSON MESSAGE
   org.json.JSONObject obj = new org.json.JSONObject();
   obj.put("type", "CHAT");
   obj.put("from", username);
   obj.put("message", message);
   obj.put("timestamp", System.currentTimeMillis());
   // SEND TO ALL CONNECTED PEERS
   for (Peer peer : PeerStore.getUsers()) {
    try {
     if (peer.conn != null && peer.conn.isOpen()) {
      peer.conn.send(obj.toString());
     }
    } catch (Exception e) {
     e.printStackTrace();
    }
   }
   JSObject result = new JSObject();
   result.put("success", true);
   call.resolve(result);
  } catch (Exception e) {
   call.reject(e.getMessage());
  }
 }

}