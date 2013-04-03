import javax.swing.*;

import java.awt.*;
import java.awt.*;
import java.awt.event.*;


public class ButtonDemo extends JFrame implements ActionListener, FocusListener
{

	public static final int WIDTH = 400;
	public static final int HEIGHT = 300;
	private JButton m_cloudyButton, m_sunnyButton;
	JTextField m_textField;
	JTextField m_textField2;
	
	public ButtonDemo()
	{
		setSize(WIDTH,HEIGHT);
		setTitle("Worlds Simplest GUI App");
		
		Container contentPane = getContentPane();
		contentPane.setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane.setLayout(new FlowLayout());
		
		JLabel buttonLabel = new JLabel("These are buttons");
		contentPane.add(buttonLabel);
		
		m_cloudyButton = new JButton("Cloudy");
		m_cloudyButton.addActionListener(this);
		contentPane.add(m_cloudyButton);
		
		m_sunnyButton = new JButton("Sunny");
		m_sunnyButton.addActionListener(this);
		contentPane.add(m_sunnyButton);
		
		JLabel textLabel = new JLabel("Enter some text please");
		contentPane.add(textLabel);
		
		m_textField = new JTextField(10);
		contentPane.add(m_textField);
		
		JSlider mySlider = new JSlider();
		contentPane.add(mySlider);
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String actionCommand = e.getActionCommand();
		
		Container contentPane = getContentPane();
		
		if (actionCommand.equals("Cloudy"))
		{
			contentPane.setBackground(Color.gray);
			m_cloudyButton.setText("no Sunny");
		}
		else if (actionCommand.equals("Sunny"))
			contentPane.setBackground(Color.blue);
		else 
			System.out.println("Don't know that command!");
	}
	
	public static void main(String[] args)
	{
		ButtonDemo myDemo = new ButtonDemo();
		myDemo.setVisible(true);
	}

	@Override
	public void focusGained(FocusEvent e) 
	{
		// TODO Auto-generated method stub
		m_textField.setBackground(Color.CYAN);
		
	}

	@Override
	public void focusLost(FocusEvent e) 
	{
		// TODO Auto-generated method stub
		m_textField.setBackground(Color.pink);
		
	}

}
