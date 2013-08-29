package com.zoki.podsetnik.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.zoki.podsetnik.logika.Countdown;
import com.zoki.podsetnik.logika.SettingsPodsetnik;

public class MainGui extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TrayIcon trayIcon;
	SystemTray tray;
	PopupMenu popup;
	Image image_icon;
	JLabel message_jl;
	JTextArea textArea;
	public SettingsPodsetnik sp;
	String file_nema = "data/settings_podsetnik";
	String messagesfn = "data/message_text";
	ArrayList<String> messages;
	Random randomGenerator;
	Countdown countdown;
	JButton ok_button;

	public MainGui() {
		super("Reminder");

		try {
			init_settings();
			init_message_list();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			image_icon = Toolkit.getDefaultToolkit().getImage("data/Wall_clock.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
		add_fields();

		if (SystemTray.isSupported()) {

			tray = SystemTray.getSystemTray();

			add_popupmenue();

			trayIcon = new TrayIcon(image_icon, "Reminder", popup);
			trayIcon.setImageAutoSize(true);
		} else {

		}
		add_events();

		setIconImage(image_icon);
		setSize(400, 210);

		// set to centar of screan
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);

		setResizable(false);
		setVisible(true);
		
		start_counter();
	}

	private void init_message_list() {
		messages = new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(messagesfn));
			String line;

			while ((line = br.readLine()) != null) {
				messages.add(line);
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init_settings() {
		sp = new SettingsPodsetnik();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file_nema));
			String line;

			while ((line = br.readLine()) != null) {
				String val[] = line.split("=");

				if (val[0].equals(SettingsPodsetnik.TIMEOUT_TAG)) {
					sp.period = Long.parseLong(val[1]);
				} else if (val[0].equals(SettingsPodsetnik.PLAY_SOUND_TAG)) {
					sp.play_sound = val[1].equals("1") ? true : false;
				}
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void add_fields() {
		JPanel add_b = new JPanel();
		add_b.setBackground(Color.white);

		setBackground(Color.white);
		ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/smile-clock_2.png"));

		JLabel background = new JLabel("", icon, SwingConstants.CENTER);
		background.setSize(icon.getIconWidth() - 60, icon.getIconHeight() - 90);
		add_b.add(background);
		add(add_b, BorderLayout.NORTH);

		randomGenerator = new Random();
		int index = randomGenerator.nextInt(messages.size());

		try {
			textArea = new JTextArea(new String(messages.get(index).getBytes(), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		textArea = textAreaProperties(textArea);
		add_b = new JPanel();
		add_b.setBackground(Color.white);
		add_b.add(textArea);
		add(add_b, BorderLayout.CENTER);

		ok_button = new JButton("OK");
		ok_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					tray.add(trayIcon);
					setVisible(false);
					start_thread();
				} catch (AWTException ex) {
					ex.printStackTrace();
				}
			}
		});
		ok_button.setEnabled(false);
		add_b = new JPanel();
		add_b.setBackground(Color.white);
		add_b.add(ok_button);
		add(add_b, BorderLayout.SOUTH);

	}

	private void add_popupmenue() {

		popup = new PopupMenu();

		MenuItem defaultItem = new MenuItem("Exit");
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PrintWriter writer = new PrintWriter(file_nema, "UTF-8");
					writer.println(SettingsPodsetnik.TIMEOUT_TAG + "=" + sp.period);
					writer.println(SettingsPodsetnik.PLAY_SOUND_TAG + "=" + (sp.play_sound ? 1 : 0));

					writer.close();

					System.exit(DISPOSE_ON_CLOSE);
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
		});
		popup.add(defaultItem);

		defaultItem = new MenuItem("Open");
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
				setExtendedState(JFrame.NORMAL);
			}
		});
		popup.add(defaultItem);

		defaultItem = new MenuItem("Settings");
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettngsGui settingsgui = new SettngsGui(sp);
				settingsgui.setVisible(true);
			}
		});
		popup.add(defaultItem);
	}

	public void start_thread() {
		if (countdown == null)
			countdown = Countdown.start(sp.period, this);
		else
			countdown.notify_thread();

	}

	private void add_events() {
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == ICONIFIED) {
					try {
						tray.add(trayIcon);
						setVisible(false);
					} catch (AWTException ex) {
						ex.printStackTrace();
					}
				}

				if (e.getNewState() == 7) {
					try {
						tray.add(trayIcon);
						setVisible(false);
					} catch (AWTException ex) {
						ex.printStackTrace();
					}
				}

				if (e.getNewState() == MAXIMIZED_BOTH) {
					tray.remove(trayIcon);
					setVisible(true);
				}
				if (e.getNewState() == NORMAL) {
					tray.remove(trayIcon);
					setVisible(true);
				}

			}
		});

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0) {

				try {
					PrintWriter writer = new PrintWriter(file_nema, "UTF-8");
					writer.println(SettingsPodsetnik.TIMEOUT_TAG + "=" + sp.period);
					writer.println(SettingsPodsetnik.PLAY_SOUND_TAG + "=" + (sp.play_sound ? 1 : 0));

					writer.close();

					System.exit(DISPOSE_ON_CLOSE);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		trayIcon.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON1) {
					long tim = countdown.get_time_remaining();
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					Date d = new Date();
					d.setTime(tim - 60 * 60 * 1000);
					String tip = "Reminder [" + format.format(d) + "]";
					trayIcon.displayMessage("Time left", tip, TrayIcon.MessageType.INFO);
				}

			}
		});
	}

	public void show_message() {
		int index = randomGenerator.nextInt(messages.size());
		try {
			Toolkit.getDefaultToolkit().beep();
			textArea.setText(new String(messages.get(index).getBytes(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setVisible(true);
		setExtendedState(JFrame.NORMAL);
		tray.remove(trayIcon);
		
		start_counter();
	}

	private void start_counter()
	{
		(new Thread(){
			public void run()
			{
				ok_button.setEnabled(false);
				for(int i = 5 ; i>0;i--)
				{
					ok_button.setText(" "+i+" ");
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				ok_button.setText("OK");
				ok_button.setEnabled(true);
			}
		}).start();
		
		
	}
	private JTextArea textAreaProperties(JTextArea textArea) {
		textArea.setEditable(false);
		textArea.setCursor(null);
		textArea.setOpaque(false);
		textArea.setFocusable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		Font font = new Font("Verdana", Font.BOLD, 12);
		textArea.setFont(font);
		textArea.setForeground(Color.BLUE);
		textArea.setBackground(Color.white);
		textArea.setSize(380, 70);
		return textArea;
	}


	public static void main(String[] args) {
		new MainGui();
	}

}