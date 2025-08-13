import { Program } from "@babel/types";
import {ISimplify} from "./ISimplify";
import traverse, {NodePath} from "@babel/traverse";
import * as TYPE from "@babel/types";

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
export class RewriteSimplify implements ISimplify {
    public static binaryInvert(node:TYPE.BinaryExpression){
        const op = node.operator;
        let invertOp = null;
        switch (op) {
            case "!=":{
                invertOp = "==";
                break;
            }
            case "!==":{
                invertOp = "===";
                break;
            }
            case "<":{
                invertOp = ">=";
                break;
            }
            case "<=":{
                invertOp = ">";
                break;
            }
            case ">":{
                invertOp = "<=";
                break;
            }
            case "==":{
                invertOp = "!=";
                break;
            }
            case "===":{
                invertOp = "!==";
                break;
            }
            case ">=":{
                invertOp = "<";
                break;
            }
        }
        return invertOp;
    }

    private InvertExpressionRecursive(node:TYPE.Expression):TYPE.Expression{
        if (TYPE.isBinaryExpression(node)){
            const invert = RewriteSimplify.binaryInvert(node);
            if (invert == null) return;
            node.operator = invert;
        }else if (TYPE.isLogicalExpression(node)){
            if (TYPE.isBinaryExpression(node.left) || TYPE.isLogicalExpression(node.left)){
                this.InvertExpressionRecursive(node.left)
            }else {
                node.left = TYPE.unaryExpression('!',node.left)
            }
            if (TYPE.isBinaryExpression(node.right) || TYPE.isLogicalExpression(node.right)){
                this.InvertExpressionRecursive(node.right)
            }else {
                node.right = TYPE.unaryExpression('!',node.right)
            }
            if (node.operator === '||'){
                node.operator = '&&';
            }else {
                node.operator = '||';
            }
        }else {
            node = TYPE.unaryExpression('!',node)
        }
        return node;
    }

    private simplifyTestExpression(path:NodePath<TYPE.Expression>){
        const node = path.node;
        if (TYPE.isLogicalExpression(node)){
            const logic = path as NodePath<TYPE.LogicalExpression>;
            this.simplifyTestExpression(logic.get('left'))
            this.simplifyTestExpression(logic.get('right'))
            return;
        }
        if (TYPE.isUnaryExpression(node) && node.operator === '!'){
            const arg = node.argument;
            if (TYPE.isUnaryExpression(arg)  && arg.operator === '!'){
                const rr = arg.argument;
                path.replaceWith(rr);
                this.simplifyTestExpression(path)
            }
            if (TYPE.isBinaryExpression(arg)){
                const invert = RewriteSimplify.binaryInvert(arg);
                if (invert == null) return;
                arg.operator = invert;
                path.replaceWith(arg);
                this.simplifyTestExpression(path)
            }
            if (TYPE.isLogicalExpression(arg)){
                this.InvertExpressionRecursive(arg);
                path.replaceWith(arg);
                this.simplifyTestExpression(path)
            }
        }
    }
    private checkIsAccUndefined(node:NodePath<TYPE.Statement>){
        return TYPE.isExpressionStatement(node.node)
            && TYPE.isAssignmentExpression(node.node.expression)
            && TYPE.isIdentifier(node.node.expression.left) && node.node.expression.left.name === 'acc'
            && TYPE.isIdentifier(node.node.expression.right) && node.node.expression.right.name === 'undefined';
    }

    private removeFunctionReturnUndefined(body:NodePath<TYPE.BlockStatement>):boolean{
        const b = body.get('body');
        if (b.length < 2) return;
        const last = b[b.length-1];
        const last2 = b[b.length-2];
        if (this.checkIsAccUndefined(last2) &&
            TYPE.isReturnStatement(last.node) && last.node.argument == null){
            last.remove();
            last2.remove();
            return true;
        }
        return false;
    }
    private duplicateAccUndefined(block:NodePath<TYPE.BlockStatement>){
        const body = block.get('body');
        const needRemove = [];
        let flag = false;
        for (let i = 0; i < body.length; i++) {
            if (this.checkIsAccUndefined(body[i])){
                if (i + 1 === body.length) return;
                for (let j = i + 1; j < body.length; j++) {
                    if (this.checkIsAccUndefined(body[j])){
                        needRemove.push(body[j]);
                    }else{
                        flag = true;
                        break;
                    }
                }
                if (flag) break;
            }
        }
        flag = false;
        for (let needRemoveElement of needRemove) {
            needRemoveElement.remove();
            flag = true;
        }
        return flag;
    }

    private changeCallExpression(value:NodePath<TYPE.CallExpression>){
        const callee = value.get('callee');
        if (callee.type === 'MemberExpression'){
            const prop = (callee as NodePath<TYPE.MemberExpression>).get('property')
            const object = (callee as NodePath<TYPE.MemberExpression>).get('object')
            if (TYPE.isIdentifier(prop.node) && TYPE.isMemberExpression(object.node) && prop.node.name === 'call'){
                callee.replaceWith(object);
                if (TYPE.isMemberExpression(callee.node) && callee.node.property.type === 'StringLiteral'){
                    (callee as NodePath<TYPE.MemberExpression>).get('property').replaceWith(TYPE.identifier(callee.node.property.value))
                    callee.node.computed = false;
                }
                value.get('arguments')[0].remove();
                value.scope.crawl();
            }
        }
    }

