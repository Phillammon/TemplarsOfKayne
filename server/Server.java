import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Server {
    long starttime;
    int ticknumber;
    int entitycount;
    Entity baseentity;
    EntityHolder first;
    TemplarHolder firstTemplar;
    KeyPressReciever inputsock;
    public static void main(String[] args){
        Server server = new Server();
    }
    public Server(){
        this.bootServer();
    }
    protected void bootServer(){
        this.starttime = System.currentTimeMillis();
        this.log("Beginning server boot process.");
        this.log("Initializing listen socket.");
        this.inputsock = new KeyPressReciever(this);
        this.log("Starting to listen for clients.");
        new Thread(this.inputsock).start();
        this.log("Listener thread now running.");
        this.log("Populating server with starting entities.");
        this.populate();
        this.ticknumber = 0;
        this.log("Preparing first tick.");
        System.out.println("-----------------------------------------");
        this.starttime = System.currentTimeMillis();
        this.tickServer();
        this.mainLoop();
    }
    protected void mainLoop(){
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
        if (this.inputsock.first != null){
            KeyPressHolder kp = this.inputsock.first;
            this.inputsock.first = kp.next;
            TemplarHolder ptr = this.firstTemplar;
            TemplarHolder prev = null;
            while (ptr != null) {
                if (ptr.address.equals(kp.address)){
                    ptr.templar.keypresses = kp.keypresses;
                    break;
                }
                else {
                    prev = ptr;
                    ptr = ptr.next;
                }
            }
            if (ptr == null){
                Templar t = new Templar(this.baseentity);
                t.keypresses = kp.keypresses;
                TemplarHolder th = new TemplarHolder(t, kp.address);
                this.addEntity(t);
                if (this.firstTemplar == null){
                    this.firstTemplar = th;
                }
                else {
                    prev.next = th;
                }
            }
        }
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
    public InetAddress address;
    public KeyPressHolder(KeyPresses k, InetAddress a){
        this.keypresses = k;
        this.address = a;
        this.next = null;
    }
}

class KeyPressReciever implements Runnable {
    public Server server;
    public KeyPressHolder first;
    public KeyPressReciever(Server s){
        super();
        this.server = s;
        this.first = null;
    }
    public void run(){
        try {
            KeyPressHolder ptr = null;
            DatagramSocket socket = new DatagramSocket(31337);
            byte[] buffer = new byte[10];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true){
                socket.receive(packet);
                this.server.log(packet.toString());
                if (this.first == null){
                    this.first = new KeyPressHolder(new KeyPresses(false, false, false, false, false, false, 0, 0), packet.getAddress());
                }
                else {
                    ptr = this.first;
                    while (ptr.next != null){
                        ptr = ptr.next;
                    }
                    ptr.next = new KeyPressHolder(new KeyPresses(false, false, false, false, false, false, 0, 0), packet.getAddress());
                }
            }
        }
        catch(Exception e){
            //Shh. Maybe they didn't notice.
        }
    }
    
}

class BastionFreeForAll extends Server {
}