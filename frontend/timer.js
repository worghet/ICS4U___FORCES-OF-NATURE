//timer to make sure animation frames arent happening too fast

class Timer{
    constructor() {
        this.gameTime = 0;
        this.maxStep = 0.08;//how fast iframes iterate, can be adjusted
        this.lastTimeStamp = 0;
    };

    //checks how much time it's been since last tick
    tick(){
        var current = Date.now();//returns current time in millis
        var delta  = (current - this.lastTimeStamp) / 1000;
        this.lastTimeStamp = current;

        var gameDelta = Math.min(delta, this.maxStep);
        this.gameTime += gameDelta;
        return gameDelta;//return difference in time
    }

}