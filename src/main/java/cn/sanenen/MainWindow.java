package cn.sanenen;

import cn.hutool.core.convert.NumberChineseFormater;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.sanenen.handler.MessageReceiveHandler;
import cn.sanenen.service.AtomicUtil;
import cn.sanenen.service.ConvertService;
import cn.sanenen.service.SignUtil;
import com.zx.sms.connect.manager.EndpointEntity.SupportLongMessage;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.connect.manager.cmpp.CMPPClientEndpointEntity;
import com.zx.sms.handler.api.BusinessHandlerInterface;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {

	private final EndpointManager manager = EndpointManager.INS;
	private JFrame frame;
	private JPanel panel;
	public static boolean isCanSend = true;
	private long sendLastNum;
	private long responseLastNum;
	

	public static JButton button_connect;
	public static JButton button_unconnect;
	public static JButton button_send;

	private JTextField textField_IP;
	private JTextField textField_PORT;
	private JTextField txtDse;
	private JTextField textField_pwd;
	private JTextField textField_serviceid;
	private JTextField textField_spnum;
	private JTextField textField_conCount;
	private JTextField textField_speed;

	private JTextArea textArea_content;
	private JTextArea textArea_mobile;
	private JLabel label_msgShow;// 条数
	private JCheckBox checkBox_randomContent;
	private JCheckBox checkBox_randomMobile;
	private JCheckBox checkBox_manySend;
	private JTextField textField_manySendCount;

	public static JLabel lebel_sendCount;
	public static JLabel label_reponseCount;
	public static JLabel label_reportCount;
	public static JLabel lbls_1;
	public static JLabel lbls;
	public static JLabel label_sucCount;
	public static JLabel label_failCount;
	private JLabel label_daxie;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(()-> {
				try {
					MainWindow frame = new MainWindow();
					frame.init();
				} catch (Exception e) {
					e.printStackTrace();
				}
		});
	}

	public void init() {
		CronUtil.setMatchSecond(true);
		CronUtil.schedule("0/1 * * * * ? ", (Runnable) () -> {
			long nowcnt = AtomicUtil.sendCount.get();
			MainWindow.lbls_1.setText(StrUtil.format("发送速度:{}/s", (nowcnt - sendLastNum) / 1));
			sendLastNum = nowcnt;
			long nowcnt2 = AtomicUtil.reponseCount.get();
			MainWindow.lbls.setText(StrUtil.format("响应速度:{}/s", (nowcnt2 - responseLastNum) / 1));
			responseLastNum = nowcnt2;
		});
		CronUtil.start();
		
		frame = new JFrame("cmpp20客户端");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFont(new Font("Consolas", Font.PLAIN, 12));
		frame.setIconImage(new ImageIcon(new ClassPathResource("e.png").getUrl()).getImage());
		// frame.getContentPane().setLayout(null);
		panel = new JPanel();// 基础信息
		panel.setLayout(null);
		Container contentPane = frame.getContentPane();
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 720, 365);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		JPanel panel2 = new JPanel();
		panel2.setLayout(null);
		tabbedPane.addTab("CMPP20", panel);
