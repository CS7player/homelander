package com.cs7player.lan_chat;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PeerStore {

 private static final Map<String, Peer> peers = new ConcurrentHashMap<>();

 public static void addPeer(String ip, Peer peer) {
  peers.put(ip, peer);
 }

 public static void removePeer(String ip) {
  peers.remove(ip);
 }

 public static boolean hasPeer(String ip) {
  return peers.containsKey(ip);
 }

 public static Peer getPeer(String ip) {
  return peers.get(ip);
 }

 public static Map<String, Peer> getPeers() {
  return peers;
 }

 public static Collection<Peer> getUsers() {
  return peers.values();
 }
}