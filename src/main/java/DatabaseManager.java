/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Educom
 */
public class DatabaseManager {
    
    public static Connection connectDb(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mi6", "cooldog", "6supercool4");
            System.out.println("Connected");
            return conn;
        }
        catch(SQLException | ClassNotFoundException e){
            System.out.println(e);
        }
        return null;
    }
    
    public static boolean checkServiceNumber(short serviceNumber, Connection conn){
        String SELECT_ACTIVE_MYSQL = "SELECT active FROM agents WHERE service_number = ?;";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(SELECT_ACTIVE_MYSQL);
            preparedStatement.setShort(1, serviceNumber);
            System.out.println(preparedStatement);
            
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next())
            {
                return rs.getBoolean("active");
            }
            return false;
        }
        catch(SQLException ex){
            for(Throwable e: ex){
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while(t != null){
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
        return false;
    }
    
    public static int getAgentId(short serviceNumber, Connection conn){
        String SELECT_ID_SQL = "SELECT id FROM agents WHERE service_number= ?;";
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(SELECT_ID_SQL);
            preparedStmt.setShort(1, serviceNumber);
            System.out.println(preparedStmt);
            
            ResultSet rs = preparedStmt.executeQuery();
            while(rs.next()){
                return rs.getInt("id");
            }
            return 0;
        }
        catch(SQLException ex){
            for(Throwable e: ex){
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while(t != null){
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
        return 0;
    }
    
    public static int checkTimedOut(int agentId, Connection conn){
        String SELECT_TIMEOUT_DURATION = 
                "SELECT timeout_duration FROM logins WHERE agent_id= ? ORDER BY id DESC LIMIT 1;";
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(SELECT_TIMEOUT_DURATION);
            preparedStmt.setInt(1, agentId);
            System.out.println(preparedStmt);
            
            ResultSet rs = preparedStmt.executeQuery();
            
            while(rs.next()){
                return rs.getInt("timeout_duration");
            }
        }
        catch(SQLException ex){
            for(Throwable e: ex){
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while(t != null){
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
        return 0;
    }
    
      public static Agent authenticateAgent(short serviceNumber, String secret, Connection conn){
        String SELECT_ACTIVE_SQL = "SELECT * FROM agents WHERE service_number= ? AND secret_code= ?;";
        Agent agent;
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(SELECT_ACTIVE_SQL);
            preparedStmt.setShort(1, serviceNumber);
            preparedStmt.setString(2, secret);
            
            ResultSet rs = preparedStmt.executeQuery();
            
            while(rs.next()){
                System.out.println(preparedStmt);
                boolean active = rs.getBoolean("active");
                boolean licenseToKill = rs.getBoolean("license_to_kill");
                
                LocalDateTime licenseEndTerm = Instant.ofEpochMilli(rs.getDate("license_end_term").getTime())
                                                       .atZone(ZoneId.systemDefault())
                                                       .toLocalDateTime();
                agent = new Agent(active, licenseToKill, licenseEndTerm);
                return agent;
            }
        }
        catch(SQLException ex){
            for(Throwable e: ex){
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while(t != null){
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
        
        agent = new Agent(false, false, null);
        return agent;
    }  

    public static void addLoginAttempt(LocalDateTime localDT, boolean ifSucceed, int timeOut, int agentId, Connection conn){
        String INSERT_LOGIN_SQL = "INSERT INTO logins (agent_id, login_attempt, succeed, timeout_duration)"
                + "VALUES(?, ?, ?, ?);";
        try{
            Timestamp timestamp = Timestamp.valueOf(localDT);
            PreparedStatement preparedStmt = conn.prepareStatement(INSERT_LOGIN_SQL);
            
            preparedStmt.setInt(1, agentId);
            preparedStmt.setTimestamp(2, timestamp);
            preparedStmt.setBoolean(3, ifSucceed);
            preparedStmt.setInt(4, timeOut);
            
            preparedStmt.executeUpdate();
        }
        catch(SQLException ex){
            for(Throwable e: ex){
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while(t != null){
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
    
    public static Timestamp getLastLoginAttempt(int agentId, Connection conn){
        String SELECT_ATTEMPT_SQL = "SELECT login_attempt FROM logins WHERE agent_id= ? ORDER BY id DESC LIMIT 1;";
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(SELECT_ATTEMPT_SQL);
            preparedStmt.setInt(1, agentId);
            ResultSet rs = preparedStmt.executeQuery();
            
            while(rs.next()){
                return rs.getTimestamp("login_attempt");
            }
            return null;
        }
        catch(SQLException ex){
            for(Throwable e: ex){
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while(t != null){
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
        return null;
    }
    
    public static List<LoginAttempt> getLoginAttempts(Timestamp lastLogin, Connection conn){
        String SELECT_LOGINS_SQL = "SELECT * FROM logins WHERE login_attempt <= ?;";
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(SELECT_LOGINS_SQL);
            preparedStmt.setTimestamp(1, lastLogin);
            ResultSet rs = preparedStmt.executeQuery();
            
            List<LoginAttempt> list = new ArrayList<>();
            
            while(rs.next()){
                list.add( new LoginAttempt( rs.getTimestamp("login_attempt").toLocalDateTime(), 
                                            rs.getBoolean("succeed") ) );
            }
            return list;
        }
        catch(SQLException ex){
            for(Throwable e: ex){
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while(t != null){
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
        return null;
    }
    
    public static void closeConn(Connection conn){
        try{
            conn.close();
        }
        catch(SQLException ex){
            for(Throwable e: ex){
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while(t != null){
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
