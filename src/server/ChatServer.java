package server;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

public class ChatServer extends JFrame implements ActionListener{
     public static int port=8888;
     ServerSocket serverSocket;
     JComboBox combobox;
     JTextArea messageShow;
     JScrollPane messageScrollPane;
     JTextField showStatus; 
     JLabel sendToLabel,messageLabel;
     JTextField sysMessage;
     JButton sysMessageButton;  
     UserLinkList userLinkList;//用户链表    
  
     //建立菜单栏   
     JMenuBar jMenuBar =new JMenuBar();
     //建立菜单组   
     JMenu serviceMenu=new JMenu("服务(V)");
     //建立菜单项   
     JMenuItem portItem= new JMenuItem("端口设置(P)");
     JMenuItem startItem=new JMenuItem("启动服务(S)");
     JMenuItem stopItem=new JMenuItem("停止服务(T)");
     JMenuItem exitItem=new JMenuItem("退出(X)");
     JMenu helpMenu=new JMenu ("帮助(H)");   
     JMenuItem helpItem=new JMenuItem ("帮助(H)",new ImageIcon("face/HelpCenter.gif"));     

     //建立工具栏   
     JToolBar toolBar = new JToolBar();    

     //建立工具栏中的按钮组件   
     JButton portSet;//启动服务端侦听  
     JButton startServer;//启动服务端侦听  
     JButton stopServer;//关闭服务端侦听  
     JButton exitButton;//退出按钮    
     
     //框架的大小   
     Dimension faceSize = new Dimension(400, 400);  
     ServerListen listenThread;  
     JPanel downPanel;    
     GridBagLayout girdBag;   
     GridBagConstraints girdBagCon;  

public ChatServer(){   
     init();//初始化程序     

     //添加框架的关闭事件处理    
     this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
     this.pack();    
     //设置框架的大小    
     this.setSize(faceSize);   
     this.setVisible(true);     
     
     //设置运行时窗口的位置   
     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();    
     this.setLocation( (int) (screenSize.width - faceSize.getWidth()) / 2,(int) (screenSize.height-faceSize.getHeight()) / 2);    
     this.setResizable(false);
     this.setTitle("聊天室服务端");   
     //为服务菜单栏设置热键'V'    
     serviceMenu.setMnemonic('V');

     portItem.setMnemonic ('P');
     portItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_P,InputEvent.CTRL_MASK));      
  
     //为启动服务快捷键为ctrl+s   
     startItem.setMnemonic ('S');startItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_S,InputEvent.CTRL_MASK));      

     //为端口设置快捷键为ctrl+T   
     stopItem.setMnemonic ('T');
     stopItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_T,InputEvent.CTRL_MASK));      

     //为退出设置快捷键为ctrl+x   
     exitItem.setMnemonic ('X');
     exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));      
        helpMenu.setMnemonic('H');


     helpItem.setMnemonic('H');
     helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK));    }    

