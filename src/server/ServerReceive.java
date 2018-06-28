package server;
import javax.swing.*; 
import java.io.*; 
import java.net.*;
import java.util.Date;
import java.util.Random;
import java.text.SimpleDateFormat;  
/*   * �������շ���Ϣ����  */  
public class ServerReceive extends Thread {  
   JTextArea textarea;
   JTextField textfield;
   JComboBox combobox;
   Node client;
   UserLinkList userLinkList;//�û�����
   public boolean isStop;
   String filename;

   public ServerReceive(JTextArea textarea,JTextField textfield,JComboBox combobox,Node client,UserLinkList userLinkList){ 
       this.textarea = textarea; 
       this.textfield = textfield;
      this.client = client;
      this.userLinkList = userLinkList;
      this.combobox = combobox;
     isStop = false;  
     }
   public void run(){    
         //�������˷����û����б�
      sendUserList();
      while(!isStop && !client.socket.isClosed()){ 
          try{ 
             String type = (String)client.input.readObject();    
                if(type.equalsIgnoreCase("������Ϣ")){ 
                 String toSomebody = (String)client.input.readObject();
                String message = (String)client.input.readObject();
                if(message.equalsIgnoreCase("�����ļ�")) {
                	receiveFile(client.input);
                	                  
     
                 if(toSomebody.equalsIgnoreCase("������")){ 

                         sendToAllFile(filename);//�������˷�����Ϣ   
                     }   
                        else{  
                            try{     
                             client.output.writeObject("������Ϣ");
                             client.output.flush();
                             String mess=client.username+" "+ "�� "+ toSomebody+ "�������ļ�"+ "\n";
                             client.output.writeObject(mess);
                             client.output.flush();
                 
                       }  
                     catch (Exception e){  
                         //System.out.println("###"+e);   
                       }  
                 Node node = userLinkList.findUser(toSomebody); 
                 if(node != null){ 
                 		
                        node.output.writeObject("������Ϣ"); 
                        node.output.flush(); 
                        node.output.writeObject("�����ļ�"); 
                        node.output.flush(); 
                        sendFile(filename,node);
                       } 
                  } 
                	
                }
                else {
            String msg = client.username+" "+ "�� "+ toSomebody+ " ˵ : "+ message+ "\n";
              
                textarea.append(msg); 
            if(toSomebody.equalsIgnoreCase("������")){ 
                    sendToAll(msg);//�������˷�����Ϣ   
                   }   
                   else{  
                       try{     
                        client.output.writeObject("������Ϣ");
                        client.output.flush();
                        client.output.writeObject(msg);
                        client.output.flush();           	   
                  }  
                catch (Exception e){  
                    //System.out.println("###"+e);   
                  }  
            Node node = userLinkList.findUser(toSomebody); 
            if(node != null){ 
            	
                      node.output.writeObject("������Ϣ"); 
                   node.output.flush();      
                        node.output.writeObject(msg); 
                    node.output.flush();   
                  } 
             }   
           }  
         }
         else if(type.equalsIgnoreCase("�û�����")){   
             Node node = userLinkList.findUser(client.username); 
            userLinkList.delUser(node);
         String msg = "�û� " + client.username + " ����\n";
                 int count = userLinkList.getCount();
          combobox.removeAllItems();
           combobox.addItem("������");
           int i = 0; 
            while(i < count){   
                 node = userLinkList.findUser(i);  
                 if(node == null) {   
                      i ++;       
                         continue; 
                }
              combobox.addItem(node.username);  
                 i++;  
             } 
            combobox.setSelectedIndex(0);
           textarea.append(msg);
           textfield.setText("�����û�" +  userLinkList.getCount() + "��\n");
           sendToAll(msg);//�������˷�����Ϣ 
            sendUserList();//���·����û��б�,ˢ�� 
         break; 
         }   
    }     catch (Exception e){    
                 //System.out.println(e); 
            }  
 } 
}    
/*    * �������˷�����Ϣ   */   
public void sendToAll(String msg){
  int count = userLinkList.getCount();      
   int i = 0;   
   while(i < count){   
      Node node = userLinkList.findUser(i); 
     if(node == null) {    
              i ++;    
              continue;   
       }   
       try{	
       node.output.writeObject("������Ϣ");
       node.output.flush();
       node.output.writeObject(msg);
       node.output.flush();
    } 
     catch (Exception e){   
          //System.out.println(e); 
     }    
     i++;  
    } 
}  
/*    * �������˷����û����б�   */   

public void sendToAllFile(String msg){
	  int count = userLinkList.getCount();      
	   int i = 0;   
	   while(i < count){   
	      Node node = userLinkList.findUser(i); 
	     if(node == null) {    
	              i ++;    
	              continue;   
	       }   
	       try{	
	    	   node.output.writeObject("������Ϣ"); 
               node.output.flush(); 
               node.output.writeObject("�����ļ�"); 
               node.output.flush(); 
               sendFile(msg,node);
	    } 
	     catch (Exception e){   
	          //System.out.println(e); 
	     }    
	     i++;  
	    } 
	}  
public void sendUserList(){  
    String userlist = "";
   int count = userLinkList.getCount();
   int i = 0;  
   while(i < count){   
        Node node = userLinkList.findUser(i);
      if(node == null) {   
              i ++;   
              continue;  
        }    
         userlist += node.username; 
       userlist += '\n';  
        i++; 
   }    
     i = 0; 
   while(i < count){    
          Node node = userLinkList.findUser(i); 
        if(node == null) {  
             i ++;    
              continue;   
          }  
        try{    
             node.output.writeObject("�û��б�");
          node.output.flush();  
           node.output.writeObject(userlist); 
           node.output.flush(); 
        }   
         catch (Exception e){    
             //System.out.println(e);
           }   
          i++;  
       } 
    }
public void receiveFile(ObjectInputStream input) throws IOException{
	   byte[] inputByte = null;  
       int length = 0;  
   
       FileOutputStream fos = null;  
       String filePath = "E:/temp/"+GetDate.getDate()+"SJ"+new Random().nextInt(10000)+".txt";  
       filename=filePath;
       try {  
           try {  
             
               File f = new File("E:/temp");  
               if(!f.exists()){  
                   f.mkdir();    
               }  
         
               fos = new FileOutputStream(new File(filePath));      
               inputByte = new byte[1024];     
               System.out.println("��ʼ��������...");    
               if((length = input.read(inputByte, 0, inputByte.length)) > 0) {  
                   fos.write(inputByte, 0, length);  
                   fos.flush(); 
                   System.out.println(length);
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
public void sendFile(String f, Node node) throws IOException {
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
                node.output.write(sendBytes, 0, length);  
                node.output.flush();  
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

}
class GetDate {  
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");  
    public static  String getDate(){  
        return df.format(new Date());  
    }    
}  

