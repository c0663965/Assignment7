package messageCRUD;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kihoon, Lee
 */

public class DB {

    private static final String db = "test";
    private static final String table = "messagebox";

    public String getDb() {
        return db;
    }
    
    public static String getTableName() {
        return table;
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        String url = "jdbc:mysql://localhost:3306/"+db;
        return DriverManager.getConnection(url,"root","");
    }
    
    public static void createTable() {
         
        try (Connection connection = getConnection()){
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS " + table);
            
            String sql = "CREATE TABLE IF NOT EXISTS " + table
                    + " (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "title VARCHAR(200),"
                    + "contents VARCHAR(200),"
                    + "author VARCHAR(200),"
                    + "senttime VARCHAR(100));";

            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println("SQL Exception" + ex.getMessage());
        }
    }
    
    public static void insertData() {
       
        try (Connection conn = getConnection()) {
            Statement stmt=conn.createStatement();

            stmt.executeUpdate("INSERT INTO "+table+" VALUES "+
                    "(1,'English','Practicing English speaking','Kihoon','2011-12-01 21:03:21')," +
                    "(2,'Java2','Jax-RS assignment','Gagan','2013-02-25 05:03:21')," +
                    "(3,'Oracle','Final term project','Jaesuk','2015-08-25 12:07:24')," +
                    "(4,'jQuery','Making web pages','Hyoju','2016-12-10 15:13:12'),"+
                    "(5,'Mathematics','Solving equations','Kukjin','2012-12-10 25:13:12'),"+
                    "(6,'Co-op','Writing a resume','Hanbyul','2015-07-31 17:17:35')");
        } catch (SQLException ex) {
            System.out.println("SQL Exception" + ex.getMessage());
        }
    }
}

/****************************************************************************************************/
/************************* This is the initial data used to run this program. ***********************/
/****************************************************************************************************/

//USE test;
//DROP TABLE IF EXISTS messagebox;
//CREATE TABLE messagebox 
//    (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
//    title VARCHAR(50),
//    contents VARCHAR(50),
//    author VARCHAR(20),
//    senttime VARCHAR(30));
//
//INSERT INTO messagebox VALUES(1,"English","Practicing speaking","Kihoon","2011-12-01 21:03:21");
//INSERT INTO messagebox VALUES(2,"Java2","Jax-RS assignment","Gagan","2013-02-25 05:03:21");
//INSERT INTO messagebox VALUES(3,"Oracle","Term Project final","Jaesuk","2015-08-25 12:07:24");
//INSERT INTO messagebox VALUES(4,"Jquery","Making web pages","Hyoju","2016-12-10 15:13:12");
//INSERT INTO messagebox VALUES(5,"Mathematics","Solving equatiions","Kukjin","2012-12-10 25:13:12");
//INSERT INTO messagebox VALUES(6,"Co-op","Writing a resume","Hanbyul","2015-07-31 17:17:35");