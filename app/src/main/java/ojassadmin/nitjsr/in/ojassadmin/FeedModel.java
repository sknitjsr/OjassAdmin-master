package ojassadmin.nitjsr.in.ojassadmin;

/**
 * Created by Aditya on 26-12-2016.
 */

public class FeedModel {
    String question,ans;


    public FeedModel(String question,String ans) {

        this.question=question;
        this.ans=ans;


    }
    public FeedModel()
    {

    }

    public String getAns() {
        return ans;
    }

    public String getQuestion() {
        return question;
    }
}
