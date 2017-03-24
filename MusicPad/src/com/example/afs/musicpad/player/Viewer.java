// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Viewer extends JFrame {
  private JLabel topLine;
  private JLabel bottomLine;

  public Viewer() {
    Font font = new Font("Courier", Font.BOLD, 36);
    JPanel panel = new JPanel(new BorderLayout());
    topLine = new JLabel("Top");
    topLine.setFont(font);
    panel.add(topLine, BorderLayout.NORTH);
    bottomLine = new JLabel("Bottom");
    bottomLine.setFont(font);
    panel.add(bottomLine, BorderLayout.SOUTH);
    getContentPane().add(panel);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocation(100, 100);
    setSize(400, 100);
    setVisible(true);
  }

  public JLabel getBottomLine() {
    return bottomLine;
  }

  public JLabel getTopLine() {
    return topLine;
  }
}