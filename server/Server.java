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
    public static void main(String[] args){
    }
}

class Entity extends Object{
    public int id;
    public Entity parent;
    public int team;
    public double r;
    public double theta;
    public double v_r;
    public double v_theta;
    public double bounding_radius;
    public boolean pendingdestruction;
    public static String name = "Unknown Entity";
    public Entity(){
        this.team = 0;                
        this.id = -1;                  
        this.parent = this; //REMEMBER YOU HAVE DONE THIS.
        this.r = ToKVars.PlanetRadius;
        this.theta = 0;
        this.v_r = 0;
        this.v_theta = 0;
        this.boundingradius = 0;
        this.pendingdestruction = false;
    }
    public Entity(int id, Entity parent){
        this.team = 0;                
        this.id = id;                  
        this.parent = parent;
        this.r = ToKVars.PlanetRadius;
        this.theta = 0;
        this.v_r = 0;
        this.v_theta = 0;
        this.boundingradius = 0;
        this.pendingdestruction = false;
    }
    public void tick(){
        this.moveTick();
        if (this.theta <= ToKVars.PlanetRadius){
            this.hitGround();
        }
    }
    private void moveTick(){   
        this.r = this.r + this.v_r;
        this.theta = this.theta + this.v_theta;
    }
    public Coords reportPosition(){
        return new Coords(true, this.r, this.theta);
    }
    
    private void hitGround(){
        this.r = ToKVars.PlanetRadius;
    }
}

class Projectile extends Entity {
    public static String name = "Unknown Projectile";
    public Projectile(int id){
        this.team = 0;
        this.id = id;
        this.r = ToKVars.PlanetRadius;
        this.theta = 0;
        this.v_r = 0;
        this.v_theta = 0;
        this.boundingradius = 0;
        this.pendingdestruction = false;
    }
    private void moveTick(){   
        this.v_r = this.v_r - ToKVars.Gravity;
        this.r = this.r + this.v_r;
        this.theta = this.theta + this.v_theta;
    }
}

class Templar extends Projectile {
    private static double movespeed = 0;
    public static double maxhealth = 1;
    public int health;
    public Templar(int id){
        this.team = 0;
        this.name = "Unknown Templar"
        this.id = id;
        this.r = ToKVars.PlanetRadius;
        this.theta = 0;
        this.v_r = 0;
        this.v_theta = 0;
        this.boundingradius = 0;
        this.pendingdestruction = false;
        this.health = this.maxhealth;
    }
}

class StatusFairy {
}

class StatusEffect {
}

class Coords {
    public double x;
    public double y;
    public double r;
    public double theta;
    public Coords(){
        this.x = 0;
        this.y = 0;
        this.r = 0;
        this.theta = 0;
    }
    public Coords(boolean polar, double val1, double val2){
        if (polar) {
            this.r = val1;
            this.theta = val2;
            this.x = Math.cos(val2) * val1;
            this.y = Math.sin(val2) * val1;
        }
        else {
            this.x = val1;
            this.y = val2;
            this.r = (double) Math.sqrt(val1*val1+val2*val2);
            if (val1 != 0){
                int quad = 0;
                if (val1 < 0){quad++;}
                if (val2 < 0){quad++;}
                this.theta = Math.atan(val2/val1) + Math.PI*quad;
            }
            else {
                if (val2 < 0){
                    this.theta = -Math.PI / 2;
                }
                else{
                    this.theta = Math.PI / 2;
                }
            }
        }
    }
    public Coords pathTo(Coords target, boolean polar){
        if (polar){
            double dtheta = target.theta - this.theta;
            double dr = target.r - this.r;
            if (dtheta > Math.PI){
                dtheta = dtheta - 2*Math.PI;
            }
            if (dtheta < -Math.PI){
                dtheta = dtheta + 2*Math.PI;
            }
            return new Coords(true, dr, dtheta);
        }
        else {
            double dx = target.x - this.x;
            double dy = target.y - this.y;
            return new Coords(false, dx, dy);
        }
    }
    public Coords stepTo(Coords target, boolean polar){ //Generates "one unit" towards the target, for things with set movespeed
        if (polar){                                     //The maths is wonky for the polar side. Don't stare too hard.
            Coords direction = this.pathTo(target, true);
            double magnitude = Math.sqrt((direction.r*direction.r)+(direction.theta*direction.theta/(ArcLengthOne*ArcLengthOne))); //If you squint, this looks legit
            return new Coords(true, direction.r/magnitude, direction.theta/magnitude);  //The maths might work here. Will be honest, not sure.
        }
        else {
            Coords direction = this.pathTo(target, false);                                      //Maths definitely does work here
            double magnitude = Math.sqrt((direction.x*direction.x)+(direction.y*direction.y));  //Pythag behaves better with straight lines
            return new Coords(false, direction.x/magnitude, direction.y/magnitude);
        }
    }
}

class KeyPresses {ph
}