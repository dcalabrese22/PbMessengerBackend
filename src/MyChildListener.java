import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.firebase.database.*;

import java.io.IOException;

public class MyChildListener implements ChildEventListener {

    private String messageId;


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        messageId = dataSnapshot.getKey();
        System.out.println("Added: " + messageId);

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        messageId = dataSnapshot.getKey();
        System.out.println("Changed: " + messageId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference msgRef = reference.child("conversations")
                .child("messages")
                .child(messageId);

        ChildEventListener subListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PbMessage message = dataSnapshot.getValue(PbMessage.class);
                try {
                    final WebClient webClient = new WebClient();
                    final HtmlPage loginPage = webClient.getPage("https://www.pinkbike.com/user/login/");
                    final HtmlForm loginForm = loginPage.getFormByName("loginform");
                    final HtmlSubmitInput button = loginForm.getInputByValue("Login");
                    final HtmlTextInput email = loginForm.getInputByName("username-login-loginlen");
                    email.setValueAttribute(Constants.EMAIL);

                    final HtmlPasswordInput password = loginForm.getInputByName("password-password-lt200");
                    password.setValueAttribute(Constants.PASS);

                    final HtmlPage page2 = button.click();
                    String url = "https://www.pinkbike.com/u/dcalabrese22/mail/view/" + messageId;
                    System.out.println(url);
                    final HtmlPage messagePage = webClient.getPage(url);
                    final HtmlForm sendForm = messagePage.getFormByName("mailform");
                    final HtmlTextArea textArea = sendForm.getTextAreaByName("message-gt3-textbb");

                    textArea.setText(message.getBody());
                    System.out.println(message.getBody());
                    final HtmlSubmitInput send = sendForm.getInputByValue("Send");
                    send.click();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(message.getBody());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        msgRef.orderByKey().limitToLast(1).addChildEventListener(subListener);


    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        messageId = dataSnapshot.getKey();
        System.out.println("Removed key " + dataSnapshot.getKey());

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
