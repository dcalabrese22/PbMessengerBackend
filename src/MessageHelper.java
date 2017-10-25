import com.gargoylesoftware.htmlunit.html.*;
import com.google.firebase.database.*;

import java.util.*;

public class MessageHelper {


    public static ArrayList<PbMessage> parseMessages(HtmlPage page, String id, DatabaseReference reference) {

        final List<HtmlBold> dates =
                page.getByXPath("//b[@style='color:#999999']");
        final List<HtmlSpan> users =
                page.getByXPath("//span[@class='bold']");
        final List<HtmlTableDataCell> bodies =
                page.getByXPath("//td[@class='messagebox']");

        ArrayList<String> bodyText = new ArrayList<>();
        for (int i = 0; i < bodies.size(); i++) {
            if (i % 2 == 1) {
                bodyText.add(bodies.get(i).asText());
            }
        }

        ArrayList<PbMessage> messages = new ArrayList<>();
        for (int j = 0; j < dates.size(); j++) {
            final String messageKey = Integer.toString(j);
            String date = dates.get(j).asText();
            String body = bodyText.get(j);
            String sender = users.get(j).asText();
            String type = "recieved";
            if (sender.toLowerCase().contains("dcalabrese22")) {
                type = "sent";
            }
            Long time = new Date().getTime();
            messages.add(new PbMessage(messageKey, body, date, sender, type, time));
            DatabaseReference idReference = reference.child(id);
            idReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(messageKey)) {
                        Map<String, Object> map = new HashMap<>();
                        String type = "recieved";
                        if (sender.toLowerCase().contains("dcalabrese22")) {
                            type = "sent";
                        }
                        map.put(messageKey, new PbMessage(messageKey, body, date, sender, type, time));
                        idReference.updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        return messages;
    }
}
