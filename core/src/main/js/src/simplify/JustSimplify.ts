import { Program } from "@babel/types";
import {ISimplify} from "./ISimplify";
import traverse, {Binding, NodePath} from "@babel/traverse";
import * as TYPE from "@babel/types";
import generate from "@babel/generator";

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
export class JustSimplify implements ISimplify {
    private index = 0;
    private static CONDITIONAL_BLOCK_NAME = "JustSimplify_id_next";

    private isIteratorNextNode(node:TYPE.Node){
        if (TYPE.isMemberExpression(node) && TYPE.isCallExpression(node.object) && generate(node).code.endsWith('.next().value')){
            return true;
        }
        return false;
    }

    private isContainClassNode(node:TYPE.Node):boolean{
        if (TYPE.isClassExpression(node)) return true;
        return !!(TYPE.isMemberExpression(node) && TYPE.isClassExpression(node.object));

    }

    private isKeepNode(node:TYPE.Node){
        return this.isContainClassNode(node) || TYPE.isClassExpression(node) || TYPE.isLiteral(node) || TYPE.isArrayExpression(node) || TYPE.isObjectExpression(node) ||
             TYPE.isNewExpression(node) || (TYPE.isIdentifier(node) && node.name === "undefined")||
            this.isIteratorNextNode(node)
    }

    private isLeftAcc(path:NodePath<TYPE.Statement>){
        return TYPE.isExpressionStatement(path.node)
        && TYPE.isAssignmentExpression(path.node.expression)
        && this.isIdentifierName(path.node.expression.left,"acc")
    }

    private isRightAcc(path:NodePath<TYPE.Statement>){
        return TYPE.isExpressionStatement(path.node)
            && TYPE.isAssignmentExpression(path.node.expression)
            && this.isIdentifierName(path.node.expression.right,"acc")
    }

    private generateNextID(){
        return JustSimplify.CONDITIONAL_BLOCK_NAME + ++this.index;
    }

    private getIdentifiers(path:NodePath<TYPE.Node>):Set<string>{
        let names = new Set<string>();
        traverse(path.node,{
            Identifier:(value)=>{
                names.add(value.node.name);
            }
        },path.scope)
        return names;
    }

    private isIdentifierName(node:TYPE.Node,name:string){
        return TYPE.isIdentifier(node) && node.name === name;
    }

    private isInCondition(referencedNode:[number,NodePath<TYPE.Statement>]):boolean{
        const referenceNodeStatement = referencedNode[1].node;
        return TYPE.isIfStatement(referenceNodeStatement) ||
        TYPE.isWhileStatement(referenceNodeStatement) ||
        TYPE.isDoWhileStatement(referenceNodeStatement)||
        TYPE.isForStatement(referenceNodeStatement)

    }

    private invalidReference(first:[number,NodePath<TYPE.Statement>],
                             second:[number,NodePath<TYPE.Statement>]|null,
                             referenceArr:Array<[number,NodePath<TYPE.Statement>]>,
                             referencePathsMap:Map<number,Array<NodePath<TYPE.Node>>>
    ):Array<number>{
        const statement = first[1];
        const left = (<NodePath<TYPE.AssignmentExpression>>statement.get('expression')).get('left');
        const right = (<NodePath<TYPE.AssignmentExpression>>statement.get('expression')).get('right');
        const res = referenceArr.map((value)=>{return value[0]});
        if (res.length == 1){
            const ref = referenceArr[0];
            if ((second === null && this.isLeftAcc(first[1]) ) || !this.isInCondition(ref) ) return res;
            // if (this.isLeftAcc(first[1]) && this.isLeftAcc(second[1]) && this.isRightAcc(ref[1])) return res;
        }
        for (let referenceArrElement of referenceArr) {
            if (second !== null && this.isInCondition(referenceArrElement)) return []
        }
        if(TYPE.isLiteral(right.node) || this.isIdentifierName(right.node,'undefined')){
            if (second === null) return res;
            if (this.isIdentifierName(left.node,'acc')) return res;
            return [];
        }
        if (this.isKeepNode(right.node)) return [];
        return res;
    }
    private indexOf(body:NodePath<TYPE.Node>[],node:NodePath<TYPE.Node>):[number,NodePath<TYPE.Statement>]{
        let tempNode = node.getStatementParent();
        let index = -1;
        while (true){
            index = body.indexOf(tempNode);
            if (index != -1) return [index,tempNode];
            const temp = tempNode.parentPath;
            if (temp.type === 'Program') return [-1,null];
            tempNode = tempNode.parentPath.getStatementParent();
        }
    }

