import { Injectable } from '@angular/core';
import { registerPlugin } from '@capacitor/core';

const LanChat = registerPlugin<any>('LanChat');

@Injectable({
 providedIn: 'root',
})
export class LanChatService {

 peers: any[] = [];
 messages: any[] = [];

 async start(username: string) {
  await LanChat.start({ username });
  // PEER FOUND
  LanChat.addListener('peerFound', (peer: any) => {
   console.log('PEER', peer);
   const exists = this.peers.find(p => p.ip === peer.ip);
   if (!exists) { this.peers.push(peer); }
  }
  );
  // MESSAGE
  LanChat.addListener('message', (msg: any) => {
   console.log('MESSAGE', msg);
   this.messages.push(msg);
  }
  );
 }

 async sendMessage(username: string, message: string) {
  if (!message.trim()) { return; }
  await LanChat.sendMessage({ username, message });
  // LOCAL ECHO
  this.messages.push({ from: username, message });
 }
}