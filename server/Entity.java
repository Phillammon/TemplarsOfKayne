class Entity{
    public int id;
    public Entity parent;
    public int team;
    public double r;
    public double theta;
    public double v_r;
    public double v_theta;
    public double bounding_radius;
    public boolean pending_destruction;
    public static String name = "Unknown Entity";
    public Entity(int id, Entity parent){
        this.team = 0;                
        this.id = id;                  
        this.parent = parent;
        this.r = ToKVars.PlanetRadius;
        this.theta = 0;
        this.v_r = 0;
        this.v_theta = 0;
        this.bounding_radius = 0;
        this.pending_destruction = false;
    }
    public Entity(){
        this.team = 0;                
        this.id = -1;
        this.parent = this; //REMEMBER YOU HAVE DONE THIS.
        this.r = ToKVars.PlanetRadius;
        this.theta = 0;
        this.v_r = 0;
        this.v_theta = 0;
        this.bounding_radius = 0;
        this.pending_destruction = false;
    }
    public void tick(){
        this.moveTick();
        if (this.theta <= ToKVars.PlanetRadius){
            this.hitGround();
        }
    }
    public void moveTick(){   
        this.r = this.r + this.v_r;
        this.theta = this.theta + this.v_theta;
    }
    public Coords reportPosition(){
        return new Coords(true, this.r, this.theta);
    }
    public void hitGround(){
        this.r = ToKVars.PlanetRadius;
    }
}

class Projectile extends Entity {
    public static String name = "Unknown Projectile";
    public Projectile(int id, Entity parent){
        super(id, parent);
        this.team = parent.team;
    }
    public void moveTick(){   
        this.v_r = this.v_r - ToKVars.Gravity;
        this.r = this.r + this.v_r;
        this.theta = this.theta + this.v_theta;
    }
}

class Templar extends Projectile {
    public static String name = "Unknown Templar";
    public static double movespeed = 0;
    public static int maxhealth = 1;
    public int health;
    public boolean facing;
    public StatusFairy statusfairy;
    public KeyPresses keypresses;
    public Templar(int id, Entity parent){
        super(id, parent);
        this.health = this.maxhealth;
        this.statusfairy = new StatusFairy(this);
        this.keypresses = new KeyPresses();
    }
    public void tick(){
        this.handleInput();
        this.moveTick();
        if (this.theta <= ToKVars.PlanetRadius){
            this.hitGround();
        }
    }
    public void hitGround(){
        this.r = ToKVars.PlanetRadius;
        this.v_theta = 0;
    }
    public void handleInput(){
        if (this.statusfairy.canAct()){
            if (this.statusfairy.canMove()){
                this.facing = this.reportPosition().pathTo(new Coords(false, this.keypresses.cursorx, this.keypresses.cursory), true).theta > 0;
                if (this.facing){
                    if (this.keypresses.movefore && !this.keypresses.moveaft){
                        this.v_theta = this.movespeed;
                    }
                    if (this.keypresses.moveaft && !this.keypresses.movefore){
                        this.v_theta = -this.movespeed;
                    }
                }
                else {
                    if (this.keypresses.movefore && !this.keypresses.moveaft){
                        this.v_theta = -this.movespeed;
                    }
                    if (this.keypresses.moveaft && !this.keypresses.movefore){
                        this.v_theta = this.movespeed;
                    }
                }
            }
        }
    }
}

class StatusFairy {
    private Templar parent;
    private StatusEffect first;
    public StatusFairy (Templar parent){
        this.parent = parent;
    }
    public boolean canAct(){
        return true;
    }
    public boolean canMove(){
        return true;
    }
    public boolean canCast(String tags){
        return true;
    }
    public void tickStatuses(){
    }
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
            double magnitude = Math.sqrt((direction.r*direction.r)+(direction.theta*direction.theta/(ToKVars.ArcLengthOne*ToKVars.ArcLengthOne))); //If you squint, this looks legit
            return new Coords(true, direction.r/magnitude, direction.theta/magnitude);  //The maths might work here. Will be honest, not sure.
        }
        else {
            Coords direction = this.pathTo(target, false);                                      //Maths definitely does work here
            double magnitude = Math.sqrt((direction.x*direction.x)+(direction.y*direction.y));  //Pythag behaves better with straight lines
            return new Coords(false, direction.x/magnitude, direction.y/magnitude);
        }
    }
}

class KeyPresses {
    public boolean spell1;
    public boolean spell2;
    public boolean spell3;
    public boolean spell4;
    public boolean movefore;
    public boolean moveaft;
    public int cursorx;
    public int cursory;
    public KeyPresses(){
        this.spell1 = false;
        this.spell2 = false;
        this.spell3 = false;
        this.spell4 = false;
        this.movefore = false;
        this.moveaft = false;
        this.cursorx = 0;
        this.cursory = 0;
    }
    public KeyPresses(boolean sp1, boolean sp2, boolean sp3, 
                      boolean sp4, boolean mvfore, boolean mvaft,
                      int cursx, int cursy){ //and a partridge in a pear tree
        this.spell1 = sp1;
        this.spell2 = sp2;
        this.spell3 = sp3;
        this.spell4 = sp4;
        this.movefore = mvfore;
        this.moveaft = mvaft;
        this.cursorx = cursx;
        this.cursory = cursy;
    }
}