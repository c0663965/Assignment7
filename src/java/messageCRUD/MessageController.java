package messageCRUD;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.bean.ApplicationScoped;
import javax.ws.rs.core.Response;
import static messageCRUD.DB.getConnection;
import static messageCRUD.DB.getTableName;

/**
 * Totally Written by Kihoon,Lee (c0663965)
 */
@ApplicationScoped
public class MessageController {
    private final List<Message> list = new ArrayList<>();

    public MessageController() {
    }

    public String getAll() {  //GET
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + getTableName());

            while (rs.next()) {
                Message message = new Message(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("contents"),
                        rs.getString("author"),
                        rs.getString("senttime"));
                list.add(message);
            }
            return gson.toJson(list);
        } catch (SQLException ex) {
            return "SQL Exception:"+ex.getMessage();
        }
    }

    public String getById(String id) { //GET
        Message message = new Message();
        String str;

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + getTableName() + " WHERE id=" + id);

            while (rs.next()) {
                message.setId(rs.getInt("id"));
                message.setTitle(rs.getString("title"));
                message.setContents(rs.getString("contents"));
                message.setAuthor(rs.getString("author"));
                message.setSenttime(rs.getString("senttime"));
            }
            conn.close();
            str=message.toString();
        } catch (SQLException ex) {
            str="SQL Exception :"+ex.getMessage();
        }
        
        return str;
    }

    public String getByDateRange(String startDate, String endDate) throws ParseException { //GET
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

        Date start = format1.parse(startDate); //Converting string to date
        Date end = format1.parse(endDate); //Converting string to date
        List<Message> messages = new ArrayList<>();

        Date senttime;
        String result;

        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + getTableName());

            while (rs.next()) {
                senttime = format2.parse(rs.getString("senttime"));
                if (start.before(senttime) && senttime.before(end)) {
                    Message temp = new Message(rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("contents"),
                            rs.getString("author"),
                            rs.getString("senttime"));
                    messages.add(temp);
                };
            }
            
            result=messages.toString();
            conn.close();
        } catch (SQLException ex) {
            result="SQL Exception" + ex.getMessage();
        }
        
        return result;
    }

    public Response addMessage(String json) throws SQLException { //POST

        JsonParser parser = new JsonParser();
        JsonObject message = (JsonObject) parser.parse(json);

        String title = message.get("title").getAsString();
        String contents = message.get("contents").getAsString();
        String author = message.get("author").getAsString();

        Response res;
        int theLastId=0;

        try (Connection conn = getConnection()) {
            String query = "INSERT INTO " + getTableName() + " (title,contents,author,senttime) "
                                          + "VALUES (?, ?, ?, ?);";

            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, contents);
            ps.setString(3, author);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            ps.setString(4, sdf.format(new Date()));
            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();

            if(rs.next()) 
                theLastId = rs.getInt(1);  
            res=Response.ok(getById(String.valueOf(theLastId))).build();
            conn.close();
        } catch (SQLException ex) {
            res=Response.status(500).entity("SQL Exception" + ex.getMessage()).build();
        }
        return res;
    }

    public Response deleteMessage(String id) { //DELET
        Response res;
        
        try(Connection conn = getConnection()){
            String sql = "DELETE FROM " + getTableName() + " WHERE ID=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(id));
            ps.execute();
            
            res=Response.ok("The message (ID="+id+") has been successfully deleted!").build();
            conn.close();
        }catch (Exception ex){
            res=Response.status(500).entity(ex.getMessage()).build();
        }
        return res;
    }
    
    public Response editMessage(String id, String json) throws SQLException { //PUT without ID
        
        Response res;

        JsonParser parser = new JsonParser();
        JsonObject oldMessage = (JsonObject) parser.parse(getById(id));
        JsonObject newMessage = (JsonObject) parser.parse(json);

        String newContent;
        String oldContent;
      
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String[] columns = {"title","contents","author"};
        StringBuilder updateDetails = new StringBuilder("--- The following data (id="+id+") has been successfully updated. ---\n\n");
        StringBuilder sql = new StringBuilder();
        
        try (Connection conn = getConnection()) {

            Statement stmt = conn.createStatement();
            Map<String,String> updates = new HashMap<>();
            
            for(String value : columns){
                newContent=newMessage.get(value).getAsString();
                oldContent=oldMessage.get(value).getAsString();
                
                if(!newContent.equals(oldContent)){
                    updates.put(value,newContent);
                    updateDetails.append(" \"").append(value).append("\"").
                        append(" : ").append(oldContent+" --> "+newContent).append("\n");
                }
            }
            
            if (!updates.isEmpty()) {
                sql.append("UPDATE "+getTableName()+" SET ");
               
                for(String key : updates.keySet())
                    sql.append(key+"='"+updates.get(key)+"',");
                sql.append("senttime='"+sdf.format(new Date())+"' WHERE id="+id);
                
                stmt.executeUpdate(sql.toString());
                res = Response.ok(getById(id)+"\n\n\n"+updateDetails).build();
            } else 
                res = Response.ok("There is no new content which should be updated in the id="+ id).build();
            conn.close();
        } catch (Exception ex) {
            res = Response.status(500).entity(sql.toString()).build();
        }
        return res;
    }
}