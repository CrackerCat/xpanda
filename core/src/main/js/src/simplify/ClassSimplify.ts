import { Program } from "@babel/types";
import {ISimplify} from "./ISimplify";
import traverse, {NodePath} from "@babel/traverse";
import * as TYPE from "@babel/types";
import generate from "@babel/generator";

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

class GetterSetterDesc{
    public target:TYPE.Identifier;
    public name:string;
    public getterFunc:TYPE.FunctionExpression|TYPE.Identifier|null;
    public setterFunc:TYPE.FunctionExpression|TYPE.Identifier|null;
    public clazz:TYPE.ClassExpression|null
}

export class ClassSimplify implements ISimplify {

    private checkAssignLeftRight(node:TYPE.AssignmentExpression,ln:string,rn:string){
        const l = node.left;
        const r = node.right;
        return TYPE.isIdentifier(l) && l.name === ln && TYPE.isIdentifier(r) && r.name === rn;
    }

    private parseGetterSetterCallExpression(node:TYPE.CallExpression,gsd:GetterSetterDesc):string{
        const callee = node.callee;
        if (TYPE.isMemberExpression(callee) && TYPE.isIdentifier(callee.property)){
            if (TYPE.isIdentifier(callee.object))
                gsd.target = callee.object
            if (callee.property.name === '__defineSetter__'){
                const args = node.arguments;
                if (args.length !== 2) return;
                const name = args[0]
                if (!TYPE.isStringLiteral(name)) return;
                gsd.name = name.value;
                const v2 = args[1];
                if (TYPE.isFunctionExpression(v2)){
                    gsd.setterFunc = v2;
                }else if (TYPE.isIdentifier(v2)){
                    if (v2.name === "undefined"){
                        gsd.setterFunc = undefined
                    }else gsd.setterFunc = v2;
                }
                return "setter";
            }else if (callee.property.name === '__defineGetter__'){
                const args = node.arguments;
                if (args.length !== 2) return;
                const name = args[0]
                if (!TYPE.isStringLiteral(name)) return;
                gsd.name = name.value;
                const v2 = args[1];
                if (TYPE.isFunctionExpression(v2)){
                    gsd.getterFunc = v2;
                }else if (TYPE.isIdentifier(v2)){
                    if (v2.name === "undefined"){
                        gsd.getterFunc = undefined
                    }else gsd.getterFunc = v2;
                }
                return "getter";
            }
        }
        return "";
    }

    simplify(node: Program): void {
        console.log("do ClassSimplify")
        traverse(TYPE.file(node), {
            ClassExpression:(value:NodePath<TYPE.ClassExpression>)=>{
                if (TYPE.isIdentifier(value.node.superClass) && value.node.superClass.name === 'hole'){
                    value.node.superClass = null;
                }
            },
            CallExpression:(value:NodePath<TYPE.CallExpression>)=>{
                const gsd = new GetterSetterDesc();
                const callee = value.get('callee');
                if (this.parseGetterSetterCallExpression(value.node,gsd) === 'setter'){
                    const pres = callee.getStatementParent().getAllPrevSiblings();
                    const needClearArr: Array<NodePath<TYPE.Node>> = [value.getStatementParent()]
                    let bodyRange = true;
                    for (let pre of pres) {
                        //get __defineGetter__
                        if (bodyRange && TYPE.isExpressionStatement(pre.node) && TYPE.isCallExpression(pre.node.expression)){
                            this.parseGetterSetterCallExpression(pre.node.expression,gsd);
                        }
                        if (bodyRange){
                            needClearArr.push(pre);
                        }
                        if (gsd.target == undefined){break;}
                        //range
                        if (TYPE.isExpressionStatement(pre.node) && TYPE.isAssignmentExpression(pre.node.expression)
                            && this.checkAssignLeftRight(pre.node.expression,"acc",gsd.target.name)){
                            bodyRange = false;
                            continue;
                        }
                        //class
                        if (TYPE.isExpressionStatement(pre.node) && TYPE.isAssignmentExpression(pre.node.expression)
                        && TYPE.isClassExpression(pre.node.expression.right)){
                            gsd.clazz = pre.node.expression.right;
                            break;
                        }
                    }
                    if (gsd.clazz){
                        let doit = false;
                        if (gsd.getterFunc && gsd.getterFunc.type === "FunctionExpression"){
                            gsd.clazz.body.body.push(TYPE.classMethod('get',TYPE.identifier(gsd.name),[],gsd.getterFunc.body))
                            doit = true;
                        }
                        if (gsd.setterFunc && gsd.setterFunc.type === "FunctionExpression"){
                            gsd.clazz.body.body.push(TYPE.classMethod('set',TYPE.identifier(gsd.name),gsd.setterFunc.params,gsd.setterFunc.body))
                            doit = true;
                        }
                        if (doit){
                            for (let needClearArrElement of needClearArr) {
                                needClearArrElement.remove();
                            }
                            value.scope.crawl()
                        }
                    }

                }

            },
            ExpressionStatement:(value:NodePath<TYPE.ExpressionStatement>)=>{
                //transform single class expression to class declaration
                const path = value.get('expression');
                if (path.isClassExpression()){
                    const classExp = path.node;
                    value.replaceWith(TYPE.classDeclaration(classExp.id,classExp.superClass,classExp.body,classExp.decorators))
                    value.scope.crawl()
                }
            }

        })
    }

}