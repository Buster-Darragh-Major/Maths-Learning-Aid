import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(String title) {
        super(title);
        // Set layout manager
        setLayout(new BorderLayout());

        // Create Swing components
        JLabel header1 = new JLabel("Welcome to Maths Authoring Aid!");
        JLabel header2 = new JLabel("A list of your current creations are as follows:");

        JList listPane = new JList();
        JButton createButton = new JButton("Create");

        // Add swing components to content page
        Container c = getContentPane();

        c.add(header1, BorderLayout.NORTH);
        c.add(header2, BorderLayout.PAGE_START);
        c.add(listPane, BorderLayout.CENTER);
        c.add(createButton, BorderLayout.SOUTH);
    }
}