    private checkRightConstantViolation(first:[number,NodePath<TYPE.Statement>],second:[number,NodePath<TYPE.Statement>]|null,body:NodePath<TYPE.Node>[]):boolean{
        const statement = first[1];
        const right = (<NodePath<TYPE.AssignmentExpression>>statement.get('expression')).get('right');
        let flag = false;
        if(this.isLeftAcc(statement)){
            // if (second == null) return false;
            // if (second && this.isLeftAcc(second[1])) return false;
        }
        const checkFunc = (name:string)=>{
            const binding1 = statement.scope.getBinding(name);
            if (binding1){
                for (let constantViolation of binding1.constantViolations) {
                    const result = this.indexOf(body,constantViolation);
                    if (second == null){
                        if ( result[0] > first[0]) flag = true;
                    } else if ( result[0] > first[0] && result[0] < second[0]){
                        flag = true;
                    }
                }
            }
        }
        if (right.node.type === 'Identifier') checkFunc((right as NodePath<TYPE.Identifier>).node.name);
        else{
            const names = this.getIdentifiers(right);
            for (const value of Array.from(names)) {
                checkFunc(value)
            }
        }
        return flag;
    }

    private checkReferencedBetween(constantViolations:Array<[number,NodePath<TYPE.Statement>]>,
                                   body:NodePath<TYPE.Node>[],
                                   referenceArray:Array<[number,NodePath<TYPE.Statement>]>,
                                   referencePathsMap:Map<number,Array<NodePath<TYPE.Node>>>):Array<number>{
        const res = [];
        const first = constantViolations[0];
        if (!(TYPE.isExpressionStatement(first[1].node) && TYPE.isAssignmentExpression(first[1].node.expression)))
            return []
        if (constantViolations.length === 1){
            const trueReferenced:Array<[number,NodePath<TYPE.Statement>]> = []
            for (let referencePathsArrayElement of referenceArray) {
                if (referencePathsArrayElement[0] > first[0]){
                    trueReferenced.push(referencePathsArrayElement);
                }
            }
            if (trueReferenced.length === 1 && first[0] + 1 === trueReferenced[0][0]){
                if(TYPE.isExpressionStatement(first[1].node) && TYPE.isAssignmentExpression(first[1].node.expression) && !this.isKeepNode(first[1].node.expression.right)){
                    return [trueReferenced[0][0]]
                }

            }
            if (this.checkRightConstantViolation(first,null,body)) return [];
            return this.invalidReference(first,null,trueReferenced,referencePathsMap)
        }
        if (constantViolations.length === 2){
            const second = constantViolations[1];
            if (first[0] + 2 === second[0]){
                const accRef:Array<[number,NodePath<TYPE.Statement>]> = []
                for (let referencePathsArrayElement of referenceArray) {
                    if (referencePathsArrayElement[0] > first[0] && referencePathsArrayElement[0] <= second[0]){
                        accRef.push(referencePathsArrayElement);
                    }
                }
                if (accRef.length === 1 && first[0] + 1 === accRef[0][0]){
                    if(TYPE.isExpressionStatement(first[1].node) && TYPE.isAssignmentExpression(first[1].node.expression) && !this.isKeepNode(first[1].node.expression.right)){
                        return [accRef[0][0]]
                    }
                }
            }
            if (this.checkRightConstantViolation(first,second,body)) return [];
            const trueReferenced:Array<[number,NodePath<TYPE.Statement>]> = []
            for (let referencePathsArrayElement of referenceArray) {
                if (referencePathsArrayElement[0] > first[0] && referencePathsArrayElement[0] <= second[0]){
                    trueReferenced.push(referencePathsArrayElement);
                }
            }
            return this.invalidReference(first,second,trueReferenced,referencePathsMap)
        }
        return res;
    }

