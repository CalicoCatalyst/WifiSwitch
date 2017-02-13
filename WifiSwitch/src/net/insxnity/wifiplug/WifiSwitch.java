package net.insxnity.wifiplug;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.SwingConstants;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.SystemColor;

public class WifiSwitch {

	private JFrame frmWifiswitchSettings;
	private JTextField textField;
	private JTextField textField_1;
	private static JButton btnOn;
	private JLabel lblStatus;
	private static JLabel lblIpaddress;
	private static JLabel lblConnectedstatus;
	private static MenuItem aboutPlug;
	public static CheckboxMenuItem plugOn;
	private static CheckboxMenuItem plugOff;
	
	private static String ipaddress;
	private static Integer port;
	private static TrayIcon trayIcon;
	
	private static WifiSwitch window;
	
	private static HS100 plug;
	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		try {
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new WifiSwitch();
					window.frmWifiswitchSettings.setVisible(false);
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
					errorOnConnect();
				}
			}
		});
		//Default IP
		ipaddress = "192.168.1.9";
		port = 9999;
		plug = new HS100(ipaddress, port);
        final PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(createImage("poweroff.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();
        
        aboutPlug = new MenuItem("IP: " + plug.getIp() + ":" + plug.getPort());
        plugOn = new CheckboxMenuItem("Plug on");
        plugOff = new CheckboxMenuItem("Plug off");
        
        plugOn.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED){
						turnOnPlug();
                	
                } else {
                }
            }
        });
        plugOff.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED){
						turnOffPlug();
                	
                } else {
                }
            }
        });
        
        aboutPlug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
					window.frmWifiswitchSettings.setVisible(true);
					window.frmWifiswitchSettings.setSize(600, 400);
            }
        });
        
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	if (plug.isOn()) {
						turnOffPlug();
					}
					else {
						turnOnPlug();
					}
                }
            }
        });
        
        if (plug.isOn()) {
        	plugOn.setEnabled(true);
			trayIcon.setImage(createImage("poweron.png", "on icon"));
        } else {
        	plugOff.setEnabled(true);
        }
        
        popup.add(aboutPlug);
        popup.addSeparator();
        popup.add(plugOn);
        popup.add(plugOff);
        
        trayIcon.setPopupMenu(popup);
        
        try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			errorOnConnect();
		}
        
        while (true) {
        	if (plug.isOn() && plugOff.isEnabled()) {
				turnOnPlug();
        	}
        	if (!plug.isOn() && plugOn.isEnabled()) {
        		turnOffPlug();
        	}
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
        }
		} catch (Exception e) {
			
		}
	}
	
	public static void refreshTaskBar() throws IOException {
        if (plug.isOn()) {
        	plugOn.setEnabled(true);
			trayIcon.setImage(createImage("poweron.png", "on icon"));
        } else {
        	plugOff.setEnabled(true);
        	trayIcon.setImage(createImage("poweroff.png", "on icon"));
        }
        aboutPlug.setLabel("IP: " + plug.getIp() + ":" + plug.getPort());
	}
	
	public static void refreshGUI() throws IOException {

		lblIpaddress.setText("IP: " + plug.getIp() + ":" + plug.getPort());
		lblConnectedstatus.setText((plug.isPresent() ? "Connected" : "Not Connected"));
		btnOn.setLabel(plug.isOn() ? "On" : "Off");
	}

	/**
	 * Create the application.
	 */
	public WifiSwitch() {
		initialize();
	}
	
	private static void errorOnConnect() {
		JOptionPane.showMessageDialog(null, "Could Not Connect to Plug. Please double check your connections", "Error", JOptionPane.INFORMATION_MESSAGE);
		trayIcon.setImage(createImage("poweroff.png", "off icon"));
	}
	
	public static boolean turnOnPlug() {
		boolean r = false;
		try {
			r = plug.switchOn();
		} catch (IOException e) {
			errorOnConnect();
			return false;
		}
		plugOn.setState(true);
		plugOff.setState(false);
		trayIcon.setImage(createImage("poweron.png", "on icon"));
		return r;
	}
	
	public static boolean turnOffPlug() {
		boolean r = false;
		try {
			r = plug.switchOff();
		} catch (IOException e) {
			errorOnConnect();
			return false;
		}
		plugOn.setState(false);
		plugOff.setState(true);
		trayIcon.setImage(createImage("poweroff.png", "off icon"));
		return r;
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWifiswitchSettings = new JFrame();
		frmWifiswitchSettings.getContentPane().setBackground(SystemColor.window);
		frmWifiswitchSettings.setBackground(SystemColor.window);
		frmWifiswitchSettings.setLocationByPlatform(true);
		frmWifiswitchSettings.getContentPane().setSize(new Dimension(600, 400));
		frmWifiswitchSettings.setTitle("WifiSwitch Settings");
		frmWifiswitchSettings.getContentPane().setLayout(null);
		frmWifiswitchSettings.setLocation(600, 300);
		
		JLabel lblIpAddress = new JLabel("Ip Address");
		lblIpAddress.setBounds(10, 11, 68, 14);
		frmWifiswitchSettings.getContentPane().add(lblIpAddress);
		
		JLabel lblPortshouldAlways = new JLabel("Port (Should Always Be 9999)");
		lblPortshouldAlways.setSize(new Dimension(600, 400));
		lblPortshouldAlways.setBounds(131, 11, 173, 14);
		frmWifiswitchSettings.getContentPane().add(lblPortshouldAlways);
		
		textField = new JTextField();
		textField.setText("192.168.1.9");
		textField.setBounds(10, 32, 86, 20);
		frmWifiswitchSettings.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setText("9999");
		textField_1.setBounds(131, 32, 86, 20);
		frmWifiswitchSettings.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setBackground(SystemColor.control);
		btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRefresh.setBounds(10, 63, 89, 23);
		frmWifiswitchSettings.getContentPane().add(btnRefresh);
		
		lblStatus = new JLabel("Status");
		lblStatus.setBounds(10, 97, 86, 14);
		frmWifiswitchSettings.getContentPane().add(lblStatus);
		
		lblIpaddress = new JLabel("ipaddress");
		lblIpaddress.setBounds(10, 122, 200, 14);
		frmWifiswitchSettings.getContentPane().add(lblIpaddress);
		
		lblConnectedstatus = new JLabel("connectedstatus");
		lblConnectedstatus.setBounds(10, 147, 112, 14);
		frmWifiswitchSettings.getContentPane().add(lblConnectedstatus);
		
		btnOn = new JButton("Off");
		btnOn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnOn.setBounds(10, 172, 59, 23);
		frmWifiswitchSettings.getContentPane().add(btnOn);
		
		btnOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (btnOn.getText() == "Off") {
					if (turnOnPlug()) btnOn.setText("On");
            	}
            	else {
            		if (turnOffPlug()) btnOn.setText("Off");
            	}
            }
        });
		
		btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
					refreshGUI();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
		
		
		
		JButton btnApply = new JButton("Apply");
		btnApply.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnApply.setBounds(386, 327, 89, 23);
		frmWifiswitchSettings.getContentPane().add(btnApply);
		btnApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	plug.setIp(textField.getText());
            	plug.setPort(Integer.valueOf(textField_1.getText().toString()));
            	try {
					refreshGUI();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
		
		JButton btnOk = new JButton("Ok");
		btnOk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnOk.setBounds(485, 327, 89, 23);
		frmWifiswitchSettings.getContentPane().add(btnOk);
		
		btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String ip = textField.getText();
            	Integer port = Integer.valueOf(textField_1.getText());
            	window.frmWifiswitchSettings.setVisible(false);
            	plug.setIp(ip);
            	plug.setPort(port);
            	try {
					refreshGUI();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
		
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnCancel.setBounds(287, 327, 89, 23);
		frmWifiswitchSettings.getContentPane().add(btnCancel);
		
		btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	window.frmWifiswitchSettings.setVisible(false);
            }
        });
		
		
		JLabel lblDesignedByInsxnity = new JLabel("Designed By Insxnity");
		lblDesignedByInsxnity.setLocation(new Point(600, 400));
		lblDesignedByInsxnity.setBounds(10, 331, 179, 14);
		frmWifiswitchSettings.getContentPane().add(lblDesignedByInsxnity);
		
		JLabel lblExitProgram = new JLabel("Exit Program");
		lblExitProgram.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblExitProgram.setHorizontalAlignment(SwingConstants.RIGHT);
		lblExitProgram.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblExitProgram.setBounds(462, 11, 112, 14);
		frmWifiswitchSettings.getContentPane().add(lblExitProgram);
		lblExitProgram.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	frmWifiswitchSettings.dispose();
                    final SystemTray tray = SystemTray.getSystemTray();
                    tray.remove(trayIcon);
                    System.exit(0);
                }
            }
        });
		
		try {
			refreshGUI();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		}
	}
	
	
	
    protected static Image createImage(String path, String description) {
        URL imageURL = WifiSwitch.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