    private invertTestExpression(path:NodePath<TYPE.Expression>){
        if (path.isUnaryExpression() && path.node.operator === '!'){
            path.replaceWith(path.get('argument'))
        }else if (path.isBinaryExpression()){
            const op = RewriteSimplify.binaryInvert(path.node);
            if (op == null){
                path.replaceWith(TYPE.unaryExpression('!',path.node));
            }else {
                path.node.operator = op;
            }
        }else {
            path.replaceWith(TYPE.unaryExpression('!',path.node));
        }
    }


    private checkAndTransformToIfElse(value:NodePath<TYPE.IfStatement>):boolean{
        const consequent = value.get('consequent');
        const alternate = value.get('alternate');
        const test = value.get('test');
        if (alternate.isBlockStatement() && alternate.get('body').length === 1
            && alternate.get('body')[0].isIfStatement()){
            /**
             * if(test){
             *  ...
             * }else{
             *     if(test){...}else{...}
             * }
             */
            alternate.replaceWith(alternate.get('body')[0]);
            value.scope.crawl();
            this.checkAndTransformToIfElse(value)
            return true;
        }else if (consequent.isBlockStatement() && consequent.get('body').length === 1
            && consequent.get('body')[0].isIfStatement() && value.node.alternate != null){
            /**
             * if(test){
             *     if(test2){}
             * }else{
             *     ...
             * }
             */
            const innerIfStatement = consequent.get('body')[0] as NodePath<TYPE.IfStatement>;
            this.invertTestExpression(test);
            consequent.replaceWith(alternate);
            alternate.replaceWith(innerIfStatement);
            this.checkAndTransformToIfElse(value)
            return true;
        }else if (consequent.isBlockStatement() && alternate.isBlockStatement()
            && test.isBinaryExpression()){
            if (test.node.operator === '!=='){
                test.node.operator = '===';
            }else if (test.node.operator === '!='){
                test.node.operator = '==';
            }else return false;

            const temp = alternate.node;
            alternate.replaceWith(consequent);
            consequent.replaceWith(temp);
            this.checkAndTransformToIfElse(value);
            return true;
            //remove empty alternate block
        }else if (alternate.isBlockStatement() && alternate.node.body.length == 0){
            value.node.alternate = null;
            this.checkAndTransformToIfElse(value);
            return true;
            //transform empty consequent block
        }else if (consequent.isBlockStatement() && consequent.node.body.length == 0 && alternate.isBlockStatement()){
            value.node.test = this.InvertExpressionRecursive(test.node);
            value.get('consequent').replaceWith(alternate)
            value.node.alternate = null
            value.scope.crawl();
            this.checkAndTransformToIfElse(value);
            return true;
        }
        return false;
    }
    simplify(node: Program): void {
        console.log("do RewriteSimplify")
        traverse(TYPE.file(node), {
            //simplify test node of IfStatement
            IfStatement:(value)=>{
              this.simplifyTestExpression(value.get('test'));

              //transform to if-else
              if (this.checkAndTransformToIfElse(value)){
                  value.scope.crawl();
              }
            },
            //simplify update node
            UpdateExpression:(value)=>{
              const parent = value.getStatementParent();
              if (TYPE.isExpressionStatement(parent.node) && TYPE.isAssignmentExpression(parent.node.expression)
                  && parent.node.expression.right === value.node){
                  const left = parent.node.expression.left;
                  const right = value.get('argument').node;
                  if (TYPE.isIdentifier(left) && TYPE.isIdentifier(right) && left.name === right.name)
                    parent.replaceWith(parent.node.expression.right)
              }
            },
            //simplify duplicate undefined
            BlockStatement:(value)=>{
                if(this.duplicateAccUndefined(value)){
                    value.scope.crawl();
                }
            },
            //simplify tail return undefined
            Function:(value)=>{
                const body = value.get('body');
                if (body.type === 'BlockStatement' && this.removeFunctionReturnUndefined(body as NodePath<TYPE.BlockStatement>)){
                    value.scope.crawl();
                }
            },
            //simplify member expression
            MemberExpression:(value)=>{
                // const property = value.get('property');
                // if (TYPE.isStringLiteral(property.node) && isNaN(Number(property.node.value))){
                //     property.replaceWith(TYPE.identifier(property.node.value));
                //     value.node.computed = false;
                // }
                if (TYPE.isIdentifier(value.node.object) && value.node.object.name === 'arguments'
                && TYPE.isNumericLiteral(value.node.property)){
                    const node = value.getFunctionParent();
                    const paramsLength = node.node.params.length;
                    const memNum = value.node.property.value;
                    if (memNum < paramsLength){
                        value.replaceWith(TYPE.identifier('p'+memNum))
                        value.scope.crawl();
                    }
                }
            },
            //simplify call expression
            CallExpression:(value)=>{
                this.changeCallExpression(value);
            },
            FunctionExpression:(value)=>{
                const node = value.getStatementParent();
                if (node.isExpressionStatement() && node.get('expression') === value){
                    node.replaceWith(TYPE.functionDeclaration(value.node.id,value.node.params,value.node.body,value.node.generator,value.node.async))
                    value.scope.crawl()
                }
            },
            UnaryExpression:(value)=>{
                if (value.node.operator === '!' && TYPE.isNumericLiteral(value.node.argument)){
                    if (value.node.argument.value === 0){
                        value.replaceWith(TYPE.booleanLiteral(true));
                    }else if (value.node.argument.value === 1){
                        value.replaceWith(TYPE.booleanLiteral(false));
                    }
                }
            },
            SpreadElement:(value)=>{
                if (TYPE.isArrayExpression(value.node.argument) && value.node.argument.elements.length == 1){
                    value.replaceWith(value.node.argument.elements[0]);
                    value.scope.crawl();
                }
            }
        });
    }

}