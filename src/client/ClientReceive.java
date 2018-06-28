package client;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.io.*;
import javax.swing.*;

import java.util.Date;
import java.util.Random;
import java.text.SimpleDateFormat;  
public class ClientReceive extends Thread{
	private JComboBox combobox;
	private JTextArea textarea;
	Socket socket;
	ObjectOutputStream output;
	ObjectInputStream input;
	JTextField showStatus;
	public ClientReceive(
			Socket socket,ObjectOutputStream output,ObjectInputStream input,
			JComboBox combobox,JTextArea textarea,JTextField showStatus
			){
		this.socket=socket;
		this.output=output;
		this.combobox=combobox;
		this.textarea=textarea;
		this.showStatus=showStatus;
		this.input=input;
	}
	public void run() {
		while(!socket.isClosed()) {
			try {
				String type=(String)input.readObject();
				if(type.equalsIgnoreCase("ϵͳ��Ϣ")){
					String message=(String)input.readObject();
						textarea.append("ϵͳ��Ϣ:"+message);
				}
				else if(type.equalsIgnoreCase("����ر�")) {
					output.close();
					input.close();
					socket.close();
					textarea.append("�������ѹر�\n");
					break;
				}
				else if(type.equalsIgnoreCase("������Ϣ")) {
					String message = (String)input.readObject();
					if(message.equalsIgnoreCase("�����ļ�")){
						receiveFile(input);
					}else {
						textarea.append(message);
					}
				}
				else if(type.equalsIgnoreCase("�û��б�")) {
					String userList=(String)input.readObject();
					String usernames[]=userList.split("\n");
					combobox.removeAllItems();
					int i=0;
					combobox.addItem("������");
					while(i<usernames.length) {
						combobox.addItem(usernames[i]);
						i++;
					}
					combobox.setSelectedIndex(0);
					showStatus.setText("�����û�"+usernames.length+"��");
				}
			}catch(Exception e) {
				System.out.println(e);
		}
		}	
	}
	public void receiveFile(ObjectInputStream input) throws IOException{
		   byte[] inputByte = null;  
	       int length = 0;  
	       FileOutputStream fos = null;  
	       String filePath = "E:/cilent/"+GetDate.getDate()+"SJ"+new Random().nextInt(10000)+".txt";  
	       try {  
	           try {  
	               File f = new File("E:/cilent");  
	               if(!f.exists()){  
	                   f.mkdir();    
	               }         
	               fos = new FileOutputStream(new File(filePath));      
	               inputByte = new byte[1024];     
	               System.out.println("��ʼ��������...");    
	               if((length = input.read(inputByte, 0, inputByte.length)) > 0) {  
	                   fos.write(inputByte, 0, length);  
	                   fos.flush(); 
	               }  
	               System.out.println("��ɽ��գ�"+filePath);  
	           } finally {  
	        	   if (fos != null)  
	                   fos.close(); 	          
	           }  
	       } catch (Exception e) {  
	           e.printStackTrace();  
	       }  
	   }  
}
class GetDate {  
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");  
    public static  String getDate(){  
        return df.format(new Date());  
    }    
}  