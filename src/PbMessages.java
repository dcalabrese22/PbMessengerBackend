import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class PbMessages {

    private String userName = Constants.EMAIL;
    private String pass = Constants.PASS;

    public PbMessages() {
    }


    public PbMessages(String password) {
        pass = password;
    }

    public PbMessages(String name, String password) {
        userName = name;
        pass = password;
    }

    public void setup() throws IOException, SQLException, InterruptedException {
        final WebClient webClient = new WebClient();
        final HtmlPage loginPage = webClient.getPage("https://www.pinkbike.com/user/login/");
        final HtmlForm loginForm = loginPage.getFormByName("loginform");
        final HtmlSubmitInput button = loginForm.getInputByValue("Login");
        final HtmlTextInput email = loginForm.getInputByName("username-login-loginlen");
        email.setValueAttribute(userName);

        final HtmlPasswordInput password = loginForm.getInputByName("password-password-lt200");
        password.setValueAttribute(pass);

        final HtmlPage page2 = button.click();

        final HtmlPage inboxPage = webClient.getPage("https://www.pinkbike.com/u/dcalabrese22/mail/");

        final List<HtmlAnchor> messageLinks = inboxPage.getByXPath("//tr[@class='normalmessage']/td/a");

        FileInputStream serviceAccount = new FileInputStream("service-account.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://pbmessages-62c8d.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("conversations");
        DatabaseReference userRef = reference.child("g5561yqarrWCVLMN92zrYZzoVyk1");
        userRef.removeValue();
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        DatabaseReference pushKeyRef = FirebaseDatabase.getInstance().getReference("pushKeys");

        for (int i = messageLinks.size()-1; i >= 1; i -= 2) {
            String[] anchors = messageLinks.get(i).getHrefAttribute().split("/");
            int lastSpace = messageLinks.get(i).asText().lastIndexOf(" ");
            String conversationId = anchors[anchors.length - 1];
            String user = messageLinks.get(i-1).asText().split("/")[0];
            System.out.println(user);

            String title;
            if (lastSpace == -1) {
                title = messageLinks.get(i).asText();

            } else {
                title = messageLinks.get(i).asText().substring(0, lastSpace);
            }
            getAvatarImage(webClient, user);
            final HtmlPage conversationPage = webClient.getPage(messageLinks.get(i).getHrefAttribute());

            ArrayList<PbMessage> messages = MessageHelper.parseMessages(conversationPage, conversationId, messagesRef);
            Long time = new Date().getTime();

            String key = userRef.push().getKey();
            PbConversation conversation = new PbConversation(conversationId, title, user,
                    messages.get(messages.size()-1).getBody(), getAvatarImage(webClient, user),
                    messages.get(messages.size()-1).getType(), time, key);

//            pushKeyRef.child(conversationId).setValue(key);

            Map<String, Object> m = new HashMap<>();
//            Map<String, Object> messagesMap = new HashMap<>();
            m.put(conversationId, conversation);
//            messagesMap.put(conversationId, messages);
            userRef.updateChildren(m);
//            messagesRef.updateChildren(messagesMap);
            userRef.child(key).setValue(conversation);

        }


        Thread.sleep(5000);
    }

    public String getAvatarImage(WebClient client, String userName) throws IOException{
        HtmlPage userPage = client.getPage("https://www.pinkbike.com/u/" + userName);
        List<HtmlImage> images = userPage.getByXPath("//img[@class='user-image']");
        return images.get(0).getSrcAttribute();
    }

//    public ArrayList<PbMessage> parseMessages(HtmlPage page, String id, DatabaseReference reference) {
//        final List<HtmlBold> dates =
//                page.getByXPath("//b[@style='color:#999999']");
//        final List<HtmlSpan> users =
//                page.getByXPath("//span[@class='bold']");
//        final List<HtmlTableDataCell> bodies =
//                page.getByXPath("//td[@class='messagebox']");
//
//        ArrayList<String> bodyText = new ArrayList<>();
//        for (int i = 0; i < bodies.size(); i++) {
//            if (i % 2 == 1) {
//                bodyText.add(bodies.get(i).asText());
//            }
//        }
//
//        ArrayList<PbMessage> messages = new ArrayList<>();
//        for (int j = 0; j < dates.size(); j++) {
//            final String messageKey = Integer.toString(j);
//            String date = dates.get(j).asText();
//            String body = bodyText.get(j);
//            String sender = users.get(j).asText();
//            String type = "recieved";
//            if (sender.toLowerCase().contains("dcalabrese22")) {
//                type = "sent";
//            }
//            Long time = new Date().getTime();
//            messages.add(new PbMessage(messageKey, body, date, sender, type, time));
//            DatabaseReference idReference = reference.child(id);
//            idReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (!dataSnapshot.hasChild(messageKey)) {
//                        Map<String, Object> map = new HashMap<>();
//                        String type = "recieved";
//                        if (sender.toLowerCase().contains("dcalabrese22")) {
//                            type = "sent";
//                        }
//                        map.put(messageKey, new PbMessage(messageKey, body, date, sender, type, time));
//                        idReference.updateChildren(map);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//
//        return messages;
//    }


    public static void main(String[] args) {
        PbMessages a = new PbMessages();
        try {
            a.setup();
        }catch (Exception e ) {
            e.printStackTrace();
        }

        System.exit(0);
    }

}