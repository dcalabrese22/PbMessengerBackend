import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationUpdater {

    private static String userName = Constants.EMAIL;
    private static String pass = Constants.PASS;
    public static void updatePbConversation(WebClient webClient) {
        try {

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
            String[] anchors = messageLinks.get(1).getHrefAttribute().split("/");
            String conversationId = anchors[anchors.length - 1];


            Query query = FirebaseDatabase.getInstance().getReference()
                    .child("conversations")
                    .child("g5561yqarrWCVLMN92zrYZzoVyk1")
                    .limitToLast(1);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        PbConversation oldConversation = child.getValue(PbConversation.class);
                        System.out.println("title " + oldConversation.getTitle());
                        DatabaseReference oldMsgRef = FirebaseDatabase.getInstance().getReference()
                                .child("conversations")
                                .child("g5561yqarrWCVLMN92zrYZzoVyk1")
                                .child(oldConversation.getId());
                        oldMsgRef.child("id").setValue(conversationId);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