//		tabbedPane.addTab("HTTP", panel2);
//		contentPane.add(panel, BorderLayout.CENTER);
		// 连接信息等输入框
		initText();
		// 计数相关
		initCount();
		// 按钮添加
		initButton();
		// 内容 手机号 相关
		initInput();
		frame.setVisible(true);
	}

	private void initCount() {
		lebel_sendCount = new JLabel("发送数量:0");
		lebel_sendCount.setBounds(543, 27, 148, 19);
		panel.add(lebel_sendCount);

		label_reponseCount = new JLabel("响应数量:0");
		label_reponseCount.setBounds(543, 56, 148, 21);
		panel.add(label_reponseCount);

		label_sucCount = new JLabel("提交成功:0");
		label_sucCount.setBounds(543, 80, 148, 21);
		panel.add(label_sucCount);

		label_failCount = new JLabel("提交失败:0");
		label_failCount.setBounds(543, 103, 148, 23);
		panel.add(label_failCount);
		frame.setBounds(500, 200, 751, 438);

		label_reportCount = new JLabel("报告数量:0");
		label_reportCount.setBounds(543, 137, 148, 31);
		panel.add(label_reportCount);

		lbls_1 = new JLabel("发送速度:0/s");
		lbls_1.setBounds(543, 177, 148, 31);
		panel.add(lbls_1);

		lbls = new JLabel("响应速度:0/s");
		lbls.setBounds(543, 205, 148, 31);
		panel.add(lbls);
		
		label_daxie = new JLabel("");
		label_daxie.setBounds(396, 286, 270, 21);
		panel.add(label_daxie);

	}

	private void initInput() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(209, 61, 281, 83);
		panel.add(scrollPane);
		
		checkBox_randomContent = new JCheckBox("内容增加随机变量");
		checkBox_randomContent.setBounds(209, 231, 148, 23);
		panel.add(checkBox_randomContent);
		
		checkBox_randomMobile = new JCheckBox("随机手机号码");
		checkBox_randomMobile.setBounds(209, 259, 148, 23);
		panel.add(checkBox_randomMobile);
		
		label_msgShow = new JLabel("当前字数:0,短信条数:0");
		label_msgShow.setBounds(219, 154, 225, 15);
		panel.add(label_msgShow);

		textArea_content = new JTextArea();
		textArea_content.addCaretListener(e -> {
			String txt = textArea_content.getText();
			int leng = txt.length();
			if (checkBox_randomContent.isSelected()) {
				leng += 8;
				txt += "00000000";
			}
			label_msgShow.setText(StrUtil.format("当前字数:{},短信条数:{}", leng,SignUtil.spliteMsg(txt)));
		});
		scrollPane.setViewportView(textArea_content);
		textArea_content.setText("【易信科技】我是短信内容");
		textArea_content.setTabSize(0);
		textArea_content.setWrapStyleWord(true);
		textArea_content.setLineWrap(true);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(209, 175, 281, 53);
		panel.add(scrollPane_1);

		textArea_mobile = new JTextArea();
		scrollPane_1.setViewportView(textArea_mobile);
		textArea_mobile.setText("13700020004");
		textArea_mobile.setWrapStyleWord(true);
		textArea_mobile.setTabSize(0);
		textArea_mobile.setLineWrap(true);


		checkBox_manySend = new JCheckBox("循环发送");
		checkBox_manySend.addChangeListener(e -> {
			if (checkBox_manySend.isSelected()) {
				textField_manySendCount.setEditable(true);
			}else {
				textField_manySendCount.setEditable(false);
			}
		});
		checkBox_manySend.setBounds(209, 285, 78, 23);
		panel.add(checkBox_manySend);

		textField_manySendCount = new JTextField();
		textField_manySendCount.setText("100");
		textField_manySendCount.addCaretListener(e -> {
			String text = textField_manySendCount.getText();
			if (StrUtil.isNotBlank(text)) {
				label_daxie.setText(NumberChineseFormater.format(Double.parseDouble(text), false));
			}else {
				label_daxie.setText("");
			}
		});
		textField_manySendCount.setEditable(false);
		textField_manySendCount.setBounds(291, 286, 95, 21);
		panel.add(textField_manySendCount);
		textField_manySendCount.setColumns(10);
		
		JButton button_clear = new JButton("清零");
		button_clear.addActionListener(e -> {
			sendLastNum = 0;
			responseLastNum = 0;
			lebel_sendCount.setText("发送数量:0"); 
			label_reponseCount.setText("响应数量:0");
			label_sucCount.setText("提交成功:0");
			label_failCount.setText("提交失败:0");
			label_reportCount.setText("报告数量:0");
			AtomicUtil.clear();
		});
		button_clear.setForeground(Color.BLACK);
		button_clear.setBackground(SystemColor.controlHighlight);
		button_clear.setBounds(543, 263, 72, 31);
		panel.add(button_clear);
		
