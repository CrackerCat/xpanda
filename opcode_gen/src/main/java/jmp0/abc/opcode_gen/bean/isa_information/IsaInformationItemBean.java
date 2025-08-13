package jmp0.abc.opcode_gen.bean.isa_information;

import lombok.Data;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Data
public final class IsaInformationItemBean {
    private String description;
    private int last_opcode_idx;
    private int last_throw_prefixed_opcode_idx;
    private int last_wide_prefixed_opcode_idx;
    private int last_deprecated_prefixed_opcode_idx;
    private int last_callruntime_prefixed_opcode_idx;
}
