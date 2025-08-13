package jmp0.abc.opcode_gen.bean.groups;

import lombok.Data;

import java.util.List;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Data
public final class GroupItemBean {
    private String title;
    private String description;
    private List<String> verification;
    private List<String> exceptions;
    private List<String> properties;
    private String namespace;
    private String pseudo;
    private String semantics;
    private List<InstructionItemBean> instructions;
}
