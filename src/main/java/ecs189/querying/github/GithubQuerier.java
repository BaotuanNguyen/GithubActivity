package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            JSONArray commits = event.getJSONObject("payload").getJSONArray("commits");

            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");

            //html table
            if(commits.length()> 0) {
                sb.append("<table class = \"table table-striped \">");
                sb.append("<tr> <th width = \"15%\">SHA</th> <th  width = \"85%\">Commit Message</th> </tr>");
            }

            for(int j = 0; j < commits.length(); j++)
            if(!commits.isNull(j)) {
                sb.append("<tr>");
                sb.append("<td>");
                String sha = commits.getJSONObject(j).getString("sha");
                sb.append(sha.substring(0, Math.min(sha.length(), 8)));
                sb.append("</td>");
                sb.append("<td>" + commits.getJSONObject(j).getString("message") + "</td>");
                sb.append("</tr>");
            }
            //html table
            if(commits.length()> 0)
                sb.append("</table>");
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)

            sb.append("<br/>");
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        int PushEventCount = 0;
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int pageNumber = 1; pageNumber < 3; pageNumber++) {
            String url = BASE_URL + user + "/events?page=" + pageNumber;
            System.out.println(url);
            JSONObject json = Util.queryAPI(new URL(url));
            System.out.println(json);
            JSONArray events = json.getJSONArray("root");
            for (int i = 0; i < events.length() && PushEventCount < 10; i++) {
                if (events.getJSONObject(i).getString("type").equals("PushEvent")) {
                    PushEventCount++;
                    eventList.add(events.getJSONObject(i));
                }
            }
        }
        return eventList;
    }
}