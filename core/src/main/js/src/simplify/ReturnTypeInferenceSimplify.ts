import { Program } from "@babel/types";
import {ISimplify} from "./ISimplify";
import traverse, {NodePath} from "@babel/traverse";
import * as TYPE from "@babel/types";
import generate from "@babel/generator";

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
export class ReturnTypeInferenceSimplify implements ISimplify {

    simplify(node: Program): void {
        console.log("do ReturnTypeInferenceSimplify")
        traverse(TYPE.file(node), {
            CallExpression:(value)=>{
                if (value.node.extra && value.node.extra['callIns']){
                    const parent = value.getStatementParent();
                    if (parent == null) return;
                    const next = parent.getNextSibling();
                    if (next == null) return;

                    if (TYPE.isExpressionStatement(next.node) && TYPE.isAssignmentExpression(next.node.expression)
                        && TYPE.isIdentifier(next.node.expression.left) && next.node.expression.left.name === 'acc'){
                        const binding = value.scope.getBinding('acc');
                        if (binding?.referencePaths){
                            for (let referencePath of binding.referencePaths) {
                                if (referencePath.getStatementParent() === next) return;
                            }
                        }
                        //check parent
                        if (parent.isExpressionStatement() && parent.get('expression').isAssignmentExpression()){
                            const right = parent.get('expression').get('right');
                            console.log("return type....")
                            console.log(generate(parent.node).code)
                            console.log(generate(next.node).code)
                            parent.replaceWith(right as NodePath);
                            parent.scope.crawl();
                        }
                    }
                }
            }
        })
    }

}