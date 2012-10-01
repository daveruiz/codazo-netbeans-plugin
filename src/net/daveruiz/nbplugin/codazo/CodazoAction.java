package net.daveruiz.nbplugin.codazo;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Edit",
	id = "net.daveruiz.nbplugin.codazo.CodazoAction")
	@ActionRegistration(
		iconBase = "net/daveruiz/nbplugin/codazo/codazo16.png",
		displayName = "#CTL_CodazoAction")
	@ActionReferences({
		@ActionReference(path = "Menu/Edit", position = 1470, separatorBefore = 1455),
		@ActionReference(path = "Editors/Popup", position = 0000),
		@ActionReference(path = "Shortcuts", name = "DO-C")
	})
	@Messages("CTL_CodazoAction=Codazo!")
public final class CodazoAction implements ActionListener {

	private final EditorCookie context;

	public CodazoAction(EditorCookie context) {
		this.context = context;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		JEditorPane[] editors = context.getOpenedPanes();

		if (editors != null) {

			JEditorPane editor = editors[0];

			int selectionStart = editor.getSelectionStart();
			int selectionLength = editor.getSelectionEnd() - selectionStart;

			String code = null;
			int line = 1;

			// Obtain selection
			try {
				code = editor.getText( selectionStart, selectionLength );
			} catch (Exception ex) {
				error( ex.toString() );
				return;
			}

			if ( code != null && !"".equals(code) ) {

				// Set options
				CodazoOptions options = new CodazoOptions();
				options.startWithLine = line; // TODO

				// Do request
				String url;
				try {
					url = CodazoService.getShortUrl(code, options);
				} catch (Exception err) {
					error( err.toString() );
					return;
				}

				if ( url != null && !"".equals(url) ) {
					// Copy response to clipboard
					StringSelection sl = new StringSelection( url );
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(sl, null);

					info( "Successfully copied into your clipboard!");
				} else {
					error( "Mmmm, something fail... Try later or check options." );
				}

			} else {
				info( "Select code to share first!" );
			}
		} else {
			// No editor opened ?? do nothing
		}
	}

	private void info( String text ) {
		JOptionPane.showMessageDialog(null, text, "Codazo!", JOptionPane.INFORMATION_MESSAGE );
	}

	private void error( String text ) {
		JOptionPane.showMessageDialog(null, text, "Error!", JOptionPane.ERROR_MESSAGE );
	}

}
