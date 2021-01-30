//package de.myxrcrs.corndoors.devUtils

//import node.system.fs
const fs=require("fs");

class EdgeGenerator{
    static dirs = [['north',0],['east',90],['south',180],['west',270]];
    static hinges = ['left','right'];
    static modelPath = "src/main/resources/assets/corndoors/models/block";

    static generate(file,height,hKey,opt){
        let files = fs.readdirSync(this.modelPath).filter(v=>new RegExp(file).test(v));
        //console.log(files);
        let matched = {};
        for(let j=1;j<=height;j++){
                matched[hKey+j+'l']=files.filter(v=>new RegExp(hKey+j+'l').test(v))[0];
                matched[hKey+j+'r']=files.filter(v=>new RegExp(hKey+j+'r').test(v))[0];
        }
        //console.log(matched);
        let obj = {multipart:[]};
        for(let [key,angle] of this.dirs){
            for(let hinge of this.hinges){
                if(opt=='--apply-glass'){
                    obj.multipart.push({
                        when:{
                            facing:key,
                            part:`${hinge}|all`
                        },
                        apply:{
                            model: `corndoors:block/general_glass_noside_${hinge}`,
                            y:angle
                        }
                    })
                }
                for(let j=1;j<=height;j++){
                    let a = {
                        when:{
                            vertical_pos:j-1,
                            facing:key,
                            part:`${hinge}|all`
                        },
                        apply:{
                            model: `corndoors:block/${matched[hKey+j+hinge[0]].split('.')[0]}`
                        }
                    }
                    if(angle!=0)a.apply.y=angle;
                    obj.multipart.push(a);
                }
            }
        }

        fs.writeFileSync("output.json",JSON.stringify(obj,undefined,4));
    }

    static main(args){
        this.generate(...args.slice(2));
    }
}

EdgeGenerator.main(process.argv);