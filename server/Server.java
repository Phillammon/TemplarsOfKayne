import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    long starttime;
    int ticknumber;
    int entitycount;
    Entity baseentity;
    EntityHolder first;
    public static void main(String[] args){
        Server server = new Server();
    }
    public Server(){
        this.bootServer();
    }
    private void bootServer(){
        this.starttime = System.currentTimeMillis();
        this.populate();
        this.ticknumber = 0;
        this.tickServer();
        while (true){
            if ((System.currentTimeMillis() - this.starttime)/
            (1000/ToKVars.TicksPerSecond) >= this.ticknumber){
                this.log(""+skips);
                skips=0;
                this.tickServer();
            }
            else {
                //this.log("Check for packets here");
            }
        }
    }
    private void populate(){
        this.baseentity = new Entity(this);
        this.addEntity(new Templar(this.baseentity));
    }
    public void addEntity(Entity e){
        if (this.first == null){
            this.first = new EntityHolder(e);
        }
        else {
            EntityHolder ptr = this.first;
            while (ptr.next != null){
                ptr = ptr.next;
            }
            ptr.next = new EntityHolder(e);
            ptr.next.entity.id = this.entitycount;
            this.entitycount++;
        }
    }
    private void cullEntities(){
        this.log("Beginning cull.");
        EntityHolder ptr = this.first;
        while (ptr.next != null){
            if (ptr.next.entity.pending_destruction){
                this.log("Culled entity " + ptr.next.entity.id + " (" + ptr.next.entity.name + ")");
                ptr.next = ptr.next.next;
            }
            else
            {
                ptr = ptr.next;
            }
        }
        this.log("Cull complete.");
    }
    private void tickServer(){
        this.log("Tick " + this.ticknumber + " begins.");
        this.cullEntities();
        this.log("Tick " + this.ticknumber + " ends.");
        this.ticknumber++;
    }
    public void log(String s){
        float time = ((float) (System.currentTimeMillis()-this.starttime))/1000;
        String timestamp = String.format("%.3f", time);
        timestamp = String.format("%1$" + 8 + "s", timestamp);  
        System.out.println(timestamp + ": " + s);
    }
}

class EntityHolder{
    public EntityHolder next;
    public Entity entity;
    public EntityHolder(Entity e){
        this.entity = e;
        this.next = null;
    }
}