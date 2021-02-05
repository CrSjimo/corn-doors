//package de.myxrcrs.corndoors.devUtils

//import node.system.fs
const fs = require("fs");

class MoveElement{
    
    static handler(path,dx,dy,dz){
        let d =[dx,dy,dz].map(i=>parseFloat(i));
        const obj = JSON.parse(fs.readFileSync(path).toString());
        for(let i in obj.elements){
            obj.elements[i].from = obj.elements[i].from.map((v,i)=>v+d[i]);
            obj.elements[i].to = obj.elements[i].to.map((v,i)=>v+d[i]);
        }
        fs.writeFileSync(path,JSON.stringify(obj,undefined,4));
    }

    static main(args){
        this.handler(...args.slice(2));
    }
}

MoveElement.main(process.argv);