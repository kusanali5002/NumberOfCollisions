public class Block  {

    double positionX, velocity, mass;
    int size;

    public Block(double positionX, double velocity, double mass, int size) {
        this.positionX = positionX;
        this.velocity = velocity;
        this.mass = mass;
        this.size = size;
    }

    public void move(double timeStep){
        positionX += velocity * timeStep;
    }

    public void reverseVelocity() {
        velocity = -velocity;
    }
}