import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    long starttime;
    int ticknumber;
    public static void main(String[] args){
        Server server = new Server();
    }
    public Server(){
        this.bootServer();
    }
    private void bootServer(){
        this.starttime = System.currentTimeMillis();
        this.ticknumber = 0;
        this.tickServer();
        while (true){
            if ((System.currentTimeMillis() - this.starttime)/
            (1000/ToKVars.TicksPerSecond) >= this.ticknumber){
                this.tickServer();
            }
            else {
                //this.log("Check for packets here");
            }
        }
    }
    private void tickServer(){
        this.ticknumber++;
        this.log("Tick " + this.ticknumber + " begins.");
    }
    public void log(String s){
        float time = ((float) (System.currentTimeMillis()-this.starttime))/1000;
        String timestamp = String.format("%.2f", time);
        timestamp = String.format("%1$" + 7 + "s", timestamp);  
        System.out.println(timestamp + ": " + s);
    }
}