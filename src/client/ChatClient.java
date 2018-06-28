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
	String UserName="巹���";
	int type=0;// ����״̬
	
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
		this.setTitle("�������ͻ���");
		show();
	}
	public void init(String userInputName,String ip,int port) {
		this.ip=ip;
		this.UserName=userInputName;
		this.port=port;
		Container contentPane= getContentPane();
		contentPane.setLayout(new BorderLayout());
		JLabel DLGINFO1=new JLabel("�û�����"+UserName);
		JLabel DLGINFO2=new JLabel("IP��"+ip);
		JLabel DLGINFO3=new JLabel("PORT��"+port);
		 JPanel panel2 = new JPanel();
		  panel2.setLayout(new GridLayout(1, 3));
		  panel2.add(DLGINFO1);
		  panel2.add(DLGINFO2);
		  panel2.add(DLGINFO3);
		contentPane.add(panel2,BorderLayout.NORTH);
		combobox=new JComboBox();
		combobox.insertItemAt("������", 0);
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
		clientMessageButton.setText("����");
		
		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);
		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		sendToLabel=new JLabel("������");
		messageLabel=new JLabel("������Ϣ");
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
			JOptionPane.showConfirmDialog(this,"�������ӵ�������","��ʾ",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
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
			messageShow.append("���ӷ�����"+ip+":"+port+"�ɹ�...\n");
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
			output.writeObject("�û�����");
			output.flush();
			input.close();
			output.close();
			socket.close();
			messageShow.append("�������Ͽ�����...\n");
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
			output.writeObject("������Ϣ");
			output.flush();
			output.writeObject(toSomeBody);
			output.flush();//�ļ������ж�
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
		output.writeObject("�����ļ�");
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
	                System.out.println("�Ѵ��䣺"+((sumL/l)*100)+"%");  
	                output.write(sendBytes, 0, length);  
	                output.flush();  
	            }   
	            //��Ȼ�������Ͳ�ͬ����JAVA���Զ�ת������ͬ�������ͺ������Ƚ�  
	            if(sumL==l){  
	                bool = true;  
	            }  
	        }catch (Exception e) {  
	            System.out.println("�ͻ����ļ������쳣");  
	            bool = false;  
	            e.printStackTrace();    
	        } finally{    
	            if (fis != null)  
	            	fis.close();  
	        }  
	        System.out.println(bool?"�ɹ�":"ʧ��");  
	    }  


	public static void main(String args[] )throws Exception {
		
	}
}
	

