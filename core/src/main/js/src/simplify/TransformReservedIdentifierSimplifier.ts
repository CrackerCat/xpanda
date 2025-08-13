import { Program } from "@babel/types";
import {ISimplify} from "./ISimplify";
import traverse, {cache, NodePath} from "@babel/traverse";
import * as TYPE from "@babel/types";
import generate from "@babel/generator";
import scope = cache.scope;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
export class TransformReservedIdentifierSimplifier implements ISimplify {
    simplify(node: Program): void {
        console.log("do TransformReservedIdentifierSimplifier")
        traverse(TYPE.file(node), {
            Identifier:(path,state)=>{
                if (path.getStatementParent().node.type == "VariableDeclaration") return;
                if (path.node.name.startsWith("panda_jmp0_reserved_param")){
                    if (path.node.name.endsWith("p0")){
                        const func = path.getFunctionParent()
                        if (func == null){
                            path.getStatementParent().remove();
                            path.skip()
                            return;
                        }
                        if (TYPE.isFunctionExpression(func.node)){
                            if (func.node.id == null){
                                path.getStatementParent().remove()
                                path.skip()
                                return;
                            }else path.node.name = func.node.id.name;
                        }
                        if (TYPE.isClassMethod(func.node)){
                            if (func.node.kind == 'constructor'){
                                const clazzNode = func.findParent((path)=>{
                                    return path.isClassExpression();
                                });
                                if (TYPE.isClassExpression(clazzNode.node)){
                                    path.node.name = clazzNode.node.id.name;
                                }
                            }else {
                                if (TYPE.isIdentifier(func.node.key))
                                    path.node.name = func.node.key.name;
                            }
                        }
                        path.skip()
                        return;
                    }
                    if (path.node.name.endsWith("p1")){
                        path.replaceWith(TYPE.memberExpression(TYPE.identifier("new"),TYPE.identifier("target"),false))
                        path.skip()
                        return;
                    }
                }
                if(path.node.name === "panda_jmp0_reserved_sendable_class"){
                    const func = path.getFunctionParent();
                    if (func.isClassMethod()){
                        const clazz = func.parentPath.parentPath;
                        if (TYPE.isClass(clazz.node)){
                            path.replaceWith(clazz.node.id)
                        }
                    }
                }
                if (path.node.name === 'panda_jmp0_reserved_lex_begin'){
                    path.getStatementParent().remove();
                    return;
                }
            },
            Program:(path)=>{
                const bindings = path.scope.getAllBindings();
                for (let bindingsKey in bindings) {
                    if (bindingsKey.startsWith("panda_jmp0_reserved_export_name_")){
                        const binding = bindings[bindingsKey]
                        const keyName = bindingsKey.substring(32)
                        if (path.scope.references[keyName] === true){
                            //remove
                            for (let referencePath of binding.referencePaths) {
                                referencePath.replaceWith(TYPE.identifier(keyName))
                            }
                            for (let constantViolation of binding.constantViolations) {
                                constantViolation.remove();
                                constantViolation.skip()
                            }
                            binding.path.remove()
                            binding.path.skip()
                        }

                    }
                }
            }
        })
    }

}