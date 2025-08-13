import {NodeFactory} from "./NodeFactory";
import {SimplifyExport} from "./simplify/SimplifyExport";
const fs = require('fs');
(()=>{
    if (process.argv.length !== 3) return;
    const a = performance.now();
    const path = process.argv[2]
    try {
        const code = fs.readFileSync(path,"utf-8").toString()
        const node = JSON.parse(code);
        SimplifyExport.simplify(node)

        const code2 = NodeFactory.generate(node);
        const node2 = NodeFactory.parse(code2);
        SimplifyExport.simplify(node2)

        fs.writeFileSync(path,NodeFactory.generate(node2))
    }catch (e){
        fs.writeFileSync(path,"")
    }
    const b = performance.now();
    console.log("time use:",(b-a)/1000);

})()