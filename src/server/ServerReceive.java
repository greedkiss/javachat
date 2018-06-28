package server;
import javax.swing.*; 
import java.io.*; 
import java.net.*;
import java.util.Date;
import java.util.Random;
import java.text.SimpleDateFormat;  
/*   * 服务器收发消息的类  */  
public class ServerReceive extends Thread {  
   JTextArea textarea;
   JTextField textfield;
   JComboBox combobox;
   Node client;
   UserLinkList userLinkList;//用户链表
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
         //向所有人发送用户的列表
      sendUserList();
      while(!isStop && !client.socket.isClosed()){ 
          try{ 
             String type = (String)client.input.readObject();    
                if(type.equalsIgnoreCase("聊天信息")){ 
                 String toSomebody = (String)client.input.readObject();
                String message = (String)client.input.readObject();
                if(message.equalsIgnoreCase("传输文件")) {
                	receiveFile(client.input);
                	                  
     
                 if(toSomebody.equalsIgnoreCase("所有人")){ 

                         sendToAllFile(filename);//向所有人发送消息   
                     }   
                        else{  
                            try{     
                             client.output.writeObject("聊天信息");
                             client.output.flush();
                             String mess=client.username+" "+ "对 "+ toSomebody+ "发送了文件"+ "\n";
                             client.output.writeObject(mess);
                             client.output.flush();
                 
                       }  
                     catch (Exception e){  
                         //System.out.println("###"+e);   
                       }  
                 Node node = userLinkList.findUser(toSomebody); 
                 if(node != null){ 
                 		
                        node.output.writeObject("聊天信息"); 
                        node.output.flush(); 
                        node.output.writeObject("发送文件"); 
                        node.output.flush(); 
                        sendFile(filename,node);
                       } 
                  } 
                	
                }
                else {
            String msg = client.username+" "+ "对 "+ toSomebody+ " 说 : "+ message+ "\n";
              
                textarea.append(msg); 
            if(toSomebody.equalsIgnoreCase("所有人")){ 
                    sendToAll(msg);//向所有人发送消息   
                   }   
                   else{  
                       try{     
                        client.output.writeObject("聊天信息");
                        client.output.flush();
                        client.output.writeObject(msg);
                        client.output.flush();           	   
                  }  
                catch (Exception e){  
                    //System.out.println("###"+e);   
                  }  
            Node node = userLinkList.findUser(toSomebody); 
            if(node != null){ 
            	
                      node.output.writeObject("聊天信息"); 
                   node.output.flush();      
                        node.output.writeObject(msg); 
                    node.output.flush();   
                  } 
             }   
           }  
         }
         else if(type.equalsIgnoreCase("用户下线")){   
             Node node = userLinkList.findUser(client.username); 
            userLinkList.delUser(node);
         String msg = "用户 " + client.username + " 下线\n";
                 int count = userLinkList.getCount();
          combobox.removeAllItems();
           combobox.addItem("所有人");
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
           textfield.setText("在线用户" +  userLinkList.getCount() + "人\n");
           sendToAll(msg);//向所有人发送消息 
            sendUserList();//重新发送用户列表,刷新 
         break; 
         }   
    }     catch (Exception e){    
                 //System.out.println(e); 
            }  
 } 
}    
/*    * 向所有人发送消息   */   
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
       node.output.writeObject("聊天信息");
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
/*    * 向所有人发送用户的列表   */   

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
	    	   node.output.writeObject("聊天信息"); 
               node.output.flush(); 
               node.output.writeObject("发送文件"); 
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
             node.output.writeObject("用户列表");
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
               System.out.println("开始接收数据...");    
               if((length = input.read(inputByte, 0, inputByte.length)) > 0) {  
                   fos.write(inputByte, 0, length);  
                   fos.flush(); 
                   System.out.println(length);
               }  
               System.out.println("完成接收："+filePath);  
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
                System.out.println("已传输："+((sumL/l)*100)+"%");  
                node.output.write(sendBytes, 0, length);  
                node.output.flush();  
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

}
class GetDate {  
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");  
    public static  String getDate(){  
        return df.format(new Date());  
    }    
}  

