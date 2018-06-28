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
     UserLinkList userLinkList;//�û�����    
  
     //�����˵���   
     JMenuBar jMenuBar =new JMenuBar();
     //�����˵���   
     JMenu serviceMenu=new JMenu("����(V)");
     //�����˵���   
     JMenuItem portItem= new JMenuItem("�˿�����(P)");
     JMenuItem startItem=new JMenuItem("��������(S)");
     JMenuItem stopItem=new JMenuItem("ֹͣ����(T)");
     JMenuItem exitItem=new JMenuItem("�˳�(X)");
     JMenu helpMenu=new JMenu ("����(H)");   
     JMenuItem helpItem=new JMenuItem ("����(H)",new ImageIcon("face/HelpCenter.gif"));     

     //����������   
     JToolBar toolBar = new JToolBar();    

     //�����������еİ�ť���   
     JButton portSet;//�������������  
     JButton startServer;//�������������  
     JButton stopServer;//�رշ��������  
     JButton exitButton;//�˳���ť    
     
     //��ܵĴ�С   
     Dimension faceSize = new Dimension(400, 400);  
     ServerListen listenThread;  
     JPanel downPanel;    
     GridBagLayout girdBag;   
     GridBagConstraints girdBagCon;  

public ChatServer(){   
     init();//��ʼ������     

     //��ӿ�ܵĹر��¼�����    
     this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
     this.pack();    
     //���ÿ�ܵĴ�С    
     this.setSize(faceSize);   
     this.setVisible(true);     
     
     //��������ʱ���ڵ�λ��   
     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();    
     this.setLocation( (int) (screenSize.width - faceSize.getWidth()) / 2,(int) (screenSize.height-faceSize.getHeight()) / 2);    
     this.setResizable(false);
     this.setTitle("�����ҷ����");   
     //Ϊ����˵��������ȼ�'V'    
     serviceMenu.setMnemonic('V');

     portItem.setMnemonic ('P');
     portItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_P,InputEvent.CTRL_MASK));      
  
     //Ϊ���������ݼ�Ϊctrl+s   
     startItem.setMnemonic ('S');startItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_S,InputEvent.CTRL_MASK));      

     //Ϊ�˿����ÿ�ݼ�Ϊctrl+T   
     stopItem.setMnemonic ('T');
     stopItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_T,InputEvent.CTRL_MASK));      

     //Ϊ�˳����ÿ�ݼ�Ϊctrl+x   
     exitItem.setMnemonic ('X');
     exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));      
        helpMenu.setMnemonic('H');


     helpItem.setMnemonic('H');
     helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK));    }    

