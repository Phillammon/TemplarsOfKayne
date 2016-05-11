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
    double r;
    double theta;
    double v_r;
    double v_theta;
    double radius;
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

class GravityAffected extends Entity {
    private void moveTick(){   
        this.v_r = this.v_r - ToKVars.Gravity;
        this.r = this.r + this.v_r;
        this.theta = this.theta + this.v_theta;
    }
}

class Templar extends GravityAffected {
}

class Hitbox extends Entity {
}

class Projectile extends GravityAffected {
}

class Missile extends Entity {
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
}