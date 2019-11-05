
import java.sql.Connection;







/**
 *
 * @author Educom
 */
public class MI6Start 
{
    static Connection conn = DatabaseManager.connectDb(); 
    public static void main(String[] args)
    {
        MainClass start = new MainClass(conn);
        start.Run();
    }
}


