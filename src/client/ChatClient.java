package client;



import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame implements ActionListener{
	String ip="127.0.0.1";
	int port=8888;
	String UserName="宸拐子";
	int type=0;// 连接状态
	
	JComboBox combobox;
	JTextArea messageShow;
	JScrollPane messageScrollPane;
	JLabel sendToLabel,messageLabel;
	ClientReceive recvThread;
	JTextField clientMessage;
	JCheckBox checkbox;
	JButton clientMessageButton;
	JTextField showStatus;
	Socket socket;
	ObjectOutputStream output;
	ObjectInputStream input;
	
//	ClientReceive recvThread;
	
	JMenuBar jMenuBar=new JMenuBar();
	JToolBar toolBar=new JToolBar();

	Dimension faceSize=new Dimension(400,600);
	JPanel downpanel;
	GridBagLayout gridBag;
	GridBagConstraints gridBagCon;
	
	public ChatClient(String userInputName,String ip,int port) {
		init(userInputName,ip,port);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setSize(faceSize);
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(screenSize.width-faceSize.getWidth())/2, (int)(screenSize.height-faceSize.getWidth())/2);
		this.setResizable(false);
		this.setTitle("聊天器客户端");
		show();
	}
	public void init(String userInputName,String ip,int port) {
		this.ip=ip;
		this.UserName=userInputName;
		this.port=port;
		Container contentPane= getContentPane();
		contentPane.setLayout(new BorderLayout());
		JLabel DLGINFO1=new JLabel("用户名："+UserName);
		JLabel DLGINFO2=new JLabel("IP："+ip);
		JLabel DLGINFO3=new JLabel("PORT："+port);
		 JPanel panel2 = new JPanel();
		  panel2.setLayout(new GridLayout(1, 3));
		  panel2.add(DLGINFO1);
		  panel2.add(DLGINFO2);
		  panel2.add(DLGINFO3);
		contentPane.add(panel2,BorderLayout.NORTH);
		combobox=new JComboBox();
		combobox.insertItemAt("所有人", 0);
		messageShow = new JTextArea();
	     messageShow.setEditable(false);
		messageScrollPane=new JScrollPane(messageShow,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400,400));
		messageScrollPane.revalidate();
		
		clientMessage=new JTextField(23);
		clientMessage.setEnabled(true);
		clientMessageButton=new JButton();
		clientMessageButton.setText("发送");
		
		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);
		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		sendToLabel=new JLabel("发送至");
		messageLabel=new JLabel("发送消息");
		downpanel = new JPanel();
		downpanel.setLayout(new GridLayout(3, 2));
		downpanel.add(combobox);
		downpanel.add(new Label("    "));
		downpanel.add(clientMessage);
		downpanel.add(clientMessageButton);
		downpanel.add(showStatus);
		downpanel.add(new Label("    "));

		contentPane.add(messageScrollPane,BorderLayout.CENTER);
		contentPane.add(downpanel,BorderLayout.SOUTH);
		
		this.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if(type==1) {DisConnect();}
					System.exit(0);
				}
			}
		);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj=e.getSource();
		 if (obj==clientMessage||obj==clientMessageButton) 
		{	
			SendMessage();
			clientMessage.setText("");
		}	
	}
	public void Connect() {
		try {
			socket =new Socket(ip,port);
		}
		catch(Exception e){
			JOptionPane.showConfirmDialog(this,"不能连接到服务器","提示",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
			return;
		}
		try 
		{
			output= new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input=new ObjectInputStream(socket.getInputStream());
			output.writeObject(UserName);
			output.flush();
			recvThread= new ClientReceive(socket,output,input,combobox,messageShow,showStatus);
			recvThread.start();
			clientMessage.setEnabled(true);
			messageShow.append("连接服务器"+ip+":"+port+"成功...\n");
			type=1;
	}
			catch(Exception e) 
		{
			System.out.println("error"+e);
			return;
		}
	}
	public void DisConnect() {
		clientMessage.setEnabled(false);
		if(socket.isClosed()) {
			return;
		}
		try {
			output.writeObject("用户下线");
			output.flush();
			input.close();
			output.close();
			socket.close();
			messageShow.append("服务器断开连接...\n");
			type=0;
		}
		catch(Exception e) {}
	}
	
	public void SendMessage() {
		String toSomeBody=combobox.getSelectedItem().toString();
		String message=clientMessage.getText();
		File f=new File(message);
		if(socket.isClosed()) {
			return;
		}
		try {
			output.writeObject("聊天信息");
			output.flush();
			output.writeObject(toSomeBody);
			output.flush();//文件传输判断
			if(!f.exists()) {
			output.writeObject(message);
			output.flush();
			}else {
				sendFile(message);
			}
			}
		catch(Exception e) {
			
		}
		}
	public void sendFile(String f) throws IOException {
		output.writeObject("传输文件");
		output.flush();
		File file=new File(f);
		 int length = 0;  
	     double sumL = 0 ;  
	     byte[] sendBytes = null;
	     boolean bool = false; 
	     FileInputStream fis = null;
	     try {  
	            long l = file.length();   
	            fis = new FileInputStream(file);        
	            sendBytes = new byte[1024];    
	            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {  
	                sumL += length;    
	                System.out.println("已传输："+((sumL/l)*100)+"%");  
	                output.write(sendBytes, 0, length);  
	                output.flush();  
	            }   
	            //虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较  
	            if(sumL==l){  
	                bool = true;  
	            }  
	        }catch (Exception e) {  
	            System.out.println("客户端文件传输异常");  
	            bool = false;  
	            e.printStackTrace();    
	        } finally{    
	            if (fis != null)  
	            	fis.close();  
	        }  
	        System.out.println(bool?"成功":"失败");  
	    }  


	public static void main(String args[] )throws Exception {
		
	}
}
	

