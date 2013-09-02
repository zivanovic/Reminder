package com.zoki.podsetnik.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;

import com.zoki.podsetnik.logika.SettingsPodsetnik;

public class SettngsGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Image image_icon;

	JSpinner timeSpinner;
	JButton jb_S;
	JButton jb_c;
	JCheckBox jb;
	private SettingsPodsetnik sp;

	public SettngsGui(SettingsPodsetnik sp) {
		super("Reminder settings");
		this.sp = sp;
		init_form();
		init_action();
	}

	private void init_form() {
		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			image_icon = Toolkit.getDefaultToolkit().getImage("data/settings.png");
			setIconImage(image_icon);

			setSize(250, 150);
			setResizable(false);
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int) ((dimension.getWidth() - getWidth()) / 2);
			int y = (int) ((dimension.getHeight() - getHeight()) / 2);
			setLocation(x, y);

			timeSpinner = new JSpinner(new SpinnerDateModel());
			JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
			timeSpinner.setEditor(timeEditor);
			long data_time= (sp.period - (1000 * 60 * 60));//>0?sp.period - 1000 * 60 * 60:0;
			Date dat=new Date();
			dat.setTime(data_time);
			timeSpinner.setValue(dat);
			JPanel jp = new JPanel(new BorderLayout());
			jp.setBorder(BorderFactory.createTitledBorder(""));
			JLabel jl = new JLabel(" Select timout: ");
			jl.setHorizontalAlignment(JLabel.RIGHT);
			jp.add(jl, BorderLayout.WEST);
			JPanel jpt = new JPanel();
			jpt.add(timeSpinner);
			jp.add(jpt, BorderLayout.CENTER);
			add(jp, BorderLayout.NORTH);

			jb = new JCheckBox("Play song");
			jp = new JPanel(new GridLayout(1, 2));
			jp.add(jb);
			add(jp, BorderLayout.CENTER);

			jp = new JPanel(new GridLayout(1, 2));
			JPanel tmp = new JPanel();

			jb_S = new JButton("Save");
			tmp.add(jb_S);
			jp.add(tmp);
			jb_c = new JButton("Cancel");
			tmp = new JPanel();
			tmp.add(jb_c);
			jp.add(tmp);
			add(jp, BorderLayout.SOUTH);

			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void close() {
		this.setVisible(false);
	}

	private void save() {
		sp.play_sound = jb.isSelected();
		Date select = (Date) timeSpinner.getValue();
		Date reper = new Date(0L);

		long time = select.getTime();
		long rep_time = reper.getTime();
		sp.period = (time - rep_time) + 1000 * 60 * 60;
		
		close();
	}

	private void init_action() {
		jb_c.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		jb_S.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				save();
			}
		});

	}

}
