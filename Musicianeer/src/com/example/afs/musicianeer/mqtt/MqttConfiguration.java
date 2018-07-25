// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.mqtt;

public class MqttConfiguration {
  private String brokerUri;
  private String clientId;

  public String getBrokerUri() {
    return brokerUri;
  }

  public String getClientId() {
    return clientId;
  }

  public void setBrokerUri(String brokerUri) {
    this.brokerUri = brokerUri;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @Override
  public String toString() {
    return "MqttConfiguration [brokerUri=" + brokerUri + ", clientId=" + clientId + "]";
  }

}