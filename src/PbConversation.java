import java.util.ArrayList;

public class PbConversation {

    private String mId;
    private String mTitle;
    private String mUser;
    private String mUserImage;
    private String mLastMessage;
    private String mLastMessageType;
    private Long timeStamp;
    private String pushKey;

    public PbConversation() {}

    public PbConversation(String id, String title, String user, String lastMessage,
                          String userImage, String type, Long timeStamp, String pushKey) {
        mTitle = title;
        mId = id;
        mUser = user;
        mLastMessage = lastMessage;
        mUserImage = userImage;
        mLastMessageType = type;
        this.timeStamp = timeStamp;
        this.pushKey = pushKey;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public String getLastMessageType() {
        return mLastMessageType;
    }

    public String getUserImage() {
        return mUserImage;
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUser() {
        return mUser;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public void setLastMessage(String message) {
        mLastMessage = message;
    }

    public void setUserImage(String url) {
        mUserImage = url;
    }

    public void setLastMessageType(String type) {
        mLastMessageType = type;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return mTitle;
    }

}