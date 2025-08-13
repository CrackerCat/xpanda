import * as TYPE from "@babel/types";
import {ISimplify} from "./ISimplify";
import {UnusedVRegSimplify} from "./UnusedVRegSimplify";
import {TransformReservedIdentifierSimplifier} from "./TransformReservedIdentifierSimplifier";
import {JustSimplify} from "./JustSimplify";
import {RewriteSimplify} from "./RewriteSimplify";
import {HighLevelGrammarSimplify} from "./HighLevelGrammarSimplify";
import {ClassSimplify} from "./ClassSimplify";
import {ReturnTypeInferenceSimplify} from './ReturnTypeInferenceSimplify'

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
export class SimplifyExport{
    private static simplifyALL(node:TYPE.Program):void{
        const simplifyArr:Array<ISimplify> = [new TransformReservedIdentifierSimplifier(),new JustSimplify(),
            new RewriteSimplify(),new UnusedVRegSimplify(), new ClassSimplify()];
        simplifyArr.forEach((value)=>{
            value.simplify(node);
        })
    }

    public static simplify(node:TYPE.Program):void{
        new ReturnTypeInferenceSimplify().simplify(node);
        for (let i = 0; i < 5; i++) {
            SimplifyExport.simplifyALL(node)
        }
        new HighLevelGrammarSimplify().simplify(node);
    }
}