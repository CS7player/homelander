package com.cs7player.lan_chat;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import org.json.JSONObject;
import java.net.*;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

public class UDP {
 private static final int PORT = 41234;
 private DatagramSocket socket;
 private WifiManager.MulticastLock multicastLock;

 public interface UserFoundListener {
   void onUserFound(String ip, String username, int port);
 }

 public void startDiscovery(Context context,String username,UserFoundListener listener) throws Exception {

  WifiManager wifi = (WifiManager) context
    .getApplicationContext()
    .getSystemService(Context.WIFI_SERVICE);

  multicastLock = wifi.createMulticastLock("lan_chat_lock");
  multicastLock.acquire();
  socket = new DatagramSocket(PORT);
  socket.setBroadcast(true);
  Timer timer = new Timer();
  timer.scheduleAtFixedRate(
    new TimerTask() {
     @Override
     public void run() {
      try {
       InetAddress broadcast = getBroadcastAddress();
       if (broadcast == null) {return; }
       JSONObject obj = new JSONObject();
       obj.put("type", "DISCOVER");
       obj.put( "username", username);
       obj.put( "ip", getLocalIp());
       obj.put( "port", 8080);
       byte[] data = obj.toString().getBytes();
       DatagramPacket packet = new DatagramPacket(data,data.length,broadcast,PORT);
       socket.send(packet);
      } catch (Exception e) {
       e.printStackTrace();
      }
     }
    },0,3000);
  Thread receiver = new Thread(() -> {
   byte[] buffer = new byte[1024];
   while (true) {
    try {
     DatagramPacket packet = new DatagramPacket( buffer, buffer.length);
     socket.receive(packet);
     String message = new String(packet.getData(),0,packet.getLength());
     JSONObject data = new JSONObject(message);
     if ("DISCOVER".equals(data.getString("type"))) {
      String ip = data.optString("ip",packet.getAddress().getHostAddress());
      String foundUsername = data.getString("username");
      int port = data.getInt("port");
      listener.onUserFound( ip, foundUsername, port);
     }
    } catch (Exception e) {
     e.printStackTrace();
    }
   }
  });
  receiver.start();
 }

 public String getLocalIp() throws SocketException {
  Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
  while (interfaces.hasMoreElements()) {
   NetworkInterface ni = interfaces.nextElement();
   if (!ni.isUp() || ni.isLoopback()) { continue;}
   for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
    InetAddress addr = ia.getAddress();
    if (addr instanceof Inet4Address) {
     String ip = addr.getHostAddress();
     if (ip.startsWith("192.168.56.")) { continue;}
     if (ip.startsWith("169.254.")) {continue;}
     return ip;
    }
   }
  }
  return "127.0.0.1";
 }

 private InetAddress getBroadcastAddress() throws SocketException {
  Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
  while (interfaces.hasMoreElements()) {
   NetworkInterface ni = interfaces.nextElement();
   if (!ni.isUp() || ni.isLoopback()) {continue; }
   for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
    InetAddress broadcast = ia.getBroadcast();
    if (broadcast != null) { return broadcast;}
   }
  }
  return null;
 }
}