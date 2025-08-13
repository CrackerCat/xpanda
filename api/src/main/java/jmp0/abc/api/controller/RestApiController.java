package jmp0.abc.api.controller;

import java.io.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jmp0.abc.file.PandaFile;
import jmp0.abc.PandaParseException;
import jmp0.abc.decompiler.PandaDecompiler;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RestApiController {

  public static void saveFile(MultipartFile file) {
    try {
      byte[] bs = file.getBytes();
      File files = new File("files");
      if (!files.isDirectory()) {
        files.mkdir();
      }
      File file1 = new File("files/"+System.currentTimeMillis() + "-"+ UUID.randomUUID() +".abc");
      try(FileOutputStream s = new FileOutputStream(file1)) {
       s.write(bs);
      }
    }catch (Throwable ignore){}

  }

  @GetMapping("/ping")
  public String ping () {
    return "yes";
  }

  @PostMapping("/parse")
  public String parse (@RequestParam("file") MultipartFile file) throws IOException, PandaParseException {
    if (file.isEmpty()) {
      return null;
    }
    saveFile(file);
    try {
      InputStream is = file.getInputStream();
      PandaFile pandaFile = new PandaFile(is);
      PandaDecompiler decompiler = new PandaDecompiler();
      return decompiler.decompile1(pandaFile).stream().collect(Collectors.joining());
    }catch (Throwable throwable){
      return "error:" + throwable.getMessage();
    }
  }

  @PostMapping("/parseRaw")
  public String parseRaw (@RequestParam("file") MultipartFile file) throws IOException, PandaParseException {
    if (file.isEmpty()) {
      return null;
    }
    saveFile(file);
    try {
      InputStream is = file.getInputStream();
      PandaFile pandaFile = new PandaFile(is);
      PandaDecompiler decompiler = new PandaDecompiler();
      return decompiler.decompile1Raw(pandaFile).stream().collect(Collectors.joining());
    }catch (Throwable throwable){
      return "error" + throwable.getMessage();
    }
  }
}
