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


public class Entity {
	float r = 0.0;
	float theta = 0.0;
	float v_r = 0.0;
	float v_rtheta = 0.0f;
	public void tick(){
		this.r = this.r + this.v_r;
		this.theta = this.theta + this.v_theta;
	}
	public  reportPosition(){
		
	}
}

public class GravityAffected extends Entity {
}

public class Templar extends GravityAffected {
}

public class Hitbox extends Entity {
}

public class Projectile extends GravityAffected {
}

public class Missile extends Entity {
}

public class StatusFairy {
}

public class StatusEffect {
}

public class Coords {
	float x
	float y
	float r
	float theta
	public Coords {
		this.x = 0;
		this.y = 0;
		this.r = 0;
		this.theta = 0;
	}
	public Coords(boolean polar, float val1, float val2){
		if (polar) {
			this.r = val1;
			this.theta = val2;
			this.x = Math.cos(val2) * val1;
			this.y = Math.sin(val2) * val1;
		}
		else {
			this.x = val1;
			this.y = val2;
			this.r = Math.sqrt(val1*val1+val2*val2);
			if (val1 != 0){
				int quad = 0;
				if (val1 < 0){quad++;}
				if (val2 < 0){quad++;}
				this.theta = Math.atan(val2/val1) + Math.pi*quad;
			}
			else {
				if (val2 < 0){
					this.theta = -Math.pi / 2;
				}
				else{
					this.theta = Math.pi / 2;
				}
			}
		}
	}
}