    private preCheckReferenceWithReplace(refs:Array<NodePath<TYPE.Node>>,replaced:NodePath<TYPE.Expression>):boolean{
        if (refs[0].isIdentifier() && refs[0].node.name === 'panda_jmp0_reserved_param_p2') return true;
        if (refs.length > 3 && replaced.isLiteral()){
            return false;
        }
        if (replaced.isClassExpression() && refs[0].isIdentifier() && (refs[0].node as TYPE.Identifier).name !== 'acc'){
            return false;
        }
        if (replaced.isAwaitExpression() || replaced.isCallExpression()){
            if (refs.length != 1){
                return false;
            }else if (refs[0].getStatementParent().getPrevSibling() != replaced.getStatementParent()){
                return false;
            }
        }
        if (this.isIteratorNextNode(replaced.node)){
            return false;
        }
        if (TYPE.isCallExpression(replaced.node) && TYPE.isSuper(replaced.node.callee) && (refs[0].node as TYPE.Identifier).name !== 'acc'){
            return false;
        }
        for (let ref of refs) {
            if (ref.getStatementParent().isExportNamedDeclaration()) return false;
            if (ref.getStatementParent().scope !== replaced.getStatementParent().scope){
                if (!replaced.isNumericLiteral()) return false;
            }
        }
        return true
    }

    private checkAndSimplifySingle(constantViolation:[number,NodePath<TYPE.Statement>],
                                   body:NodePath<TYPE.Node>[],
                                   referencePathsArray:Array<[number,NodePath<TYPE.Statement>]>,
                                   referencePathsMap:Map<number,Array<NodePath<TYPE.Node>>>,
                                   binding:Binding){
        let res = Array.from(new Set(this.checkReferencedBetween([constantViolation],body,referencePathsArray,referencePathsMap)));
        if (res.length < 1) return false;
        const right = (<NodePath<TYPE.AssignmentExpression>>constantViolation[1].get('expression')).get('right');
        const refs:Array<NodePath<TYPE.Node>> = [];
        for (let re of res) {
            for (let x of referencePathsMap.get(re)) {
                refs.push(x);
            }
        }
        if (!this.preCheckReferenceWithReplace(refs,right)) return false;
        console.log("handleOneBinding [only one constantViolation]" + binding.identifier.name + " " + constantViolation[0]);
        for (let ref of refs) {
            ref.replaceWith(right)
        }
        constantViolation[1].remove();
        return true;
    }

