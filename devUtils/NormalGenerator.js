//package de.myxrcrs.corndoors.devUtils

//import node.system.fs
const fs=require("fs");

class NormalGenerator{
    
    static h = [,'a','b','c','d','e','f','g'];
    static dirs = [['north',0],['east',90],['south',180],['west',270]];
    static hinges = ['left','right'];
    static modelPath = "src/main/resources/assets/corndoors/models/block";

    static generate(file,width,height,opt){
        let files = fs.readdirSync(this.modelPath).filter(v=>new RegExp(file).test(v));
        //console.log(files);
        let matched = {};
        for(let i=1;i<=width;i++){
            for(let j=1;j<=height;j++){
                matched[this.h[i]+j+'l']=files.filter(v=>new RegExp(this.h[i]+j+'l').test(v))[0];
                matched[this.h[i]+j+'r']=files.filter(v=>new RegExp(this.h[i]+j+'r').test(v))[0];
            }
        }
        //console.log(matched);
        let obj = opt=='--apply-glass'?{multipart:[]}:{variants:{}};
        for(let [key,angle] of this.dirs){
            if(opt=='--apply-glass')obj.multipart.push({
                when:{facing:key},
                apply:{
                    model:'corndoors:block/general_glass_noside',
                    y:angle
                }
            });
            for(let hinge of this.hinges){
                for(let i=1;i<=width;i++){
                    for(let j=1;j<=height;j++){
                        let a = {
                            model: `corndoors:block/${matched[this.h[i]+j+hinge[0]].split('.')[0]}`
                        }
                        if(angle!=0)a.y=angle;
                        if(opt=='--apply-glass'){
                            obj.multipart.push({
                                when: {
                                    horizontal_pos:i-1,
                                    vertical_pos:j-1,
                                    facing:key,
                                    hinge:hinge
                                },
                                apply:a
                            });
                        }else{
                            obj.variants[`horizontal_pos=${i-1},vertical_pos=${j-1},facing=${key},hinge=${hinge}`] = a;
                        }
                    }
                }
            }
        }
        fs.writeFileSync("output.json",JSON.stringify(obj,undefined,4));
    }

    static main(args){
        this.generate(...args.slice(2));
    }
}

NormalGenerator.main(process.argv);