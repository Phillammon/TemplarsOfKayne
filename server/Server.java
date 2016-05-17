import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Server implements Runnable{
    long starttime;
    int ticknumber;
    int entitycount;
    int port;
    Entity baseentity;
    EntityHolder first;
    TemplarHolder firstTemplar;
    KeyPressReciever inputsock;
    public static void main(String[] args){
        if (args.length != 1){
            System.out.println("Please specify a port.");
        }
        else {
            new Thread(new BastionFreeForAll(Integer.parseInt(args[0], 10))).start();
        }
    }
    public Server(int port){
        this.port = port;
        this.bootServer(port);
    }
    protected void bootServer(int port){
        this.starttime = System.currentTimeMillis();
        this.log("Beginning server boot process.");
        this.log("Initializing listen socket.");
        this.inputsock = new KeyPressReciever(this, port);
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
    }
    public void run(){
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
                /*              If the player isn't recognized, don't take it.
                */
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
        this.log("Spawned new " + e.name + " (id " + e.id + ")"); 
    }
    protected void cullEntities(){
        //this.log("Beginning cull.");
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
        //this.log("Cull complete.");
    }
    protected void tickServer(){
        //this.log("Tick " + this.ticknumber + " begins.");
        this.tickEntities();
        this.cullEntities();
        //this.log("Tick " + this.ticknumber + " ends.");
        this.ticknumber++;
    }
    protected void tickEntities(){
        EntityHolder ptr = this.first;
        while (ptr != null){
            if (ptr.entity != null){
                ptr.entity.tick();
            }
            ptr = ptr.next;
        }
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
class AddressHolder{
    public AddressHolder next;
    public InetAddress address;
    public AddressHolder(InetAddress a){
        this.address = a;
        this.next = null;
    }
}

class KeyPressReciever implements Runnable {
    public Server server;
    public KeyPressHolder first;
    public int port;
    public KeyPressReciever(Server s, int port){
        super();
        this.server = s;
        this.first = null;
        this.port = port;
    }
    public void run(){
        try {
            KeyPressHolder ptr = null;
            DatagramSocket socket = new DatagramSocket(this.port);
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true){
                socket.receive(packet);
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
            System.err.println(e);
        }
    }
    
}

class PreConnServer implements Runnable{
    public Server server;
    protected DatagramSocket socket;
    public String name = "Unknown Server Type";
    public static String templarsstring = "";
    public PreConnServer(int port){
        //Preconn servers take connections, sort out teams/templar picks, then boot up a game server.
        try {
            this.socket = new DatagramSocket(port);
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
    public void startGame(int port){
        this.server = new Server(port);
        new Thread(this.server).start();
    }
    public void run(){
        try {
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true){
                this.socket.receive(packet);
                this.server.log("Recieved packet from " + packet.getAddress());
                this.handlePacket(packet, buffer);
            }
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
    public void handlePacket(DatagramPacket packet, byte[] buffer){
        String msg = new String(buffer, 0, packet.getLength());
        packet.setLength(buffer.length);
        if (msg.equals("query")){
            this.server.log("Recieved query from " + packet.getAddress());
            this.returnMessage(this.name + this.templarsstring, packet.getAddress());
        }
        if (msg.equals("pick")){
            this.server.log("Recieved pick from " + packet.getAddress());
            this.templarPicked(packet, msg);
        }
    }
    public void templarPicked(DatagramPacket packet, String msg){
    }
    public void returnMessage(String msg, InetAddress a){
        try {
            byte[] message = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(message, message.length, a, ToKVars.ClientPort);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
    public void addTemplar(InetAddress a, String msg){
        TemplarHolder th = new TemplarHolder(null, a);
        if (this.server.firstTemplar == null){
            this.server.firstTemplar = th;
        }
        else {
            th.next = this.server.firstTemplar;
            this.server.firstTemplar = th;
        }
        this.editTemplar(th, msg);
    }
    public void editTemplar(TemplarHolder th, String message){
        th.templar = new Templar(this.server.baseentity);
        this.server.addEntity(th.templar);
    }
}

class BastionFreeForAll extends PreConnServer {
    public String name = "All Bastion Free For All";
    public int BastionCount;
    public BastionFreeForAll(int port){
        super(port);
        this.BastionCount = 0;
        this.startGame(port+1);
    }
    public void templarPicked(DatagramPacket packet, String msg){
        addTemplar(packet.getAddress(), msg);
        this.returnMessage("go" + this.server.port, packet.getAddress());
    }
    public void editTemplar(TemplarHolder th){
        th.templar = new Bastion(this.server.baseentity);
        th.templar.team = this.BastionCount;
        this.BastionCount++;
        this.server.addEntity(th.templar);
    }
}