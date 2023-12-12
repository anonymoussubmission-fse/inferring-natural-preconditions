import javax.swing.JPanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class Corina2 {
	//protected static final Font copyrightFont = new Font("sansserif", 0, 10);

	private static String getText(String message) {
		return message;
	}

	protected static void addCopyright(JPanel box) {
		String description = getText("copyright");
		JPanel copyrightBlock = new JPanel();
		copyrightBlock.setLayout(new BoxLayout(copyrightBlock, 1));
		box.add(copyrightBlock);
		copyrightBlock.setAlignmentX(0.5F);
		BufferedReader r = new BufferedReader(new StringReader(description));
		while (true) {
			String line;
			try {
				line = r.readLine();
			} catch (IOException ioe) {
				break;
			}
			if (line == null)
				break;
			String subst = MessageFormat.format(line, new Object[] { "2001-2005", "Ken Harris, Aaron Hamid, Lucas Madar" });
			JLabel copyrightLabel = new JLabel(subst);
			copyrightLabel.setAlignmentX(0.5F);
			copyrightLabel.setFont(null);
			copyrightBlock.add(copyrightLabel);
		}
	}

}
