package jmp0.abc.disasm;

import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.clazz.PandaClassExportModule;
import jmp0.abc.file.clazz.PandaClassImportModule;
import jmp0.abc.file.method.PandaMethod;
import jmp0.abc.file.type.PandaRawType;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaDisAssemblerFakeCodeHelper {
    private static String getPandaMethodSignature(PandaMethod pandaMethod){
        StringBuilder builder = new StringBuilder();
        int nums = pandaMethod.getMethodCode().getNumArgs().intValue();
        //String returnType = (pandaMethod.getPandaProto().getPandaRawTypes()[nums]).getTypeString(false);
        String returnType = "any";
        builder.append(pandaMethod.getName().getContent());
        builder.append('(');
        for (int i = 0; i < nums; i++) {
            if (i > 2){
                builder.append("p").append(i-3);
                builder.append(':').append("any");
                if (i != nums-1){
                    builder.append(',');
                }
            }
        }
        builder.append("):").append(returnType);
        return builder.toString();
    }

    public static String genFakeCode(PandaClass pandaClass, PandaIRCFG[] pandaIRCFGS){
        if (pandaIRCFGS.length == 0) return "";
        StringBuilder builder = new StringBuilder();
        String indentation = "";
        if (pandaClass.getPandaClassModuleData() != null){
            for (PandaClassImportModule anImport : pandaClass.getPandaClassModuleData().getImports()) {
                builder.append(anImport.toString()).append('\n');
            }
        }
        builder.append("class ").append(pandaClass.getName().getContent()).append("{\n");
        indentation = "\t";
        for (PandaIRCFG pandaIRCFG : pandaIRCFGS) {
            PandaMethod pandaMethod = pandaIRCFG.getPandaMethod();
            builder.append(indentation).append("function ").append(getPandaMethodSignature(pandaMethod)).append("{\n");
            indentation = "\t\t";
            builder.append(indentation).append("numvReg:").append(pandaMethod.getMethodCode().getNumVregs()).append('\n');
            String blockString = pandaIRCFG.toString();
            String[] ls = blockString.split("\n");
            for (String l : ls) {
                builder.append(indentation).append(l).append('\n');
            }
            indentation = "\t";
            builder.append(indentation).append("}\n");
        }


        builder.append("}").append("\n");
        if (pandaClass.getPandaClassModuleData() != null){
            for (PandaClassExportModule anImport : pandaClass.getPandaClassModuleData().getExports()) {
                builder.append(anImport.toString()).append('\n');
            }
        }
        return builder.toString();
    }
}
