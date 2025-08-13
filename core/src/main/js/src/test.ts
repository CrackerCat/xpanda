import generate from "@babel/generator";
import {readFileSync,writeFileSync} from "fs"
import {parse} from "@babel/parser";
import * as path from "path";
import {SimplifyExport} from "./simplify/SimplifyExport";
const inPath = path.resolve(__dirname,"../../../test/resources/debug.js");
const outPath = path.resolve(__dirname,"../../../test/resources/debug_out.js");
const code = readFileSync(inPath,"utf-8").toString()

const node = parse(code,{
    allowImportExportEverywhere:true
}).program


SimplifyExport.simplify(node)
const result = generate(node).code
writeFileSync(outPath,result);