import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.*;
import sun.rmi.runtime.Log;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DbListener {

    private static String userName = Constants.EMAIL;
    private static String pass = Constants.PASS;

    public static void main(String[] args) {

        try {

            Long time = new Date().getTime();
            FileInputStream serviceAccount = new FileInputStream("service-account.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl("https://pbmessages-62c8d.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference messageRef = database.getReference()
                    .child("messages");

            Query conversationRef = database.getReference()
                    .child("conversations")
                    .child("g5561yqarrWCVLMN92zrYZzoVyk1")
                    .orderByChild("timeStamp")
                    .limitToLast(1)
                    .startAt(time);

            //send new conversation to pinkbike when user creates a new message
            conversationRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    PbConversation conversation = dataSnapshot.getValue(PbConversation.class);
                    System.out.println(conversation.getTitle());
                    try {
                        final WebClient webClient = new WebClient();
                        final HtmlPage loginPage = webClient.getPage("https://www.pinkbike.com/user/login/");
                        final HtmlForm loginForm = loginPage.getFormByName("loginform");
                        final HtmlSubmitInput button = loginForm.getInputByValue("Login");
                        final HtmlTextInput email = loginForm.getInputByName("username-login-loginlen");
                        email.setValueAttribute(userName);

                        final HtmlPasswordInput password = loginForm.getInputByName("password-password-lt200");
                        password.setValueAttribute(pass);

                        final HtmlPage page2 = button.click();
                        System.out.println("logged in");
                        final HtmlPage newMessagePage = webClient
                                .getPage("https://www.pinkbike.com/u/dcalabrese22/mail/sendmail-to/");
                        final HtmlForm newMessageForm = newMessagePage.getFormByName("mailform");
                        final HtmlTextInput textAreaTo = newMessageForm
                                .getInputByName("username-username-usernamelen");
                        final HtmlTextInput textAreaSubject = newMessageForm
                                .getInputByName("subject-gt1-textbb");
                        final HtmlTextArea textAreaBody = newMessageForm
                                .getTextAreaByName("message-gt3-textbb");

                        textAreaTo.setText(conversation.getUser());
                        textAreaSubject.setText(conversation.getTitle());
                        textAreaBody.setText(conversation.getLastMessage());
                        final HtmlSubmitInput send = newMessageForm.getInputByValue("Send");
                        send.click();
                        System.out.println("send new message");

                        final HtmlPage inboxPage = webClient
                                .getPage("https://www.pinkbike.com/u/dcalabrese22/mail/");
                        final List<HtmlAnchor> messageLinks = inboxPage
                                .getByXPath("//tr[@class='normalmessage']/td/a");
                        String[] anchors = messageLinks.get(1).getHrefAttribute().split("/");
                        String conversationId = anchors[anchors.length - 1];
                        System.out.println(conversationId);
                        final HtmlPage conversationPage = webClient.getPage(messageLinks.get(1).getHrefAttribute());
                        MessageHelper.parseMessages(conversationPage, conversationId, messageRef);

                        DatabaseReference ref = dataSnapshot.getRef();
                        ref.child("id").setValue(conversationId);
                        System.out.println("PushKey: " + ref.getKey());
                        System.out.println("Conversation id: " + conversationId);
                        FirebaseDatabase.getInstance().getReference()
                                .child("pushKeys")
                                .child(conversationId)
                                .setValue(ref.getKey());
                        webClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                //delete conversation on pinkbike when user deletes in app
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    PbConversation conversation = dataSnapshot.getValue(PbConversation.class);
                    String key = dataSnapshot.getKey();
                    System.out.println(key.startsWith("-"));
//                    try {
//                        final WebClient webClient = new WebClient();
//                        final HtmlPage loginPage = webClient.getPage("https://www.pinkbike.com/user/login/");
//                        final HtmlForm loginForm = loginPage.getFormByName("loginform");
//                        final HtmlSubmitInput button = loginForm.getInputByValue("Login");
//                        final HtmlTextInput email = loginForm.getInputByName("username-login-loginlen");
//                        email.setValueAttribute(userName);
//
//                        final HtmlPasswordInput password = loginForm.getInputByName("password-password-lt200");
//                        password.setValueAttribute(pass);
//
//                        button.click();
//                        System.out.println("logged in");
//                        final HtmlPage conversationPageToDelete = webClient.getPage(
//                                "https://www.pinkbike.com/u/dcalabrese22/mail/x_deletesingle/" + id);
//                        webClient.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //send message to pinkbike when user replies to an existing message
            messageRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    System.out.println(key);

                    System.out.println(time);
                    Query lastMessageRef = messageRef.child(key).orderByChild("timeStamp").limitToLast(1).startAt(time);
                    lastMessageRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            PbMessage message = dataSnapshot.getValue(PbMessage.class);
                            if (message.getType().equals("sent") && !message.getMessageId().equals("0")) {
                                try {
                                    final WebClient webClient = new WebClient();
                                    final HtmlPage loginPage = webClient.getPage("https://www.pinkbike.com/user/login/");
                                    final HtmlForm loginForm = loginPage.getFormByName("loginform");
                                    final HtmlSubmitInput button = loginForm.getInputByValue("Login");
                                    final HtmlTextInput email = loginForm.getInputByName("username-login-loginlen");
                                    email.setValueAttribute(userName);

                                    final HtmlPasswordInput password = loginForm.getInputByName("password-password-lt200");
                                    password.setValueAttribute(pass);

                                    final HtmlPage page2 = button.click();

                                    final HtmlPage messagePage = webClient.getPage("https://www.pinkbike.com/u/dcalabrese22/mail/view/" + key);
                                    final HtmlForm sendForm = messagePage.getFormByName("mailform");
                                    final HtmlTextArea textArea = sendForm.getTextAreaByName("message-gt3-textbb");

                                    textArea.setText(message.getBody());
                                    final HtmlSubmitInput send = sendForm.getInputByValue("Send");
                                    send.click();
                                    System.out.println("send");
                                    webClient.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

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
                    });
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
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
}


