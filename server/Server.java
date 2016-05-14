import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

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
    protected void bootServer(){
        this.starttime = System.currentTimeMillis();
        this.populate();
        this.ticknumber = 0;
        this.tickServer();
        while (true){
            if ((System.currentTimeMillis() - this.starttime)/
            (1000/ToKVars.TicksPerSecond) >= this.ticknumber){
                this.tickServer();
            }
            else {
                this.processInput();
            }
        }
    }
    private void populate(){
        this.baseentity = new Entity(this);
        this.first = new EntityHolder(this.baseentity);
    }
    protected void processInput(){
        
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
    protected void cullEntities(){
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
    protected void tickServer(){
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

class TemplarHolder{
    public TemplarHolder next;
    public Templar templar;
    public InetAddress address;
    public TemplarHolder(Templar t, InetAddress a){
        this.templar = t;
        this.address = a;
        this.next = null;
    }
}

class KeyPressHolder{
    public KeyPressHolder next;
    public KeyPresses keypresses;
    public KeyPressHolder(KeyPresses k){
        this.keypresses = k;
        this.next = null;
    }
}

class KeypressReciever implements Runnable {
    public Server server;
    public KeyPressHolder first;
    public KeypressReciever(Server s){
        super();
        this.server = s;
    }
    public void run(){
        try {
            DatagramSocket socket = new DatagramSocket(31337);
            byte[] buffer = new byte[10];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true){
                socket.receive(packet);
                this.server.log(packet.toString());
                
            }
        }
        catch(Exception e){
            //Shh. Maybe they didn't notice.
        }
    }
    
}

class BastionFreeForAll extends Server {
}