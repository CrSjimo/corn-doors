//package de.myxrcrs.corndoors.devUtils

//import node.system.fs
const fs = require("fs");

class Zoomer{

    static translate([x,y,z],x0,y0,z0,ratio){
        //console.log(x,y,z);
        x=x0+(x-x0)*ratio;
        y=y0+(y-y0)*ratio;
        z=z0+(z-z0)*ratio;
        //console.log(x,y,z)
        //console.log('\n');
        return [x,y,z];
    }
    
    static handler(path,x0,y0,z0,ratio){
        [x0,y0,z0]=[x0,y0,z0].map(i=>parseFloat(i));
        const obj = JSON.parse(fs.readFileSync(path).toString());
        for(let i in obj.elements){
            obj.elements[i].from = translate(obj.elements[i].from,x0,y0,z0,ratio);
            obj.elements[i].to = translate(obj.elements[i].to,x0,y0,z0,ratio);
        }
        fs.writeFileSync(path,JSON.stringify(obj,undefined,4));
    }

    static main(args){
        this.handler(...args.slice(2));
    }
}

Zoomer.main(process.argv);