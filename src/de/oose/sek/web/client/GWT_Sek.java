package de.oose.sek.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.oose.sek.web.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT_Sek implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	private Button sendButton;
	private TextBox nameField;
	private RootPanel rootPanel;
	private Label title;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Use RootPanel.get() to get the entire body element
		rootPanel = RootPanel.get();
		rootPanel.setSize("100%", "100%");

		// Add the horizontal panel, which will contain all of the control
		// elements
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(20);
		horizontalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setSize("100%", "100%");
		rootPanel.add(horizontalPanel, 0, 0);

		// Add the oose logo to the left cell of the horizontal panel
		Image oose_logo = new Image("ressources/images/logo-xs.png");
		horizontalPanel.add(oose_logo);
		horizontalPanel.setCellHorizontalAlignment(oose_logo,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Add the central controls in their own vertical panel
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setStyleName("panel");
		verticalPanel.setSpacing(5);
		horizontalPanel.add(verticalPanel);
		verticalPanel.setHeight("335px");

		// Add the title label
		title = new Label("SEK-Web Sampleproject");
		title.setSize("300px", "30px");
		verticalPanel.add(title);
		verticalPanel.setCellHorizontalAlignment(title,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Add the name textbox
		nameField = new TextBox();
		nameField.setAlignment(TextAlignment.CENTER);
		nameField.setText("GWT User");
		verticalPanel.add(nameField);
		verticalPanel.setCellHorizontalAlignment(nameField,
				HasHorizontalAlignment.ALIGN_CENTER);
		// Focus the cursor on the name field when the app loads and select its
		// content
		nameField.setFocus(true);

		// Add the send button
		sendButton = new Button("Send");
		// We can add style names to widgets
		sendButton.addStyleName("sendButton");
		sendButton.setSize("76px", "33px");
		verticalPanel.add(sendButton);
		verticalPanel.setCellHorizontalAlignment(sendButton,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Add the tup logo to the right cell of the horizontal panel
		Image tup_logo = new Image("ressources/images/icons_oose_java.png");
		horizontalPanel.setCellHorizontalAlignment(tup_logo,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.add(tup_logo);

		final Label errorLabel = new Label();
		verticalPanel.add(errorLabel);

		// Exercise_1: Add a final RichTextArea
		final RichTextArea responseTextArea = new RichTextArea();
		responseTextArea.setSize("318px", "196px");

		verticalPanel.add(responseTextArea);
		verticalPanel.setCellHorizontalAlignment(responseTextArea,
				HasHorizontalAlignment.ALIGN_CENTER);

		// We can set the id of a widget by accessing its Element
		final Button closeButton = new Button("Close");
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);

		// Create and style our response panel for the dialog box
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								// Exercise_1: Set result as text
								responseTextArea.setHTML(result);

								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}
}
