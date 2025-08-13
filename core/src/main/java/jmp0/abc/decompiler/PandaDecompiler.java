package jmp0.abc.decompiler;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jmp0.abc.Pair;
import jmp0.abc.decompiler.simple.SimpleAnalysis;
import jmp0.abc.decompiler.structure.StructureAnalysis;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.codegen.JSBridge;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.clazz.PandaClassExportModule;
import jmp0.abc.file.clazz.PandaClassImportModule;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.file.method.PandaMethod;
import jmp0.abc.util.PandaLogger;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaDecompiler {
    private final PandaLogger logger = new PandaLogger(PandaDecompiler.class);
    private final CodeGenerator codeGenerator;
    private final PandaDecompilerMethodHandler methodHandler;
    private final File simplifyJSFile = File.createTempFile("PandaDecompiler",".js");

    public PandaDecompiler() throws IOException {
        this.codeGenerator = new CodeGenerator(new JSBridge());
        this.methodHandler = new PandaDecompilerMethodHandler(this.codeGenerator);
        preBuildSimplify();
    }

    private String getByteNodeName(){
        String name = System.getProperty("os.name");
        String bytenodeName = "bytenode";
        if(name.contains("Windows")){
            bytenodeName += ".cmd";
        }
        return bytenodeName;
    }

    private void preBuildSimplify() throws IOException {
        InputStream cmdData = this.getClass().getClassLoader().getResource("cmd.js").openStream();
        byte[] data = cmdData.readAllBytes();
        cmdData.close();
        FileOutputStream outputStream = new FileOutputStream(simplifyJSFile);
        outputStream.write(data);
        outputStream.close();
        simplifyJSFile.deleteOnExit();
    }

    private void decompileSingleFunction(PandaIRCFG pandaIRCFG,PandaLexical pandaLexical, Object programNode){
        methodHandler.insertFunctionPreamble(pandaIRCFG.getPandaMethod(),pandaLexical,programNode);
        try {
            new StructureAnalysis(pandaIRCFG,pandaIRCFG.getEntryBlock(),pandaIRCFG.getLastBlock()).analysis(methodHandler,pandaLexical,programNode);
        }catch (Exception exception){
            logger.logD("decompileFunction","failed! use SimpleAnalysis");
            new SimpleAnalysis(pandaIRCFG).analysis(methodHandler,pandaLexical,programNode);
        }
    }

    public String simplify(String content) {
        Process process;
        try {
            File tempFile = File.createTempFile("simplify",".js");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
            ProcessBuilder processBuilder = new ProcessBuilder("node" , this.simplifyJSFile.getAbsolutePath() , tempFile.getAbsolutePath());;
            processBuilder.redirectErrorStream(true);
//            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            process = processBuilder.start();
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignore) {}
            }).start();
            if (process.waitFor() != 0) {
                tempFile.delete();
                return "";
            }
            FileInputStream fileInputStream = new FileInputStream(tempFile);
            String simplyCode = new String(fileInputStream.readAllBytes());
            fileInputStream.close();
            tempFile.delete();
            return simplyCode;
        } catch (IOException | InterruptedException e) {
            return "";
        }
    }

    private String decompileRaw(PandaClass pandaClass){
        PandaLexical pandaLexical = new PandaLexical(pandaClass);
        HashMap<String, PandaMethod> maps= pandaClass.getPandaMethods();
        PandaMethod funcMain0Method = maps.get(PandaFile.ENTRY_FUNCTION_MAIN);
        if (funcMain0Method == null) return "//no function main...";
        Object programNode = codeGenerator.getNodeFactory().programNode();
        //dis_compile imports
        if (pandaClass.getPandaClassModuleData() != null){
            PandaClassImportModule[] imports = pandaClass.getPandaClassModuleData().getImports();
            for (PandaClassImportModule anImport : imports) {
                Object importNode = null;
                switch (anImport.getType()){
                    case SPECIFIER:
                    case SAME_SPECIFIER:
                        importNode = codeGenerator.getNodeFactory().importDeclarationNode(
                                codeGenerator.getNodeFactory().importSpecifierNode(anImport.getLocalName(), anImport.getImportName()),
                                anImport.getPath());
                        break;
                    case NAMESPACE:
                        importNode = codeGenerator.getNodeFactory().importDeclarationNode(
                                codeGenerator.getNodeFactory().importNamespaceSpecifierNode(anImport.getLocalName()),
                                anImport.getPath());
                        break;
                    case DEFAULT:
                        importNode = codeGenerator.getNodeFactory().importDeclarationNode(
                                codeGenerator.getNodeFactory().importDefaultSpecifierNode(anImport.getLocalName()),
                                anImport.getPath());
                        break;
                }
                codeGenerator.getNodeUtils().insertNodeToBody(importNode,programNode);
            }

        }
        //dis_compile func_main_0;
        //head comment string
        StringBuilder builder = new StringBuilder();
        builder.append("\n* class name:").append(pandaClass.getName().getContent()).append('\n')
                        .append("* Source code created from a .abc file.").append('\n')
                                .append("* powered by xpanda decompiler, author: jmp0\n");

        if (pandaClass.getPandaClassModuleData() != null){
            Set<String> nameSet = new HashSet<String>();
            for (PandaClassExportModule export : pandaClass.getPandaClassModuleData().getExports()) {
                if (export.getLocalName() != null){
                    nameSet.add(export.getLocalName());
                }
            }
            if (!nameSet.isEmpty()){
                Object arrayObject = codeGenerator.arrayObject();
                for (String s : nameSet) {
                    codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().variableDeclaratorNode(codeGenerator.getNodeFactory().identifierNode(s)));
                }
                Object variableDeclarationNode = codeGenerator.getNodeFactory().variableDeclarationNode(arrayObject,"let");
                codeGenerator.getNodeUtils().insertNodeToBody(variableDeclarationNode,programNode);
            }
        }
        codeGenerator.getNodeFactory().addComment(programNode,builder.toString());
        PandaIRCFG pandaIRCFG = pandaLexical.getPandaIRCFG(funcMain0Method);
        decompileSingleFunction(pandaIRCFG,pandaLexical,programNode);
        //dis_compile exports
        if (pandaClass.getPandaClassModuleData() != null){
            PandaClassExportModule[] exports = pandaClass.getPandaClassModuleData().getExports();
            for (PandaClassExportModule export : exports) {
                Object exportNode = null;
                switch (export.getType()){
                    case ALL:
                        exportNode = codeGenerator.getNodeFactory().exportAllDeclarationNode(export.getPath());
                        break;
                    case INDIRECT:
                        if (export.getExportName().equals("default")){
                            exportNode = codeGenerator.getNodeFactory().exportNamedDeclarationNode(
                                    codeGenerator.getNodeFactory().exportDefaultSpecifierNode(export.getLocalName()),export.getPath());
                        }else{
                            exportNode = codeGenerator.getNodeFactory().exportNamedDeclarationNode(
                                    codeGenerator.getNodeFactory().exportSpecifierNode(export.getLocalName(),export.getExportName()),export.getPath());
                        }
                        break;
                    case LOCAL:{
                        if (export.getExportName().equals("default")){
                            exportNode = codeGenerator.getNodeFactory().exportDefaultDeclarationNode(export.getLocalName());
                        }else{
                            exportNode = codeGenerator.getNodeFactory().exportNamedDeclarationNode(
                                    codeGenerator.getNodeFactory().exportSpecifierNode(export.getLocalName(),export.getExportName()),export.getPath());
                        }
                        break;
                    }

                }
                codeGenerator.getNodeUtils().insertNodeToBody(exportNode,programNode);
            }
        }
        //simplify