    private handleOneBinding(block:NodePath<TYPE.BlockStatement>|NodePath<TYPE.Program>,binding:Binding):boolean{
        if (binding.identifier === null) return false;
        const constantViolations = binding.constantViolations;
        const referencePaths = binding.referencePaths;
        const constantViolationsArray:Array<[number,NodePath<TYPE.Statement>]> = []
        const constantViolationsMap:Map<number,Array<NodePath<TYPE.Node>>> = new Map();
        const referencePathsArray:Array<[number,NodePath<TYPE.Statement>]> = [];
        const referencePathsMap:Map<number,Array<NodePath<TYPE.Node>>> = new Map();
        const body = <NodePath<TYPE.Node>[]>block.get('body');
        //build constantViolation Map
        const sortFunction = function (a:[number,NodePath<TYPE.Statement>],b:[number,NodePath<TYPE.Statement>]){
            return a[0] - b[0];
        }
        for (const constantViolation of constantViolations) {
            const [index,statement] = this.indexOf(body,constantViolation);
            if (index === -1)
                continue;
            constantViolationsArray.push([index,statement]);
            if (constantViolationsMap.get(index) == null){
                constantViolationsMap.set(index,[constantViolation]);
            }else {
                constantViolationsMap.set(index,constantViolationsMap.get(index).concat(constantViolation));
            }

        }
        constantViolationsArray.sort(sortFunction);
        for (const reference of referencePaths) {
            if (!(<NodePath<TYPE.Identifier>>reference).node) return false;
            const name = (<NodePath<TYPE.Identifier>>reference).node.name;
            if (name !== binding.identifier.name)
                continue;
            const [index,statement] = this.indexOf(body,reference);
            if (index === -1)
                continue;
            referencePathsArray.push([index,statement]);
            if (referencePathsMap.get(index) == null){
                referencePathsMap.set(index,[reference]);
            }else {
                referencePathsMap.set(index,referencePathsMap.get(index).concat(reference));
            }
        }
        if (constantViolationsArray.length === 0 || referencePathsArray.length === 0){
            // console.log("handleOneBinding ignore:",binding.identifier.name);
            return;
        }
        referencePathsArray.sort(sortFunction);
        if (constantViolationsArray.length === 1){
            const constantViolation = constantViolationsArray[0];
            if (this.checkAndSimplifySingle(constantViolation,body,referencePathsArray,referencePathsMap,binding))
                return true;
        }
        //two constantViolation and one right reference
        if (constantViolationsArray.length > 1){
            for (let i = 0; i < constantViolationsArray.length - 1; i++) {
                let res = Array.from(new Set(this.checkReferencedBetween([constantViolationsArray[i],constantViolationsArray[i + 1]],body,referencePathsArray,referencePathsMap)));
                if (res.length === 0) continue;
                const right = (<NodePath<TYPE.AssignmentExpression>>constantViolationsArray[i][1].get('expression')).get('right');
                if (((TYPE.isLiteral(right.node)  || TYPE.isIdentifier(right.node)) || TYPE.isMemberExpression(right.node) || res.length === 1)){
                    const func = block.getFunctionParent();
                    const refs:Array<NodePath<TYPE.Node>> = [];
                    for (let re of res) {
                        for (let x of referencePathsMap.get(re)) {
                            refs.push(x);
                        }
                    }
                    if (!this.preCheckReferenceWithReplace(refs,right)) return false;
                    console.log("handleOneBinding [two constantViolation]" + binding.identifier.name + " " + constantViolationsArray[i][0] + " " + constantViolationsArray[i+1][0]);
                    for (let ref of refs) {
                        ref.replaceWith(right)
                    }
                    constantViolationsArray[i][1].remove();
                    return true;
                }
            }
            if (this.checkAndSimplifySingle(constantViolationsArray[constantViolationsArray.length - 1],body,referencePathsArray,referencePathsMap,binding))
                return true;
        }
        return false;
    }

    private handleIt(value:NodePath<TYPE.BlockStatement>|NodePath<TYPE.Program>){
        let result = false;
        while (true){
            const bindings = value.scope.getAllBindings();
            const condition_block_name = value.node.extra?.seq;
            if (condition_block_name && (condition_block_name as String).startsWith(JustSimplify.CONDITIONAL_BLOCK_NAME)
                && (condition_block_name as String).endsWith("left")){
                //sequence expression only handle acc!
                if (bindings["acc"]) result = this.handleOneBinding(value,bindings["acc"])
                else return;
            }else {
                for (let bindingsKey in bindings) {
                    result = this.handleOneBinding(value,bindings[bindingsKey])
                    if (result) break;
                }
            }
            if (!result) break;
            else {
                // console.log(generate(value.node).code)
                value.scope.crawl();
            }
        }
    }

    private generateSequenceHelperBlock(node:NodePath<TYPE.SequenceExpression>):TYPE.BlockStatement{
        const helperBlock= TYPE.blockStatement([]);
        for (let expression of node.get('expressions')) {
            helperBlock.body.push(TYPE.expressionStatement(expression.node));
        }
        return helperBlock;
    }

    private insertSequenceHelperBlock(block:TYPE.BlockStatement,value:NodePath<TYPE.Statement>,seq:NodePath<TYPE.SequenceExpression>){
        const parent = seq.parent as TYPE.LogicalExpression;
        const append = parent.left === seq.node ? "left" : "right";
        const name = this.generateNextID() + "_" + append;
        if (!block.extra) block.extra = {}
        block.extra.seq = name;
        value.insertBefore(block);
        seq.replaceWith(TYPE.identifier(name));
    }

    private getTestExpressionFromBlock(node:NodePath<TYPE.BlockStatement>):TYPE.Expression{
        const body = node.get('body');
        if (body.length === 1){
            return (body[0] as NodePath<TYPE.ExpressionStatement>).get('expression').node;
        }else {
            const seq = TYPE.sequenceExpression([]);
            for (let bodyElement of body) {
                seq.expressions.push((bodyElement as NodePath<TYPE.ExpressionStatement>).get('expression').node);
            }
            return seq;
        }
    }

