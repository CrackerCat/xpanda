package jmp0.abc.opcode_gen.bean.groups;

import lombok.Data;

import java.util.List;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Data
public final class InstructionItemBean {
    private String sig;
    private String acc;
    private List<Integer> opcode_idx;
    private List<String> format;
    private String prefix;
    private List<String> properties;
    private List<String> exceptions;
}
