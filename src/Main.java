/**
 * Created by alexdavis on 08/04/15.
 */
import java.*;
public class Main {

    public static void main(String[] args) {
        Task1 t1 = new Task1();
        try {
            System.out.println(t1.make().get("http://armincerf.myqnapcloud.com:32400"));
        }
        catch(NetException e){
            System.out.println(e.msg_);
        }
        Task2 t2 = new Task2();

    }
}