    private getTestExpressionFromBlockLeft(node:NodePath<TYPE.BlockStatement>):[TYPE.Expression,Array<TYPE.Statement>]{
        // such as shape if((a,b,c,d) || e) ,can be transformed to a;b;c; if(d || e)
        const body = node.get('body');
        if (body.length === 1)
            return [this.getTestExpressionFromBlock(node),[]]
        else {
            const seq = TYPE.sequenceExpression([]);
            const res:Array<TYPE.Statement> = []
            for (let i = 0; i < body.length - 1; i++) {
                res.push(body[i].node)
            }
            return [(body[body.length - 1] as NodePath<TYPE.ExpressionStatement>).get('expression').node,res]
        }

    }

    private handleCore(node:Program){
        traverse(TYPE.file(node), {
            BlockStatement:(value)=>{
                this.handleIt(value);
            },
            Program:(value)=>{
                this.handleIt(value);
            }
        });
    }

    private insertSequenceHelperBlockRecursive(value:NodePath<TYPE.Statement>,node:NodePath<TYPE.LogicalExpression>){
        let flag = false;
        traverse(node.node,{
            SequenceExpression:(seq)=>{
                const block = this.generateSequenceHelperBlock(seq);
                traverse(block,{
                    LogicalExpression:(logic)=>{
                        //exclude inner function
                        if (!block.body.includes(logic.getStatementParent().node))
                            return;
                        const node = logic.getStatementParent().getNextSibling();
                        if (TYPE.isExpressionStatement(node.node) && TYPE.isIdentifier(node.node.expression)
                                && node.node.expression.name === 'acc'){
                            node.remove();
                        }
                        this.insertSequenceHelperBlockRecursive(value,logic)
                        flag = true;
                    }
                },value.scope)
                this.insertSequenceHelperBlock(block,value,seq)
                flag = true;
            }
        },value.scope);
        if (flag){
            value.scope.crawl();
        }
    }

    simplify(node: Program): void {
        console.log("do JustSimplify")
        // simplify create helper block of sequence expression
        traverse(TYPE.file(node),{
            IfStatement:(value)=>{
                if (value.get('test').isLogicalExpression()){
                    this.insertSequenceHelperBlockRecursive(value,value.get('test') as NodePath<TYPE.LogicalExpression>);
                }
            }
        });
        this.handleCore(node)
        //handle if statement test node
        traverse(TYPE.file(node),{
            IfStatement:(value)=>{
                const test = value.get('test');
                let rr:NodePath<TYPE.Identifier> = null;
                if (this.isIdentifierName(test.node,"acc")){
                    rr = test as NodePath<TYPE.Identifier>;
                }else if (test.node.type === 'UnaryExpression' && this.isIdentifierName(test.node.argument,"acc")){
                    rr = test.get('argument') as NodePath<TYPE.Identifier>;
                }else return;
                const pre = value.getPrevSibling();
                if (TYPE.isExpressionStatement(pre.node) && TYPE.isAssignmentExpression(pre.node.expression) && this.isIdentifierName(pre.node.expression.left,"acc")){
                    const rep= pre.node.expression.right;
                    if (rr){
                        rr.replaceWith(rep)
                        pre.remove();
                        value.scope.crawl();
                    }
                }
            }
        });
        this.handleCore(node);
        // restore helper block of sequence expression
        traverse(TYPE.file(node),{
            Identifier:(value)=>{
                if (value.node.name.startsWith("JustSimplify_id")){
                    const parent = value.getStatementParent();
                    if (parent.isIfStatement()){
                        for (let node of parent.getAllPrevSiblings()) {
                            if (node.isBlockStatement() && node.node.extra && node.node.extra.seq === value.node.name){
                                if (value.node.name.endsWith("left")){
                                    const leftExpPair = this.getTestExpressionFromBlockLeft(node);
                                    node.remove();
                                    value.replaceWith(leftExpPair[0]);
                                    for (const item of leftExpPair[1]) {
                                        parent.insertBefore(item)
                                    }
                                }else {
                                    const rightExp = this.getTestExpressionFromBlock(node);
                                    node.remove();
                                    value.replaceWith(rightExp);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}