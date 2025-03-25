//this uses a js canvas instead of the css commands and its easier to use the css stuff in functionality.js
//will delete later but for now the reference is still helpful

class animation{
   constructor(spritesheet, xStart, yStart, height, width, frameCount, frameDuration, loop){
    Object.assign(this, { spritesheet, xStart, yStart, height, width, frameCount, frameDuration, loop });

    this.elapsedTime = 0;
    this.totalTime = this.frameCount * this.frameDuration;
   };

   drawFrame(tick, ctx, x, y, scale){
    this.elapsedTime+=tick;
    
    if(this.loop){
        this.elapsedTime -= this.totalTime; //resets animation
    } else {
        return;
    }

    let frame = this.currentFrame();

    ctx.drawImage(this.spritesheet, this.xStart + frame * this.width, this.yStart,
    this.width, this.height, x, y, this.width * scale, this.height * scale);

   };//end of drawFrame method

   currentFrame(){
    return Math.floor(this.elapsedTime / this.frameDuration);
   }; //end of currentFrame() method

   isDone() {
    return (this.elapsedTime >= this.totalTime);
   }


}//end of animator class