public void init(){
     Container contentPane =getContentPane();   
     contentPane.setLayout(new BorderLayout());

     //��Ӳ˵���
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

     //��ʼ����ť    
     portSet=new JButton("�˿�����");
     startServer=new JButton("��������");
     stopServer =new JButton("ֹͣ����");
     exitButton=new JButton("�˳�");
     //����ť��ӵ�������   
     toolBar.add(portSet);
     toolBar.addSeparator();//��ӷָ���   
     toolBar.add(startServer);
     toolBar.add(stopServer);
     toolBar.addSeparator();//��ӷָ���   
     toolBar.add(exitButton);
     contentPane.add(toolBar,BorderLayout.NORTH);

     //��ʼʱ����ֹͣ����ť������   
     stopServer.setEnabled(false);
     stopItem .setEnabled(false);
   
     //Ϊ�˵�������¼�����    
     portItem.addActionListener(this);
     startItem.addActionListener(this);
     stopItem.addActionListener(this);
     exitItem.addActionListener(this);
     helpItem.addActionListener(this);

     //��Ӱ�ť���¼�����    
     portSet.addActionListener(this);
     startServer.addActionListener(this);
     stopServer.addActionListener(this);
     exitButton.addActionListener(this);
     combobox = new JComboBox();
     combobox.insertItemAt("������",0);
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
     sysMessageButton.setText("����");
     combobox=new JComboBox();
		combobox.insertItemAt("������", 0);
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
		sysMessageButton.setText("����");
		
		sysMessage.addActionListener(this);
		sysMessageButton.addActionListener(this);
		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		sendToLabel=new JLabel("������");
		messageLabel=new JLabel("������Ϣ");
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
     //���ϵͳ��Ϣ���¼�����    
       
     
     //�رճ���ʱ�Ĳ���    
     this.addWindowListener(      
           new WindowAdapter(){
               public void windowClosing(WindowEvent e){      
                    stopService();      
                    System.exit(0);     
               }    
           }  
      );  
}  
/**    * �¼�����   */   
@SuppressWarnings("deprecation")   
public void actionPerformed(ActionEvent e) {   
      Object obj = e.getSource();    
      if (obj == startServer || obj == startItem) { 
             //�������� ��     
             startService();   
      }    
      else if (obj == stopServer || obj == stopItem) { 
             //ֹͣ�����     
             int j=JOptionPane.showConfirmDialog(     this,"���ֹͣ������?","ֹͣ����",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);       
        if (j == JOptionPane.YES_OPTION){     
            stopService();    
        }   
      }    
      else if (obj == portSet || obj == portItem) { 
             //�˿�����    
             //�����˿����õĶԻ���     
             PortConf portConf = new PortConf(this);    
             portConf.show();   
      }    
      else if (obj == exitButton || obj == exitItem) { 
             //�˳�����     
             int j=JOptionPane.showConfirmDialog(     this,"���Ҫ�˳���?","�˳�",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);       
             if (j == JOptionPane.YES_OPTION){     
                  stopService();     
                  System.exit(0);    
             }
      }    
 
      else if (obj == sysMessage || obj == sysMessageButton) { 
             //����ϵͳ��Ϣ     
             sendSystemMessage();   
      }  
}  
/**    * ���������   */   
public void startService(){   
      try{     
          serverSocket = new ServerSocket(port,10);     
          messageShow.append("������Ѿ���������"+port+"�˿�����...\n");       
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
/**    * �رշ����   */
public void stopService(){   
      try{     
          //�������˷��ͷ������رյ���Ϣ
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
      messageShow.append("������Ѿ��ر�\n");
      combobox.removeAllItems();
      combobox.addItem("������");   
      }    
      catch(Exception e){     
          System.out.println(e);   
      }
}  
/**    * �������˷��ͷ������رյ���Ϣ   */   
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
                node.output.writeObject("����ر�");     
                node.output.flush();
       }     
           catch (Exception e){      
                //System.out.println("$$$"+e);    
           }
      i++;   
       } 
}  
/**    * �������˷�����Ϣ   */   
public void sendMsgToAll(String msg){    
       int count = userLinkList.getCount();//�û�����   
       int i = 0;    
       while(i < count){     
             Node node = userLinkList.findUser(i);    
             if(node == null) {     
                  i ++;
             continue;
         }
         try{
            node.output.writeObject("ϵͳ��Ϣ");
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
/**    * ��ͻ����û�������Ϣ   */   
public void sendSystemMessage(){  
      String toSomebody =  combobox.getSelectedItem().toString();
    String message = sysMessage.getText() + "\n";
    messageShow.append(message);   //�������˷�����Ϣ
       if(toSomebody.equalsIgnoreCase("������")){
       sendMsgToAll(message);  
      } 
     else{    
         //��ĳ���û�������Ϣ
      Node node = userLinkList.findUser(toSomebody);
      try{    
             node.output.writeObject("ϵͳ��Ϣ");
          node.output.flush();
          node.output.writeObject(message);
          node.output.flush();
      }  
       catch(Exception e){
          System.out.println("!!!"+e);
      } 
       sysMessage.setText("");//��������Ϣ������Ϣ��� 
    } 
   }   
   public static void main(String[] args) {   
      ChatServer app = new ChatServer(); 
   } 
}