//		panel.add(tabbedPane);

	}

	public void initText() {
		JLabel lblNewLabel = new JLabel("IP");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(20, 27, 54, 15);
		panel.add(lblNewLabel);

		textField_IP = new JTextField();
		textField_IP.setText("192.168.2.58");
		textField_IP.setBounds(84, 21, 109, 21);
		textField_IP.setColumns(10);
		panel.add(textField_IP);

		JLabel label = new JLabel("端口");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(20, 69, 54, 15);
		panel.add(label);

		textField_PORT = new JTextField();
		textField_PORT.setText("9004");
		textField_PORT.setColumns(10);
		textField_PORT.setBounds(84, 63, 109, 21);
		panel.add(textField_PORT);

		JLabel label_1 = new JLabel("用户名");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setBounds(20, 111, 54, 15);
		panel.add(label_1);

		txtDse = new JTextField();
		txtDse.setText("dse123");
		txtDse.setColumns(10);
		txtDse.setBounds(84, 105, 109, 21);
		panel.add(txtDse);

		JLabel label_2 = new JLabel("密码");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setBounds(20, 153, 54, 15);
		panel.add(label_2);

		textField_pwd = new JTextField();
		textField_pwd.setText("123456");
		textField_pwd.setColumns(10);
		textField_pwd.setBounds(84, 147, 109, 21);
		panel.add(textField_pwd);

		JLabel label_4 = new JLabel("业务代码");
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		label_4.setBounds(20, 195, 54, 15);
		panel.add(label_4);

		textField_serviceid = new JTextField();
		textField_serviceid.setColumns(10);
		textField_serviceid.setBounds(84, 189, 109, 21);
		panel.add(textField_serviceid);

		JLabel label_5 = new JLabel("长号码");
		label_5.setHorizontalAlignment(SwingConstants.RIGHT);
		label_5.setBounds(20, 237, 54, 15);
		panel.add(label_5);

		textField_spnum = new JTextField();
		textField_spnum.setColumns(10);
		textField_spnum.setBounds(84, 231, 109, 21);
		panel.add(textField_spnum);

		JLabel label_6 = new JLabel("连接数");
		label_6.setHorizontalAlignment(SwingConstants.RIGHT);
		label_6.setBounds(20, 279, 54, 15);
		panel.add(label_6);

		textField_conCount = new JTextField();
		textField_conCount.setText("1");
		textField_conCount.setColumns(10);
		textField_conCount.setBounds(84, 273, 109, 21);
		panel.add(textField_conCount);

		JLabel label_7 = new JLabel("速度");
		label_7.setHorizontalAlignment(SwingConstants.RIGHT);
		label_7.setBounds(20, 321, 54, 15);
		panel.add(label_7);

		textField_speed = new JTextField();
		textField_speed.setText("100");
		textField_speed.setColumns(10);
		textField_speed.setBounds(84, 318, 109, 21);
		panel.add(textField_speed);
	}

	public void initButton() {
		button_connect = new JButton("连接");
		button_connect.addActionListener(e -> {
			connect();
			// 禁用 用户名等输入框
			downInput();
		});
		button_connect.setForeground(SystemColor.windowText);
		button_connect.setBounds(209, 15, 72, 31);
		button_connect.setBackground(SystemColor.controlHighlight);
		panel.add(button_connect);

		button_unconnect = new JButton("断开");
		button_unconnect.addActionListener(e -> {
			// 连接断开
			manager.close();
			upInput();
			isCanSend = false;
			sendLastNum = 0;
			responseLastNum = 0;
			AtomicUtil.clear();
			MainWindow.button_connect.setEnabled(true);
			MainWindow.button_send.setEnabled(false);
		});
		button_unconnect.setForeground(SystemColor.windowText);
		button_unconnect.setBackground(SystemColor.controlHighlight);
		button_unconnect.setBounds(351, 15, 72, 31);
		panel.add(button_unconnect);

		button_send = new JButton("发送");
		button_send.addActionListener(e -> {
			try {
				send();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		button_send.setEnabled(false);
		button_send.setForeground(Color.BLACK);
		button_send.setBackground(SystemColor.controlHighlight);
		button_send.setBounds(285, 313, 72, 31);
		panel.add(button_send);

	}

	private void downInput() {
		textField_IP.setEditable(false);
		textField_PORT.setEditable(false);
		txtDse.setEditable(false);
		textField_pwd.setEditable(false);
		textField_serviceid.setEditable(false);
		textField_spnum.setEditable(false);
		textField_conCount.setEditable(false);
		textField_speed.setEditable(false);
	}

	private void upInput() {
		textField_IP.setEditable(true);
		textField_PORT.setEditable(true);
		txtDse.setEditable(true);
		textField_pwd.setEditable(true);
		textField_serviceid.setEditable(true);
		textField_spnum.setEditable(true);
		textField_conCount.setEditable(true);
		textField_speed.setEditable(true);
	}

	private void connect() {
		isCanSend = true;
		CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
		client.setId(txtDse.getText());
		client.setHost(textField_IP.getText());
		client.setPort(Integer.parseInt(textField_PORT.getText()));
		client.setUserName(txtDse.getText());
		client.setPassword(textField_pwd.getText());
		client.setServiceId(textField_serviceid.getText());
		client.setMaxChannels(Short.parseShort(textField_conCount.getText()));
		client.setVersion((short) 0x20);
		client.setWriteLimit(Integer.parseInt(textField_speed.getText()));

		client.setGroupName("test");
		client.setChartset(Charset.forName("utf-8"));
		client.setRetryWaitTimeSec((short) 30);
		client.setUseSSL(false);
		client.setMaxRetryCnt((short)0);
		client.setReSendFailMsg(false);
		client.setSupportLongmsg(SupportLongMessage.BOTH);
		List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
		clienthandlers.add(new MessageReceiveHandler());
		// clienthandlers.add( new SessionConnectedHandler());
		client.setBusinessHandlerSet(clienthandlers);

		manager.addEndpointEntity(client);
		ThreadUtil.sleep(1000);
		for (int i = 0; i < client.getMaxChannels(); i++)
			manager.openEndpoint(client);
	}

	private void send() {
		String contStr = textArea_content.getText().trim();// 短信内容
		String mobile = textArea_mobile.getText();
		String id = txtDse.getText();
		boolean randomContent = checkBox_randomContent.isSelected();
		boolean randomMobile = checkBox_randomMobile.isSelected();
		boolean manySend = checkBox_manySend.isSelected();
		if (manySend) {
			new Thread(() -> {
				try {
					button_send.setEnabled(false);
					int manyCount = Integer.parseInt(textField_manySendCount.getText());
					String tempContent = contStr;
					for (int i = 0; i < manyCount; i++) {
						if (isCanSend == false) {
							break;
						}
						if (randomContent) {// 加验证码
							tempContent = contStr + RandomUtil.randomString(8);
						}
						if (randomMobile) {// 随机手机号
							ConvertService.sendSms(ConvertService.getMobile(), tempContent, id);
							AtomicUtil.sendCount.incrementAndGet();
						} else {
							String[] destMobile = mobile.split(",|，");
							for (String tmpmobile : destMobile) {
								if (StrUtil.isNotBlank(tmpmobile)) {
									ConvertService.sendSms(tmpmobile, contStr, id);
									AtomicUtil.sendCount.incrementAndGet();
								}
							}
						}
						MainWindow.lebel_sendCount.setText(StrUtil.format("发送数量:{}",AtomicUtil.sendCount.get()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				button_send.setEnabled(true);
			}).start();
		} else {
			String[] destMobile = mobile.split(",|，");
			for (String tmpmobile : destMobile) {
				if (StrUtil.isNotBlank(tmpmobile)) {
					ConvertService.sendSms(tmpmobile, contStr, id);
					AtomicUtil.sendCount.incrementAndGet();
				}
				MainWindow.lebel_sendCount.setText(StrUtil.format("发送数量:{}",AtomicUtil.sendCount.get()));
			}
		}
	}
}
