package client;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

//��Ա������Ϣ
public class login extends JFrame {
    public login() {
        init();
    }
    String userInputIp;
	int userInputPort;
    String userInputName;
    JPanel panel1, panel2, panel3;
    JTextField userName, inputIp, inputPort;
    JButton save=new JButton();
	JButton cancel=new JButton();
	JLabel DLGINFO1=new JLabel("Ĭ���û�����巹���");
	JLabel DLGINFO2=new JLabel("Ĭ��IPΪ  127.0.0.1");
	JLabel DLGINFO3=new JLabel("Ĭ�϶˿ں�8888");
    public void init() {
    	this.setSize(600, 400);
    	save.setText("��¼");
		cancel.setText("ȡ��");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        panel2 = new JPanel();
        panel2.setLayout(new GridLayout(4, 3, 0, 60));
        //�ؼ�JPanel��ͷ
        Border titleBorder1 = BorderFactory.createTitledBorder("��¼����");
        panel2.setBorder(titleBorder1);
        inputPort = new JTextField(15);
        userName = new JTextField(15);
        inputIp = new JTextField(15);
        panel2.add(new JLabel("    �û�����"));
        panel2.add(userName);
        panel2.add(DLGINFO1);
        panel2.add(new JLabel("    IP��ַ:"));
        panel2.add(inputIp);
        panel2.add(DLGINFO2);
        panel2.add(new JLabel("    �˿ںţ�"));
        panel2.add(inputPort);
        panel2.add(DLGINFO3);
        panel2.add(save);
        panel2.add(new JLabel("      "));
        panel2.add(cancel);
        this.add("Center", panel2);
        panel3 = new JPanel();
        panel3.setLayout(new GridLayout(2, 0));
        panel3.add(new JLabel("     "));
        panel3.add(new JLabel("     "));
        this.add("South", panel3);
        this.setVisible(true);
    
    save.addActionListener
	(
			new ActionListener() {
				public void actionPerformed(ActionEvent a) {
					if(userName.getText().equals("")) {
						 DLGINFO1.setText("�û�������Ϊ��");
						 userName.setText(userInputName);
						 return;
						}
						else if(userName.getText().length()>10) {
							DLGINFO1.setText("�û������Ȳ��ܳ���10��");
							userName.setText(userInputName);
							return;
						}
						userInputName=userName.getText();
					int savePort;
					String inputIP;
					try{
						userInputIp=""+InetAddress.getByName(inputIp.getText());
						userInputIp=userInputIp.substring(1);
					}catch(UnknownHostException e) {
						DLGINFO2.setText("wrong IP");
						return;
					}
					try {
						savePort=Integer.parseInt(inputPort.getText());
						if(savePort<1||savePort>65535) {
							DLGINFO3.setText("�����˿���0~65535֮��");
							inputPort.setText("");
							return;
						}
						userInputPort=savePort;
	
					}catch(NumberFormatException e) {
						DLGINFO3.setText("����Ķ˿ںţ��˿ں�����д����");
						inputPort.setText("");
						return;
					}
						dispose();
						ChatClient app=new ChatClient(userInputName,userInputIp,userInputPort);
						app.Connect();
						}
			}
			);
	cancel.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
			);
	}
    public static void main(String[] args) {
        new login();
    }

}