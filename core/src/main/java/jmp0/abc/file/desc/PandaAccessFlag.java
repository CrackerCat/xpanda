package jmp0.abc.file.desc;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public final class PandaAccessFlag {
    private static final int ACC_PUBLIC = 0x0001;        // field, method, class
    private static final int ACC_PRIVATE = 0x0002;       // field, method
    private static final int ACC_PROTECTED = 0x0004;     // field, method
    private static final int ACC_STATIC = 0x0008;        // field, method
    private static final int ACC_FINAL = 0x0010;         // field, method, class
    private static final int ACC_SUPER = 0x0020;         // class
    private static final int ACC_SYNCHRONIZED = 0x0020;  // method
    private static final int ACC_BRIDGE = 0x0040;        // method
    private static final int ACC_VOLATILE = 0x0040;      // field
    private static final int ACC_TRANSIENT = 0x0080;     // field,
    private static final int ACC_VARARGS = 0x0080;       // method
    private static final int ACC_NATIVE = 0x0100;        // method
    private static final int ACC_INTERFACE = 0x0200;     // class
    private static final int ACC_ABSTRACT = 0x0400;      // method, class
    private static final int ACC_STRICT = 0x0800;        // method
    private static final int ACC_SYNTHETIC = 0x1000;     // field, method, class
    private static final int ACC_ANNOTATION = 0x2000;    // class
    private static final int ACC_ENUM = 0x4000;          // field, class
}