public void init(){
     Container contentPane =getContentPane();   
     contentPane.setLayout(new BorderLayout());

     //添加菜单栏
     serviceMenu.add(portItem);
     serviceMenu.add(startItem);
     serviceMenu.addSeparator();
     serviceMenu.add(stopItem);
     serviceMenu.addSeparator();
     serviceMenu.add(exitItem);
     jMenuBar.add(serviceMenu);
     helpMenu.add(helpItem);
     jMenuBar.add(helpMenu);
     setJMenuBar(jMenuBar);

     //初始化按钮    
     portSet=new JButton("端口设置");
     startServer=new JButton("启动服务");
     stopServer =new JButton("停止服务");
     exitButton=new JButton("退出");
     //将按钮添加到工具栏   
     toolBar.add(portSet);
     toolBar.addSeparator();//添加分隔栏   
     toolBar.add(startServer);
     toolBar.add(stopServer);
     toolBar.addSeparator();//添加分隔栏   
     toolBar.add(exitButton);
     contentPane.add(toolBar,BorderLayout.NORTH);

     //初始时，令停止服务按钮不可用   
     stopServer.setEnabled(false);
     stopItem .setEnabled(false);
   
     //为菜单栏添加事件监听    
     portItem.addActionListener(this);
     startItem.addActionListener(this);
     stopItem.addActionListener(this);
     exitItem.addActionListener(this);
     helpItem.addActionListener(this);

     //添加按钮的事件侦听    
     portSet.addActionListener(this);
     startServer.addActionListener(this);
     stopServer.addActionListener(this);
     exitButton.addActionListener(this);
     combobox = new JComboBox();
     combobox.insertItemAt("所有人",0);
     combobox.setSelectedIndex(0);
     messageShow = new JTextArea();
     messageShow.setEditable(false);

     messageScrollPane = new JScrollPane(messageShow,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);   
     messageScrollPane.setPreferredSize(new Dimension(400,400));
     messageScrollPane.revalidate();
     showStatus = new JTextField(35);
     showStatus.setEditable(false);
     sysMessage = new JTextField(24);
     sysMessage.setEnabled(false);
     sysMessageButton = new JButton();
     sysMessageButton.setText("发送");
     combobox=new JComboBox();
		combobox.insertItemAt("所有人", 0);
		messageShow = new JTextArea();
	     messageShow.setEditable(false);
		messageScrollPane=new JScrollPane(messageShow,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400,400));
		messageScrollPane.revalidate();
		
		sysMessage=new JTextField(23);
		sysMessage.setEnabled(true);
		sysMessageButton=new JButton();
		sysMessageButton.setText("发送");
		
		sysMessage.addActionListener(this);
		sysMessageButton.addActionListener(this);
		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		sendToLabel=new JLabel("发送至");
		messageLabel=new JLabel("发送消息");
		downPanel = new JPanel();
		downPanel.setLayout(new GridLayout(3, 2));
		downPanel.add(combobox);
		downPanel.add(new Label("    "));
		downPanel.add(sysMessage);
		downPanel.add(sysMessageButton);
		downPanel.add(showStatus);
		downPanel.add(new Label("    "));

		contentPane.add(messageScrollPane,BorderLayout.CENTER);
		contentPane.add(downPanel,BorderLayout.SOUTH);
     //添加系统消息的事件侦听    
       
     
     //关闭程序时的操作    
     this.addWindowListener(      
           new WindowAdapter(){
               public void windowClosing(WindowEvent e){      
                    stopService();      
                    System.exit(0);     
               }    
           }  
      );  
}  
/**    * 事件处理   */   
@SuppressWarnings("deprecation")   
public void actionPerformed(ActionEvent e) {   
      Object obj = e.getSource();    
      if (obj == startServer || obj == startItem) { 
             //启动服务 端     
             startService();   
      }    
      else if (obj == stopServer || obj == stopItem) { 
             //停止服务端     
             int j=JOptionPane.showConfirmDialog(     this,"真的停止服务吗?","停止服务",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);       
        if (j == JOptionPane.YES_OPTION){     
            stopService();    
        }   
      }    
      else if (obj == portSet || obj == portItem) { 
             //端口设置    
             //调出端口设置的对话框     
             PortConf portConf = new PortConf(this);    
             portConf.show();   
      }    
      else if (obj == exitButton || obj == exitItem) { 
             //退出程序     
             int j=JOptionPane.showConfirmDialog(     this,"真的要退出吗?","退出",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);       
             if (j == JOptionPane.YES_OPTION){     
                  stopService();     
                  System.exit(0);    
             }
      }    
 
      else if (obj == sysMessage || obj == sysMessageButton) { 
             //发送系统消息     
             sendSystemMessage();   
      }  
}  
/**    * 启动服务端   */   
public void startService(){   
      try{     
          serverSocket = new ServerSocket(port,10);     
          messageShow.append("服务端已经启动，在"+port+"端口侦听...\n");       
          startServer.setEnabled(false);
      startItem.setEnabled(false);
      portSet.setEnabled(false);
      portItem.setEnabled(false);
      stopServer .setEnabled(true);
      stopItem .setEnabled(true);
      sysMessage.setEnabled(true);   
      }    
      catch (Exception e){     
          //System.out.println(e);   
      }    
      userLinkList = new UserLinkList();     
      listenThread = new ServerListen(serverSocket,combobox,
   messageShow,showStatus,userLinkList);   
      listenThread.start();  }  
/**    * 关闭服务端   */
public void stopService(){   
      try{     
          //向所有人发送服务器关闭的消息
          sendStopToAll();     
          listenThread.isStop = true;
      serverSocket.close();
      int count = userLinkList.getCount();
     int i =0;
      while( i < count){
          Node node = userLinkList.findUser(i);
         node.input.close();
          node.output.close();
          node.socket.close();
         i ++;
      }
      stopServer .setEnabled(false);
      stopItem .setEnabled(false);
      startServer.setEnabled(true);
      startItem.setEnabled(true);
      portSet.setEnabled(true);
      portItem.setEnabled(true);
      sysMessage.setEnabled(false);
      messageShow.append("服务端已经关闭\n");
      combobox.removeAllItems();
      combobox.addItem("所有人");   
      }    
      catch(Exception e){     
          System.out.println(e);   
      }
}  
/**    * 向所有人发送服务器关闭的消息   */   
public void sendStopToAll(){    
      int count = userLinkList.getCount();   
      int i = 0;
    while(i < count){ 
        Node node = userLinkList.findUser(i);
       if(node == null) {     
                 i ++;
            continue;
       }     
           try{
                node.output.writeObject("服务关闭");     
                node.output.flush();
       }     
           catch (Exception e){      
                //System.out.println("$$$"+e);    
           }
      i++;   
       } 
}  
/**    * 向所有人发送消息   */   
public void sendMsgToAll(String msg){    
       int count = userLinkList.getCount();//用户总数   
       int i = 0;    
       while(i < count){     
             Node node = userLinkList.findUser(i);    
             if(node == null) {     
                  i ++;
             continue;
         }
         try{
            node.output.writeObject("系统信息");
            node.output.flush();
            node.output.writeObject(msg);
            node.output.flush();
         } 
          catch (Exception e){
            System.out.println("@@@"+e);
         }
         i++; 
       } 
       sysMessage.setText("");
}  
/**    * 向客户端用户发送消息   */   
public void sendSystemMessage(){  
      String toSomebody =  combobox.getSelectedItem().toString();
    String message = sysMessage.getText() + "\n";
    messageShow.append(message);   //向所有人发送消息
       if(toSomebody.equalsIgnoreCase("所有人")){
       sendMsgToAll(message);  
      } 
     else{    
         //向某个用户发送消息
      Node node = userLinkList.findUser(toSomebody);
      try{    
             node.output.writeObject("系统信息");
          node.output.flush();
          node.output.writeObject(message);
          node.output.flush();
      }  
       catch(Exception e){
          System.out.println("!!!"+e);
      } 
       sysMessage.setText("");//将发送消息栏的消息清空 
    } 
   }   
   public static void main(String[] args) {   
      ChatServer app = new ChatServer(); 
   } 
}