//        codeGenerator.getNodeUtils().compare(programNode);
//        String content = codeGenerator.getNodeFactory().generate(programNode);
        return codeGenerator.getNodeUtils().toJson(programNode);
    }

    public void decompile(PandaFile pandaFile, IDecompileCallback callback){
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/2);
        Queue<PandaClass> pandaClassQueue = new LinkedList<>();
        for (IndexHeader indexHeader : pandaFile.getIndexHeaders()) {
            Collections.addAll(pandaClassQueue, indexHeader.getPandaClasses());
        }
        CountDownLatch latch = new CountDownLatch(pandaClassQueue.size());
        Flowable.fromIterable(pandaClassQueue)
                .map((pandaClass)->{
//                    System.out.println("generate：" + Thread.currentThread().getName() + ":" + pandaClass.getName());
                    String out = decompileRaw(pandaClass);
                    if (out.isEmpty()){
                        return new Pair<PandaClass,String>(pandaClass,null);
                    }
                    return new Pair<PandaClass,String>(pandaClass,out);
                })
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.trampoline())
                .doOnNext((pair)->{
                    //                    Object object = codeGenerator.getNodeUtils().toObject(pair.getSecond());
//                    String content = codeGenerator.getNodeFactory().generate(object);
//                    callback.onDecompileComplete(pair.getFirst(), IDecompileCallback.STATUS.SUCCESS,content);
//                    latch.countDown();
                    if (pair.getSecond() == null){
                        callback.onDecompileComplete(pair.getFirst(), IDecompileCallback.STATUS.NO_MAIN,"");
                        latch.countDown();
                    }else executorService.execute(new Runnable() {
                        @Override
                        public void run() {

                            String out = simplify(pair.getSecond());
                            if (out.isEmpty()){
//                                System.out.println("simplify：" + Thread.currentThread().getName() + ":" + pair.getFirst().getName());
                                callback.onDecompileComplete(pair.getFirst(), IDecompileCallback.STATUS.DECOMPILE_FAILED,"");
                                latch.countDown();
                            } else{
//                                System.out.println("simplify：" + Thread.currentThread().getName() + ":" + pair.getFirst().getName());
                                callback.onDecompileComplete(pair.getFirst(), IDecompileCallback.STATUS.SUCCESS,out);
                                latch.countDown();
                            }
                        }
                    });
                })
                .subscribe();
        try {
            latch.await();
        } catch (InterruptedException ignore) {}
    }

    public String decompileClassRaw(PandaClass pandaClazz){
        String out = decompileRaw(pandaClazz);
        if (out.isEmpty())return "";
        if (out.equals("//no function main...")) return "";
        Object object = codeGenerator.getNodeUtils().toObject(out);
        return codeGenerator.getNodeFactory().generate(object);
    }

    public String decompileClass(PandaClass pandaClazz){
        String out = decompileRaw(pandaClazz);
        if (out.isEmpty())return "";
        return simplify(out);
    }

    public List<String> decompile1(PandaFile pandaFile){
        Queue<PandaClass> pandaClassQueue = new LinkedList<>();
        for (IndexHeader indexHeader : pandaFile.getIndexHeaders()) {
            Collections.addAll(pandaClassQueue, indexHeader.getPandaClasses());
        }
        List<String> results = new LinkedList<>();
        for (PandaClass pandaClass : pandaClassQueue) {
            String out = decompileClass(pandaClass);
            if (out.isEmpty()) continue;
            String builder = "\n/***************************" +
                    pandaClass.getName().getContent() +
                    "*************************************/\n" +
                    out;
            results.add(builder);
        }
        return results;
    }

    public List<String> decompile1Raw(PandaFile pandaFile){
        Queue<PandaClass> pandaClassQueue = new LinkedList<>();
        for (IndexHeader indexHeader : pandaFile.getIndexHeaders()) {
            Collections.addAll(pandaClassQueue, indexHeader.getPandaClasses());
        }
        List<String> results = new LinkedList<>();
        for (PandaClass pandaClass : pandaClassQueue) {
            String content = decompileClassRaw(pandaClass);
            if (content.isEmpty()) continue;
            String builder = "\n/***************************" +
                    pandaClass.getName().getContent() +
                    "*************************************/\n" +
                    content;
            results.add(builder);
        }
        return results;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveFile(File outDir, PandaClass pandaClass,String content) {
        try {
            String path = pandaClass.getName().getContent();
            if (path.isEmpty()) return;
            String[] paths = path.substring(1,path.length()-1).split("/");
            File outFile;
            if (paths.length == 1){
                outFile = new File(outDir,paths[0] + ".js");
                outFile.createNewFile();
            }else {
                String name = paths[paths.length-1];
                StringBuilder realPath = new StringBuilder(File.separator);
                for (int i = 0; i < paths.length-1; i++) {
                    realPath.append(paths[i]).append(File.separator);
                }
                File oo = new File(outDir,realPath.toString());
                oo.mkdirs();
                outFile = new File(oo,name+".js");
                outFile.createNewFile();
            }
            FileWriter writer = new FileWriter(outFile);
            writer.write(content);
            writer.close();
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
