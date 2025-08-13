package jmp0.abc.opcode_gen.bean;

import jmp0.abc.opcode_gen.bean.chapters.ChapterItemBean;
import jmp0.abc.opcode_gen.bean.exceptions.ExceptionItemBean;
import jmp0.abc.opcode_gen.bean.groups.GroupItemBean;
import jmp0.abc.opcode_gen.bean.isa_information.IsaInformationItemBean;
import jmp0.abc.opcode_gen.bean.prefixes.PrefixItemBean;
import jmp0.abc.opcode_gen.bean.properties.PropertyItemBean;
import jmp0.abc.opcode_gen.bean.verification.VerificationItemBean;
import lombok.Data;

import java.util.List;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Data
public final class PandaIsaBean {
    private List<ChapterItemBean> chapters;
    private String min_version;
    private String version;
    private List<List<String>> api_version_map;
    private List<String> incompatible_version;
    private List<PropertyItemBean> properties;
    private List<ExceptionItemBean> exceptions;
    private List<VerificationItemBean> verification;
    private List<IsaInformationItemBean> isa_information;
    private List<PrefixItemBean> prefixes;
    private List<GroupItemBean> groups;
}
