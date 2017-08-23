

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LabResponsiveGUI extends JFrame implements ActionListener {

	private boolean _pressed = false;

	private final String TEXT_COUNT_DOWN = "Count down!";
	private final String TEXT_CANCEL = "Cancel!";
	
	private JTextField txt = new JTextField("3");
	private JButton button = new JButton(TEXT_COUNT_DOWN);
	
	private JTextArea txtOutput = new JTextArea(10, 20);
	
	public LabResponsiveGUI() {
		super("Simple GUI example");
		setSize(400, 400);
		button.addActionListener(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(txt, BorderLayout.NORTH);
		button.setPreferredSize(new Dimension(200, 50));
		add(button, BorderLayout.SOUTH);
		JScrollPane scroll = new JScrollPane(txtOutput);
		add(scroll, BorderLayout.CENTER);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		int value ;
		try {
			value = Integer.parseInt(txt.getText());
			if (value <= 0) {
				JOptionPane.showMessageDialog(this, "Please enter a number greater than zero");
				return;
			}
			
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Please enter a valid integer");
			return;
		}

		if (_pressed) {

		}

		_pressed = true;
		
		BackgroundTask backgroundTask = new BackgroundTask(value, txtOutput);
		backgroundTask.execute();

		_pressed = false;
		
	}
	
	public static void main(String[] agrs){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LabResponsiveGUI frame = new LabResponsiveGUI();
				frame.setVisible(true);
			}
		});
	}
}