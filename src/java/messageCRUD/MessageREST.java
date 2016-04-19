package messageCRUD;

import java.sql.SQLException;
import java.text.ParseException;
import javax.faces.bean.ApplicationScoped;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.Response;
import static messageCRUD.DB.*;

@Path("/messages")
@ApplicationScoped
public class MessageREST{
    MessageController mc = new MessageController();
       
    @GET
    @Path("db")  
    public Response resetDB() throws SQLException{ /*This is just for avoiding the hassle of creating/resetting a new data*/
        Response res;
        try {
            getConnection();
            createTable();
            insertData();
            res = Response.ok("The DB has been successfully created (or reset).").build();
        } catch (SQLException ex) {
            System.out.println("SQL Exception" + ex.getMessage());
            res = Response.status(500).build();
        }
        return res;
    }
    
    @GET
    @Produces("application/json")
    public String getAll() {
       return mc.getAll();
    }
    
    @GET
    @Path("{id}")
    @Produces("application/json")
    public String getById(@PathParam("id") String id){
        return mc.getById(id);
    }
    
    @GET
    @Path("/{startDate}/{endDate}/")
    @Produces("application/json")
    public String getByDateRange(@PathParam("startDate") String startDate,
                                 @PathParam("endDate") String endDate) throws ParseException{
        return mc.getByDateRange(startDate,endDate);
    }
    
    @POST
    @Produces("application/json")
    public Response addMessage(String json) throws SQLException{
        return mc.addMessage(json);
    }   
    
    @PUT
    @Path("{id}")
    @Produces("text/plain")
    public Response editMessage(@PathParam("id") String id, String json) throws SQLException {
        return mc.editMessage(id,json);  
    }

    @DELETE
    @Path("{id}")
    @Produces("text/plain")
    public Response deleteMessage(@PathParam("id") String id) {
        return mc.deleteMessage(id);
    }
}