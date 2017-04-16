// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import com.example.afs.musicpad.message.Message;

public class MessageQueue {

  private Deque<Message> messageQueue;

  public MessageQueue(int backlog) {
    messageQueue = new LinkedBlockingDeque<>(backlog);
  }

  public void add(Message message) {
    while (!messageQueue.offerLast(message)) {
      Message lastMessage = messageQueue.pollFirst();
      System.out.println("Discarding " + lastMessage.getType());
    }
    doNotify();
  }

  public int getLastMessageNumber() {
    int lastMessageNumber = 0;
    Iterator<Message> iterator = messageQueue.descendingIterator();
    if (iterator.hasNext()) {
      Message message = iterator.next();
      lastMessageNumber = message.getNumber();
    }
    return lastMessageNumber;
  }

  public Message getMessage(int since) {
    Message message;
    while ((message = findFirstMessage(since)) == null) {
      doWait();
    }
    return message;
  }

  public Deque<Message> getMessages(int since) {
    Deque<Message> messages;
    while ((messages = getReadyMessages(since)) == null) {
      doWait();
    }
    return messages;
  }

  private synchronized void doNotify() {
    notifyAll();
  }

  private synchronized void doWait() {
    try {
      wait();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private Message findFirstMessage(int since) {
    Iterator<Message> iterator = messageQueue.iterator();
    while (iterator.hasNext()) {
      Message message = iterator.next();
      if (message.getNumber() > since) {
        return message;
      }
    }
    return null;
  }

  private Deque<Message> getReadyMessages(int since) {
    boolean inRange = true;
    Deque<Message> readyMessages = null;
    Iterator<Message> iterator = messageQueue.descendingIterator();
    while (iterator.hasNext() && inRange) {
      Message message = iterator.next();
      if (message.getNumber() > since) {
        if (readyMessages == null) {
          readyMessages = new LinkedList<>();
        }
        readyMessages.addFirst(message);
      } else {
        inRange = false;
      }
    }
    return readyMessages;
  }
}
