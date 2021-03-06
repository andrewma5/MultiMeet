import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AdminWindow implements Runnable {
	
	private static Font Courier16 = new Font("Courier", Font.PLAIN, 16);
	
	private static String helpMessage = "GET [username] - Get the information for user\n"
			+ "REMOVE [username] FROM [class] - Remove user from class\n"
			+ "ADD [username] TO [class] - Add user to class\n"
			+ "LIST - List all users\n"
			+ "LIST FROM [class] - List all users in certain class\n"
			+ "LIST CLASSES - List all classes\n"
			+ "ADDCLASS [classname] - Add a new class\n"
			+ "REMOVECLASS [classname] - Remove a class\n"
			+ "CODE - Get the group code\n"
			+ "ASSIGN [username] TEACHER/STUDENT/ADMIN - Set a user to a different role\n"
			+ "SCHEDULE - Get the class times with default parameters\n"
			+ "SCHEDULE [class length] [min teachers] [min students] [max teacher classes]\n"
			+ "  -  class length - the length of a class in minutes\n"
			+ "  -  min teachers - the minimum number of teachers per class\n"
			+ "  -  min students - the minimum number of students per class\n"
			+ "  -  max teacher classes - the maximum number a classes a teacher will teach";
	
	private int prevMax = 0;

	@Override
	public void run() {
		int w = 800;
		int h = 600;
		
		JFrame frame = new JFrame();
		frame.setLayout(null);		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		JPanel panel = new JPanel();
		panel.setBorder(null);
		panel.setLayout(new GridBagLayout());
		frame.add(panel);
		panel.setBounds(0, 0, w, h);
		
		
		JLabel consoleOutput = new JLabel("<html>Admin Console></html>");
		consoleOutput.setBackground(Color.BLACK);
		consoleOutput.setOpaque(true);
		consoleOutput.setForeground(Color.WHITE);
		consoleOutput.setVerticalAlignment(JLabel.TOP);
		consoleOutput.setFont(Courier16);
		consoleOutput.setBorder(BorderFactory.createEmptyBorder(7,2,7,2));
		JScrollPane scroll = new JScrollPane(consoleOutput);
		scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
		    public void adjustmentValueChanged(AdjustmentEvent e) {
		    	if(e.getAdjustable().getMaximum() != prevMax) {
		    		prevMax = e.getAdjustable().getMaximum();
		    		e.getAdjustable().setValue(e.getAdjustable().getMaximum());
		    	}
		    }
		});
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.ipadx = 0;
		gbc.ipady = 590;
		gbc.ipadx = 800;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(scroll, gbc);


		JTextField consoleInput = new HintTextField("Enter Command...");
		consoleInput.setFont(Courier16);
		consoleInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = consoleInput.getText().substring(0, Math.min(consoleInput.getText().length(), 64));
				String commandResult = "";
				if (StartProgram.socket != null) {
					StartProgram.socket.sendMessage("CONSOLE " + command + " " + StartProgram.username + " " + StartProgram.groupcode);
					commandResult = StartProgram.socket.receiveMessage();
				}
				else {
					commandResult = "Disconnected from Server";
				}
				String outputText = consoleOutput.getText().substring(0, consoleOutput.getText().length() - 7) + 
						" " + command + "<br>" + commandResult + "<br><br>Admin Console></html>";
				consoleOutput.setText(outputText);
				consoleInput.setText("");
			}
		});
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.ipadx = 765;
		gbc.ipady = 50;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(consoleInput, gbc);
		
		JButton help = new JButton("?");
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.ipadx = 35;
		gbc.ipady = 50;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, helpMessage);
			}
		});
		panel.add(help, gbc);

		frame.setLocation(dim.width/2 - w/2, dim.height/2 - h/2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.addMouseListener(tsc);
//		frame.addMouseMotionListener(tsc);
		frame.setTitle("Lesson Planner Admin");
		frame.pack();
		frame.setSize(w+16, h+39);
		frame.setVisible(true);
		frame.setResizable(true);

	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new AdminWindow());
	}
}

class HintTextField extends JTextField implements FocusListener {

	  private final String hint;
	  private boolean showingHint;

	  public HintTextField(final String hint) {
	    super(hint);
	    this.hint = hint;
	    this.showingHint = true;
	    super.addFocusListener(this);
	  }

	  @Override
	  public void focusGained(FocusEvent e) {
	    if(this.getText().isEmpty()) {
	      super.setText("");
	      showingHint = false;
	    }
	  }
	  @Override
	  public void focusLost(FocusEvent e) {
	    if(this.getText().isEmpty()) {
	      super.setText(hint);
	      showingHint = true;
	    }
	  }

	  @Override
	  public String getText() {
	    return showingHint ? "" : super.getText();
	  }
	}
