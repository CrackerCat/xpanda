import * as TYPE from '@babel/types';
import traverse, {NodePath} from '@babel/traverse'
import {ISimplify} from "./ISimplify";
import generate from "@babel/generator";
/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
export class UnusedVRegSimplify implements ISimplify {
    private isContainClassNode(node:TYPE.Node):boolean{
        if (TYPE.isClassExpression(node)) return true;
        return !!(TYPE.isMemberExpression(node) && TYPE.isClassExpression(node.object));
    }

    private checkNotEffect(path:NodePath<TYPE.Expression>):boolean{
        if (this.isContainClassNode(path.node)) return false;
        return TYPE.isIdentifier(path.node) || TYPE.isLiteral(path.node) || TYPE.isMemberExpression(path.node)
    }

    simplify(node: TYPE.Program): void {
        console.log("do UnusedVRegSimplify")
        traverse(TYPE.file(node), {
            //simplify unused variable
            VariableDeclarator:(value)=>{
                if (TYPE.isIdentifier(value.node.id)){
                    const binding = value.scope.getBinding(value.node.id.name)
                    if (binding === undefined) return;
                    if (value.node.id.name.startsWith("panda_jmp0_reserved_p") && binding.constantViolations.length == 0){
                        value.remove();
                    }else if (!binding.referenced && binding.constantViolations.length == 0){
                        if (value.node.trailingComments !== null){
                            value.node.trailingComments = null;
                        }
                        value.remove();

                    }
                }
            },
            //simplify unused result of expression
            AssignmentExpression:(value)=>{
                const left = value.get('left');
                if (TYPE.isIdentifier(left.node)){
                    const binding = value.scope.getBinding(left.node.name);
                    if (!binding || binding.referenced) return;
                    //no referenced
                    const right = value.get('right');
                    if (this.checkNotEffect(right)){
                        value.remove();
                    }else {
                        value.replaceWith(right)
                    }
                    value.scope.crawl()
                }
            },
            ExpressionStatement:(value:NodePath<TYPE.ExpressionStatement>)=>{
                //remove single this
                if (value.get('expression').isThisExpression()){
                    value.remove();
                    value.scope.crawl();
                }else if (value.get('expression').isBinaryExpression()){
                    value.remove();
                    value.scope.crawl();
                }
            }
        });
    }
}